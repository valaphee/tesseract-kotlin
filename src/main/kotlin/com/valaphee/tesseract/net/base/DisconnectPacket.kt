/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net.base

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Clientbound)
data class DisconnectPacket(
    var message: String? = null
) : Packet {
    override val id get() = 0x05

    override fun write(buffer: PacketBuffer, version: Int) {
        message?.let {
            buffer.writeBoolean(false)
            buffer.writeString(it)
        } ?: buffer.writeBoolean(true)
    }

    override fun handle(handler: PacketHandler) = handler.disconnect(this)
}

/**
 * @author Kevin Ludwig
 */
object DisconnectPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = DisconnectPacket(if (!buffer.readBoolean()) buffer.readString() else null)
}
