/*
 * MIT License
 *
 * Copyright (c) 2021, Valaphee.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.valaphee.tesseract.net.init

import com.google.inject.Inject
import com.valaphee.tesseract.actor.player.AuthExtra
import com.valaphee.tesseract.actor.player.User
import com.valaphee.tesseract.data.Config
import com.valaphee.tesseract.net.Connection
import com.valaphee.tesseract.net.EncryptionInitializer
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.base.CacheStatusPacket
import com.valaphee.tesseract.net.base.DisconnectPacket
import com.valaphee.tesseract.net.base.ViolationPacket
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
        log.error("$this: Exception caught", cause)

        connection.close(DisconnectPacket())
    }

    override fun destroy() {
        if (state != State.Finished) log.warn("{}: Connection failed during {}", this, state)
    }

    override fun other(packet: Packet) {
        log.warn("{}: Unhandled packet: {}", this, packet)
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
        if (config.listener.verification && !packet.verified) {
            state = State.Finished

            log.warn("{}: Could not be verified", this)
            connection.close(DisconnectPacket("disconnectionScreen.notAuthenticated"))

            return
        }
        if (!config.listener.userNamePattern.matcher(authExtra.userName).matches()) {
            state = State.Finished

            log.warn("{}: Has an invalid name", this)
            connection.close(DisconnectPacket("disconnectionScreen.invalidName"))

            return
        }

        if (config.listener.encryption) {
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

                connection.setHandler(WorldPacketHandler(worldContext, connection, authExtra, user, config.listener.caching && cacheSupported))
            }
        }
    }

    override fun cacheStatus(packet: CacheStatusPacket) {
        cacheSupported = packet.supported
    }

    override fun violation(packet: ViolationPacket) {
        log.warn("{}: Violation reported: {}", this, packet)
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
