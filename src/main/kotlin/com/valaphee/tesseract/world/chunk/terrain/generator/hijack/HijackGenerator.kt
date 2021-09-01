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

package com.valaphee.tesseract.world.chunk.terrain.generator.hijack

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.valaphee.foundry.math.Int2
import com.valaphee.tesseract.Instance
import com.valaphee.tesseract.net.Compressor
import com.valaphee.tesseract.net.Connection
import com.valaphee.tesseract.net.Decompressor
import com.valaphee.tesseract.net.PacketDecoder
import com.valaphee.tesseract.net.PacketEncoder
import com.valaphee.tesseract.world.chunk.terrain.Terrain
import com.valaphee.tesseract.world.chunk.terrain.generator.Generator
import io.netty.bootstrap.Bootstrap
import io.netty.channel.AdaptiveRecvByteBufAllocator
import io.netty.channel.Channel
import io.netty.channel.ChannelFactory
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.epoll.EpollChannelOption
import network.ycc.raknet.RakNet
import network.ycc.raknet.client.channel.RakNetClientChannel
import network.ycc.raknet.pipeline.UserDataCodec
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.net.InetSocketAddress

/**
 * @author Kevin Ludwig
 */
class HijackGenerator : Generator {
    private var group = Instance.underlyingNetworking.groupFactory(0, ThreadFactoryBuilder().setNameFormat("hijack-%d").build())

    lateinit var connection: Connection

    init {
        val userDataCodec = UserDataCodec(0xFE)
        val bootstrap = Bootstrap()
            .group(group)
            .channelFactory(ChannelFactory { RakNetClientChannel(Instance.underlyingNetworking.datagramChannel) })
            .option(RakNet.MTU, 1_464)
            .option(RakNet.PROTOCOL_VERSION, 10)
            .handler(object : ChannelInitializer<Channel>() {
                override fun initChannel(channel: Channel) {
                    connection = Connection()
                    connection.setHandler(InitPacketHandler(connection))
                    channel.pipeline()
                        .addLast(UserDataCodec.NAME, userDataCodec)
                        .addLast(Compressor.NAME, Compressor())
                        .addLast(Decompressor.NAME, Decompressor())
                        .addLast(PacketEncoder.NAME, PacketEncoder(false))
                        .addLast(PacketDecoder.NAME, PacketDecoder())
                        .addLast(connection)
                }
            })
        if (Instance.underlyingNetworking == Instance.UnderlyingNetworking.Epoll) bootstrap
            .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator(4 * 1024, 8 * 1024, 16 * 1024))
            .option(EpollChannelOption.MAX_DATAGRAM_PAYLOAD_SIZE, 4 * 1024)
        bootstrap.connect(InetSocketAddress("172.16.1.116", 19132)).addListener(ChannelFutureListener {
            if (it.isSuccess) log.info("Hijacking {}", it.channel().remoteAddress())
            else log.warn("Failed to hijack {}", it.channel().remoteAddress(), it.cause())
        })
    }

    override fun generate(position: Int2): Terrain {
        TODO()
    }

    companion object {
        private val log: Logger = LogManager.getLogger(HijackGenerator::class.java)
    }
}
