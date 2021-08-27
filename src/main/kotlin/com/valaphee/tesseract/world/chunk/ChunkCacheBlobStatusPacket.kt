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
data class ChunkCacheBlobStatusPacket(
    var misses: LongArray,
    var hits: LongArray
) : Packet {
    override val id get() = 0x87

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarUInt(misses.size)
        buffer.writeVarUInt(hits.size)
        misses.forEach { buffer.writeLongLE(it) }
        hits.forEach { buffer.writeLongLE(it) }
    }

    override fun handle(handler: PacketHandler) = handler.chunkCacheBlobStatus(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChunkCacheBlobStatusPacket

        if (!misses.contentEquals(other.misses)) return false
        if (!hits.contentEquals(other.hits)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = misses.contentHashCode()
        result = 31 * result + hits.contentHashCode()
        return result
    }
}

/**
 * @author Kevin Ludwig
 */
object ChunkCacheBlobStatusPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): ChunkCacheBlobStatusPacket {
        val negativeAcknowledgeCount = buffer.readVarUInt()
        val acknowledgeCount = buffer.readVarUInt()
        return ChunkCacheBlobStatusPacket(
            LongArray(negativeAcknowledgeCount) { buffer.readLongLE() },
            LongArray(acknowledgeCount) { buffer.readLongLE() }
        )
    }
}
