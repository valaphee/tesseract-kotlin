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

package com.valaphee.tesseract.net.base

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
@Restrict(Restriction.ToClient)
class CacheBlobsPacket(
    val blobs: Long2ObjectMap<ByteArray>
) : Packet {
    override val id get() = 0x88

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarUInt(blobs.size)
        blobs.forEach { (key, value) ->
            buffer.writeLongLE(key)
            buffer.writeByteArray(value)
        }
    }

    override fun handle(handler: PacketHandler) = handler.cacheBlobs(this)

    override fun toString() = "CacheBlobsPacket(blobs=$blobs)"
}

/**
 * @author Kevin Ludwig
 */
object CacheBlobsPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): CacheBlobsPacket {
        val blobCount = buffer.readVarUInt()
        return CacheBlobsPacket(Long2ObjectOpenHashMap<ByteArray>(blobCount).apply { repeat(blobCount) { this[buffer.readLongLE()] = buffer.readByteArray() } })
    }
}
