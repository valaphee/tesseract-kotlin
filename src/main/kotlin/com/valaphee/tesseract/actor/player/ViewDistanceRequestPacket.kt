/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.player

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader

/**
 * @author Kevin Ludwig
 */
data class ViewDistanceRequestPacket(
    var distance: Int = 0
) : Packet {
    override val id get() = 0x45

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarInt(distance)
    }

    override fun handle(handler: PacketHandler) = handler.viewDistanceRequest(this)
}

/**
 * @author Kevin Ludwig
 */
object ViewDistanceRequestPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = ViewDistanceRequestPacket(buffer.readVarInt())
}
