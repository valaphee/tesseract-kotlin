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

package com.valaphee.tesseract.dev

import com.google.inject.Inject
import com.google.inject.Injector
import com.valaphee.tesseract.net.Compressor
import com.valaphee.tesseract.net.Connection
import com.valaphee.tesseract.net.Decompressor
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketDecoder
import com.valaphee.tesseract.net.PacketDecoderException
import com.valaphee.tesseract.net.PacketEncoder
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.base.DisconnectPacket
import com.valaphee.tesseract.net.base.LoginPacket
import com.valaphee.tesseract.util.dump
import com.valaphee.tesseract.util.lazyToString
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

    override fun exceptionCaught(cause: Throwable) {
        when (cause) {
            is PacketDecoderException -> packetLog.info("{}", lazyToString { cause.buffer.dump(cause.buffer.readerIndex().toLong(), cause.buffer.readerIndex(), cause.buffer.readableBytes()) })
        }
    }

    override fun destroy() {
        serverConnection.close()
    }

    override fun other(packet: Packet) {
        if (!ignoringPackets.contains(packet.id)) packetLog.info("{}", packet.toString())

        serverConnection.write(packet)
    }

    override fun login(packet: LoginPacket) {
        clientConnection.version = packet.protocolVersion

        clientConnection.context.pipeline()[PacketDecoder::class.java].verified = true

        val bootstrap = Bootstrap()
            .group(clientConnection.context.channel().eventLoop())
            .channelFactory(ChannelFactory { RakNetClientChannel(Sniff.underlyingNetworking.datagramChannel) })
            .option(RakNet.MTU, config.clientMtu)
            .option(RakNet.PROTOCOL_VERSION, 10)
            .handler(object : ChannelInitializer<Channel>() {
                override fun initChannel(channel: Channel) {
                    serverConnection = Connection(clientConnection.version).apply { setHandler(SniffClientPacketHandler(clientConnection, this, packet).apply { injector.injectMembers(this) }) }
                    channel.pipeline()
                        .addLast(UserDataCodec.NAME, Sniff.userDataCodec)
                        .addLast(Compressor.NAME, Compressor())
                        .addLast(Decompressor.NAME, Decompressor())
                        .addLast(PacketEncoder.NAME, PacketEncoder(true, serverConnection.version))
                        .addLast(PacketDecoder.NAME, PacketDecoder(true, serverConnection.version))
                        .addLast(serverConnection)
                }
            })
        bootstrap.connect(config.clientAddress).addListener(ChannelFutureListener {
            if (it.isSuccess) Sniff.log.info("Connected to {}", it.channel().remoteAddress())
            else {
                clientConnection.close(DisconnectPacket("disconnectionScreen.noReason"))

                Sniff.log.error("Failed to connect to ${config.clientAddress}", it.cause())
            }
        })
    }

    /*override fun cacheStatus(packet: CacheStatusPacket) = Unit*/

    companion object {
        private val packetLog: Logger = LogManager.getLogger("Packet (ToServer)")
        private val ignoringPackets = setOf(
            0x01, // LoginPacket
            0x88, // CacheBlobStatusPacket
            0x90, // InputPacket
        )
    }
}
