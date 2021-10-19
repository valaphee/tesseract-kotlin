/*
 * Copyright (c) 2021, GrieferGames, Valaphee.
 * All rights reserved.
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
