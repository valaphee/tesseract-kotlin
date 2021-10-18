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
import com.valaphee.tesseract.net.Decompressor
import com.valaphee.tesseract.net.PacketDecoder
import com.valaphee.tesseract.net.PacketEncoder
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.PooledByteBufAllocator
import io.netty.channel.AdaptiveRecvByteBufAllocator
import io.netty.channel.Channel
import io.netty.channel.ChannelException
import io.netty.channel.ChannelFactory
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.WriteBufferWaterMark
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollChannelOption
import io.netty.channel.unix.UnixChannelOption
import network.ycc.raknet.pipeline.UserDataCodec
import network.ycc.raknet.server.channel.RakNetServerChannel
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.concurrent.TimeUnit

/**
 * @author Kevin Ludwig
 */
class Sniff @Inject constructor(
    private val injector: Injector,
    private val config: SniffConfig
) : Tool {
    private lateinit var channel: Channel

    override fun run() {
        val serverBootstrap = ServerBootstrap()
            .group(parentGroup, childGroup)
            .channelFactory(ChannelFactory { RakNetServerChannel(underlyingNetworking.datagramChannel) })
            .handler(object : ChannelInitializer<Channel>() {
                override fun initChannel(channel: Channel) {
                    channel.config().writeBufferWaterMark = writeBufferWaterMark
                }
            })
            .childHandler(object : ChannelInitializer<Channel>() {
                override fun initChannel(channel: Channel) {
                    val config = channel.config()
                    try {
                        config.setOption(ChannelOption.IP_TOS, 0x18)
                    } catch (_: ChannelException) {
                    }
                    config.setAllocator(PooledByteBufAllocator.DEFAULT).writeBufferWaterMark = childWriteBufferWaterMark

                    channel.pipeline()
                        .addLast(UserDataCodec.NAME, userDataCodec)
                        .addLast(Compressor.NAME, Compressor())
                        .addLast(Decompressor.NAME, Decompressor())
                        .addLast(PacketEncoder.NAME, PacketEncoder(true))
                        .addLast(PacketDecoder.NAME, PacketDecoder(true))
                }
            })
        val channelFutureListener = ChannelFutureListener {
            if (it.isSuccess) {
                channel = it.channel()

                log.info("Listening on {}", channel.localAddress())
            } else log.warn("Failed to bind to ${config.address}", it.cause())
        }
        if (underlyingNetworking == Tool.UnderlyingNetworking.Epoll) {
            serverBootstrap
                .option(UnixChannelOption.SO_REUSEPORT, true)
                .option(EpollChannelOption.MAX_DATAGRAM_PAYLOAD_SIZE, 4 * 1024)
            repeat(Runtime.getRuntime().availableProcessors()) {
                serverBootstrap.clone()
                    .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator(4 * 1024, 64 * 1024, 256 * 1024))
                    .bind(config.address)
                    .addListener(channelFutureListener)
            }
        } else serverBootstrap.bind(config.address).addListener(channelFutureListener)
    }

    override fun destroy() {
        parentGroup.shutdownGracefully()
        parentGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)

        childGroup.shutdownGracefully()
        childGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
    }

    companion object {
        private val log: Logger = LogManager.getLogger(Sniff::class.java)
        private val underlyingNetworking = if (Epoll.isAvailable()) Tool.UnderlyingNetworking.Epoll/* else if (KQueue.isAvailable()) UnderlyingNetworking.Kqueue*/ else Tool.UnderlyingNetworking.Nio
        private val parentGroup = underlyingNetworking.groupFactory(0, ThreadFactoryBuilder().setNameFormat("server-%d").build())
        private val childGroup = underlyingNetworking.groupFactory(0, ThreadFactoryBuilder().setNameFormat("server-c-%d").build())
        private val writeBufferWaterMark = WriteBufferWaterMark(5 * 1024 * 1024, 10 * 1024 * 1024)
        private val childWriteBufferWaterMark = WriteBufferWaterMark(512 * 1024, 2 * 1024 * 1024)
        private val userDataCodec = UserDataCodec(0xFE)
    }
}
