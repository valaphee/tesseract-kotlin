/*
 * MIT License
 *
 * Copyright (c) 2021, Valaphee.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.valaphee.tesseract.actor.player.view

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import com.valaphee.tesseract.world.chunk.Chunk
import com.valaphee.tesseract.world.chunk.position
import com.valaphee.tesseract.world.chunk.terrain.BlockStorage.Companion.XZSize
import com.valaphee.tesseract.world.chunk.terrain.terrain

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Clientbound)
data class ChunkPacket(
    var chunk: Chunk,
    var cache: Boolean,
    val blobIds: LongArray? = null
) : Packet {
    override val id get() = 0x3A

    constructor(chunk: Chunk) : this(chunk, false, null)

    constructor(chunk: Chunk, blobIds: LongArray) : this(chunk, true, blobIds)

    override fun write(buffer: PacketBuffer, version: Int) {
        val (x, z) = chunk.position
        buffer.writeVarInt(x)
        buffer.writeVarInt(z)
        val blockStorage = chunk.terrain.blockStorage
        var sectionCount = blockStorage.sections.size - 1
        while (sectionCount >= 0 && blockStorage.sections[sectionCount].empty) sectionCount--
        buffer.writeVarUInt(++sectionCount)
        buffer.writeBoolean(cache)
        if (cache) blobIds!!.let {
            buffer.writeVarUInt(it.size)
            it.forEach { buffer.writeLongLE(it) }
        }
        val dataLengthIndex = buffer.writerIndex()
        buffer.writeZero(PacketBuffer.MaximumVarUIntLength)
        if (!cache) {
            repeat(sectionCount) { i -> blockStorage.sections[i].writeToBuffer(buffer) }
            buffer.writeBytes(ByteArray(XZSize * XZSize))
        }
        buffer.writeByte(0)
        buffer.writeVarInt(0)
        buffer.setMaximumLengthVarUInt(dataLengthIndex, buffer.writerIndex() - (dataLengthIndex + PacketBuffer.MaximumVarUIntLength))
    }

    override fun handle(handler: PacketHandler) = handler.chunkAdd(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChunkPacket

        if (chunk != other.chunk) return false
        if (cache != other.cache) return false
        if (blobIds != null) {
            if (other.blobIds == null) return false
            if (!blobIds.contentEquals(other.blobIds)) return false
        } else if (other.blobIds != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = chunk.hashCode()
        result = 31 * result + cache.hashCode()
        result = 31 * result + (blobIds?.contentHashCode() ?: 0)
        return result
    }
}