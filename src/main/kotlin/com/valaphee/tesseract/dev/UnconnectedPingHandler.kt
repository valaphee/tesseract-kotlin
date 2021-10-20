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

import com.valaphee.tesseract.latestProtocolVersion
import com.valaphee.tesseract.latestVersion
import com.valaphee.tesseract.net.Pong
import com.valaphee.tesseract.world.GameMode
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.socket.DatagramPacket
import io.netty.util.ReferenceCountUtil
import network.ycc.raknet.RakNet
import network.ycc.raknet.packet.UnconnectedPing
import network.ycc.raknet.packet.UnconnectedPong
import network.ycc.raknet.server.pipeline.UdpPacketHandler
import java.net.InetSocketAddress

/**
 * @author Kevin Ludwig
 */
class UnconnectedPingHandler : UdpPacketHandler<UnconnectedPing>(UnconnectedPing::class.java) {
    override fun handle(context: ChannelHandlerContext, address: InetSocketAddress, unconnectedPing: UnconnectedPing) {
        val rakNetConfig = context.channel().config() as RakNet.Config
        val unconnectedPong = UnconnectedPong(unconnectedPing.clientTime, rakNetConfig.serverId, rakNetConfig.magic, Pong(rakNetConfig.serverId, "Tesseract", latestVersion, latestProtocolVersion, "MCPE", false, GameMode.Survival, 0, 1, 19134, 19135, "Unable to ping.").toString())
        val buffer = context.alloc().directBuffer(unconnectedPong.sizeHint())
        try {
            rakNetConfig.codec.encode(unconnectedPong, buffer)
            repeat(3) { context.writeAndFlush(DatagramPacket(buffer.retainedSlice(), address)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE) }
        } finally {
            ReferenceCountUtil.safeRelease(unconnectedPong)
            buffer.release()
        }
    }
}
