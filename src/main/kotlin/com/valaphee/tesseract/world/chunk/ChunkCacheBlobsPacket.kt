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
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Clientbound)
data class ChunkCacheBlobsPacket(
    val blobs: Long2ObjectMap<ByteArray> = Long2ObjectOpenHashMap()
) : Packet {
    override val id get() = 0x88

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarUInt(blobs.size)
        blobs.forEach { (key, value) ->
            buffer.writeLongLE(key)
            buffer.writeByteArray(value)
        }
    }

    override fun handle(handler: PacketHandler) = handler.chunkCacheBlobs(this)
}

/**
 * @author Kevin Ludwig
 */
class ChunkCacheBlobsPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = ChunkCacheBlobsPacket().apply { repeat(buffer.readVarUInt()) { blobs[buffer.readLongLE()] = buffer.readByteArray() } }
}
