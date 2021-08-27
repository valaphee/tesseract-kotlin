/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk

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
data class ChunkCacheStatusPacket(
    var supported: Boolean = false
) : Packet {
    override val id get() = 0x81

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeBoolean(supported)
    }

    override fun handle(handler: PacketHandler) = handler.chunkCacheStatus(this)
}

/**
 * @author Kevin Ludwig
 */
class ChunkCacheStatusPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = ChunkCacheStatusPacket(buffer.readBoolean())
}
