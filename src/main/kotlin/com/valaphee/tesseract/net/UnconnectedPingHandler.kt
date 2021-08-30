/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net

import GameMode
import com.valaphee.tesseract.Config
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
class UnconnectedPingHandler(
    private val config: Config
) : UdpPacketHandler<UnconnectedPing>(UnconnectedPing::class.java) {
    override fun handle(context: ChannelHandlerContext, address: InetSocketAddress, packet: UnconnectedPing) {
        val rakNetConfig = context.channel().config() as RakNet.Config
        val unconnectedPongPacket = UnconnectedPong(packet.clientTime, rakNetConfig.serverId, rakNetConfig.magic, Pong(rakNetConfig.serverId, config.serverName, "1.17.11", 448, "MCPE", false, GameMode.Survival, 0, 20, 19132, 19133, config.serverName).toString())
        val unconnectedPongBuffer = context.alloc().directBuffer(unconnectedPongPacket.sizeHint())
        try {
            rakNetConfig.codec.encode(unconnectedPongPacket, unconnectedPongBuffer)
            repeat(3) { context.writeAndFlush(DatagramPacket(unconnectedPongBuffer.retainedSlice(), address)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE) }
        } finally {
            ReferenceCountUtil.safeRelease(unconnectedPongPacket)
            unconnectedPongBuffer.release()
        }
    }
}
