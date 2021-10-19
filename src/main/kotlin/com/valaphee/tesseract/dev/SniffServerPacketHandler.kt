/*
 * Copyright (c) 2021, GrieferGames, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.dev

import com.google.inject.Inject
import com.google.inject.Injector
import com.valaphee.tesseract.net.Compressor
import com.valaphee.tesseract.net.Connection
import com.valaphee.tesseract.net.Decompressor
import com.valaphee.tesseract.net.EncryptionInitializer
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketDecoder
import com.valaphee.tesseract.net.PacketEncoder
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.base.ClientToServerHandshakePacket
import com.valaphee.tesseract.net.base.DisconnectPacket
import com.valaphee.tesseract.net.base.LoginPacket
import com.valaphee.tesseract.util.generateKeyPair
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFactory
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelInitializer
import network.ycc.raknet.RakNet
import network.ycc.raknet.client.channel.RakNetClientChannel
import network.ycc.raknet.pipeline.UserDataCodec
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * @author Kevin Ludwig
 */
class SniffServerPacketHandler(
    private val clientConnection: Connection
) : PacketHandler {
    @Inject private lateinit var injector: Injector
    @Inject private lateinit var config: SniffConfig
    private lateinit var serverConnection: Connection
    private val keyPair = generateKeyPair()

    override fun other(packet: Packet) {
        if (!ignoringPackets.contains(packet.id)) packetLog.info("{}", packet.toString())

        serverConnection.write(packet)
    }

    override fun login(packet: LoginPacket) {
        if (!ignoringPackets.contains(packet.id)) packetLog.info("{}", packet.toString())

        clientConnection.version = packet.protocolVersion

        val bootstrap = Bootstrap()
            .group(clientConnection.context.channel().eventLoop())
            .channelFactory(ChannelFactory { RakNetClientChannel(Sniff.underlyingNetworking.datagramChannel) })
            .option(RakNet.MTU, config.clientMtu)
            .option(RakNet.PROTOCOL_VERSION, 10)
            .handler(object : ChannelInitializer<Channel>() {
                override fun initChannel(channel: Channel) {
                    serverConnection = Connection(clientConnection.version).apply { setHandler(SniffClientPacketHandler(clientConnection, this, /*packet.authExtra, packet.user*/packet).apply { injector.injectMembers(this) }) }
                    channel.pipeline()
                        .addLast(UserDataCodec.NAME, Sniff.userDataCodec)
                        .addLast(Compressor.NAME, Compressor())
                        .addLast(Decompressor.NAME, Decompressor())
                        .addLast(PacketEncoder.NAME, PacketEncoder(false, serverConnection.version))
                        .addLast(PacketDecoder.NAME, PacketDecoder(false, serverConnection.version))
                        .addLast(serverConnection)
                }
            })
        bootstrap.connect(config.clientAddress).addListener(ChannelFutureListener {
            if (it.isSuccess) {
                val encryptionInitializer = EncryptionInitializer(keyPair, packet.publicKey, clientConnection.version >= 431)
                clientConnection.write(encryptionInitializer.serverToClientHandshakePacket)
                clientConnection.context.pipeline().addLast(encryptionInitializer)

                Sniff.log.info("Connected to {}", it.channel().remoteAddress())
            } else {
                clientConnection.close(DisconnectPacket("disconnectionScreen.noReason"))

                Sniff.log.error("Failed to connect to ${config.clientAddress}", it.cause())
            }
        })
    }

    override fun clientToServerHandshake(packet: ClientToServerHandshakePacket) {
        if (!ignoringPackets.contains(packet.id)) packetLog.info("{}", packet.toString())
    }

    companion object {
        private val packetLog: Logger = LogManager.getLogger("Packet (ToServer)")
        private val ignoringPackets = setOf(
            0x01, // LoginPacket
            0x88, // CacheBlobStatusPacket
            0x90, // InputPacket
        )
    }
}
