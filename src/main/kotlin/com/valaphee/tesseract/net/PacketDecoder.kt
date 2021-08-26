/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction

/**
 * @author Kevin Ludwig
 */
@ChannelHandler.Sharable
class PacketDecoder(
    private val readers: Int2ObjectFunction<PacketReader>
) : MessageToMessageDecoder<ByteBuf>() {
    override fun decode(context: ChannelHandlerContext, `in`: ByteBuf, out: MutableList<Any>) {
        val packetBuffer = PacketBuffer(`in`)
        out.add(readers[packetBuffer.readVarUInt()].read(packetBuffer, 448))
    }

    companion object {
        const val NAME = "ta-packet-decoder"
    }
}
