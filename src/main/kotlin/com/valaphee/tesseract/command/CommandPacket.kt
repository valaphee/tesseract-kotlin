/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.command

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Serverbound)
data class CommandPacket(
    var command: String,
    var origin: Origin,
    var internal: Boolean
) : Packet {
    override val id get() = 0x4D

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeString(command)
        buffer.writeOrigin(origin)
        buffer.writeBoolean(internal)
    }

    override fun handle(handler: PacketHandler) = handler.command(this)
}

/**
 * @author Kevin Ludwig
 */
object CommandPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = CommandPacket(buffer.readString(), buffer.readOrigin(), buffer.readBoolean())
}
