/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction

/**
 * @author Kevin Ludwig
 */
class PacketDecoder(
    private val readers: Int2ObjectFunction<PacketReader>,
    var version: Int = -1
) : MessageToMessageDecoder<ByteBuf>() {
    override fun decode(context: ChannelHandlerContext, `in`: ByteBuf, out: MutableList<Any>) {
        val buffer = PacketBuffer(`in`)
        val header = buffer.readVarUInt()
        val id = header and Packet.idMask
        readers[id]?.let { out.add(it.read(buffer, version)) } ?: println("Missing 0x${id.toString(16).uppercase()}")
    }

    companion object {
        const val NAME = "ta-packet-decoder"
    }
}
