/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net.init

import com.google.inject.Inject
import com.valaphee.tesseract.Config
import com.valaphee.tesseract.actor.player.AuthExtra
import com.valaphee.tesseract.actor.player.User
import com.valaphee.tesseract.net.Connection
import com.valaphee.tesseract.net.EncryptionInitializer
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.base.CacheStatusPacket
import com.valaphee.tesseract.net.base.DisconnectPacket
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.WorldPacketHandler
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.security.KeyPair

/**
 * @author Kevin Ludwig
 */
class InitPacketHandler(
    private val worldContext: WorldContext,
    private val connection: Connection
) : PacketHandler {
    @Inject private lateinit var config: Config
    @Inject private lateinit var keyPair: KeyPair

    private var state: State? = null
    private lateinit var authExtra: AuthExtra
    private lateinit var user: User
    private var cacheSupported = false

    override fun initialize() {
        state = State.Handshake
    }

    override fun exceptionCaught(cause: Throwable) {
        log.debug("{}: Caught exception", cause)

        connection.close(DisconnectPacket())
    }

    override fun destroy() {
        if (state != State.Finished) log.warn("{}: Connection failed during {}", this, state)
    }

    override fun other(packet: Packet) {
        log.debug("{}: Unhandled packet: {}", packet)

        connection.close(DisconnectPacket("disconnectionScreen.noReason"))
    }

    override fun login(packet: LoginPacket) {
        if (state != State.Handshake) {
            connection.close(DisconnectPacket("disconnectionScreen.noReason"))

            return
        }

        connection.protocolVersion = packet.protocolVersion
        authExtra = packet.authExtra
        user = packet.user
        if (config.verification && !packet.verified) {
            state = State.Finished

            log.warn("{}: Could not be verified", this)
            connection.close(DisconnectPacket("disconnectionScreen.notAuthenticated"))

            return
        }
        if (!config.userNamePattern.matcher(authExtra.userName).matches()) {
            state = State.Finished

            log.warn("{}: Has an invalid name", this)
            connection.close(DisconnectPacket("disconnectionScreen.invalidName"))

            return
        }

        if (config.encryption) {
            state = State.Encryption

            val encryptionInitializer = EncryptionInitializer(keyPair, packet.publicKey, connection.protocolVersion >= 431)
            connection.write(encryptionInitializer.serverToClientHandshakePacket)
            connection.context.pipeline().addLast(encryptionInitializer)
        } else loginSuccess()
    }

    override fun clientToServerHandshake(packet: ClientToServerHandshakePacket) {
        if (state != State.Encryption) {
            connection.close(DisconnectPacket("disconnectionScreen.noReason"))

            return
        }

        loginSuccess()
    }

    override fun packsResponse(packet: PacksResponsePacket) {
        if (state != State.Packs) {
            connection.close(DisconnectPacket("disconnectionScreen.noReason"))

            return
        }

        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (packet.status) {
            PacksResponsePacket.Status.TransferPacks -> connection.close(DisconnectPacket("disconnectionScreen.resourcePack"))
            PacksResponsePacket.Status.HaveAllPacks -> connection.write(PacksStackPacket(false, emptyArray(), emptyArray(), false, "1.17.11", emptyArray(), false))
            PacksResponsePacket.Status.Completed -> {
                state = State.Finished

                connection.setHandler(WorldPacketHandler(worldContext, connection, authExtra, user, config.caching && cacheSupported))
            }
        }
    }

    override fun cacheStatus(packet: CacheStatusPacket) {
        cacheSupported = packet.supported
    }

    override fun toString() = if (this::authExtra.isInitialized) authExtra.userName else "${connection.address}"

    private fun loginSuccess() {
        connection.write(StatusPacket(StatusPacket.Status.LoginSuccess))

        state = State.Packs

        connection.write(PacksPacket(false, false, false, emptyArray(), emptyArray()))
    }

    private enum class State {
        Handshake, Encryption, Packs, Finished
    }

    companion object {
        private val log: Logger = LogManager.getLogger(InitPacketHandler::class.java)
    }
}
