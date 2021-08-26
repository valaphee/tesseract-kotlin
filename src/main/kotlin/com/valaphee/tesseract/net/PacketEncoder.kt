/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

/**
 * @author Kevin Ludwig
 */
class PacketEncoder(
    private val server: Boolean
) : MessageToByteEncoder<Packet>() {
    /*override fun acceptOutboundMessage(message: Any): Boolean {
        val restrictions = message::class.findAnnotation<Restrict>()?.value ?: return true
        return server && !restrictions.contains(Restriction.Clientbound) || !server && !restrictions.contains(Restriction.Serverbound)
    }*/

    override fun encode(context: ChannelHandlerContext, message: Packet, out: ByteBuf) {
        val packetBuffer = PacketBuffer(out)
        packetBuffer.writeVarUInt(message.id)
        message.write(packetBuffer, 448)
    }

    companion object {
        const val NAME = "ta-packet-encoder"
    }
}
