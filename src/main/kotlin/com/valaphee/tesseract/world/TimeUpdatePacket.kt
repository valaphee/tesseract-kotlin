/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world

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
data class TimeUpdatePacket(
    var time: Int = 0
) : Packet {
    override val id get() = 0x0A

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarInt(time)
    }

    override fun handle(handler: PacketHandler) = handler.timeUpdate(this)
}

/**
 * @author Kevin Ludwig
 */
class TimeUpdatePacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = TimeUpdatePacket(buffer.readVarInt())
}
