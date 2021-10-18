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

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.google.inject.Inject
import com.google.inject.Injector
import com.valaphee.tesseract.net.Compressor
import com.valaphee.tesseract.net.Connection
import com.valaphee.tesseract.net.Decompressor
import com.valaphee.tesseract.net.PacketDecoder
import com.valaphee.tesseract.net.PacketEncoder
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFactory
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelInitializer
import io.netty.channel.epoll.Epoll
import network.ycc.raknet.RakNet
import network.ycc.raknet.client.channel.RakNetClientChannel
import network.ycc.raknet.pipeline.UserDataCodec
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.concurrent.TimeUnit

/**
 * @author Kevin Ludwig
 */
class Extract @Inject constructor(
    private val injector: Injector,
    private val config: ExtractConfig
) : Tool {
    override fun run() {
        val bootstrap = Bootstrap()
            .group(group)
            .channelFactory(ChannelFactory { RakNetClientChannel(underlyingNetworking.datagramChannel) })
            .option(RakNet.MTU, 1_464)
            .option(RakNet.PROTOCOL_VERSION, 10)
            .handler(object : ChannelInitializer<Channel>() {
                override fun initChannel(channel: Channel) {
                    val connection = Connection()
                    connection.setHandler(ExtractPacketHandler(connection).apply { injector.injectMembers(this) })
                    channel.pipeline()
                        .addLast(UserDataCodec.NAME, userDataCodec)
                        .addLast(Compressor.NAME, Compressor())
                        .addLast(Decompressor.NAME, Decompressor())
                        .addLast(PacketEncoder.NAME, PacketEncoder(false))
                        .addLast(PacketDecoder.NAME, PacketDecoder(false))
                        .addLast(connection)
                }
            })
        bootstrap.connect(config.address).addListener(ChannelFutureListener {
            if (it.isSuccess) log.info("Connecting to {}", it.channel().remoteAddress())
            else log.error("Failed to connect to ${config.address}", it.cause())
        })
    }

    override fun destroy() {
        group.shutdownGracefully()
        group.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
    }

    companion object {
        private val log: Logger = LogManager.getLogger(Extract::class.java)
        private val underlyingNetworking = if (Epoll.isAvailable()) Tool.UnderlyingNetworking.Epoll/* else if (KQueue.isAvailable()) UnderlyingNetworking.Kqueue*/ else Tool.UnderlyingNetworking.Nio
        private val group = underlyingNetworking.groupFactory(0, ThreadFactoryBuilder().setNameFormat("client-%d").build())
        private val userDataCodec = UserDataCodec(0xFE)
    }
}
