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

package com.valaphee.tesseract.net.init

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import io.netty.buffer.ByteBuf
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Clientbound)
data class PackDataChunkPacket(
    var packId: UUID,
    var packVersion: String?,
    var chunkIndex: Long,
    var progress: Long,
    var data: ByteBuf
) : Packet {
    override val id get() = 0x53

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeString("$packId${if (null == packVersion) "" else "_$packVersion"}")
        buffer.writeIntLE(chunkIndex.toInt())
        buffer.writeLongLE(progress)
        buffer.writeVarUInt(data.readableBytes())
        buffer.writeBytes(data)
    }

    override fun handle(handler: PacketHandler) = handler.packDataChunk(this)
}

/**
 * @author Kevin Ludwig
 */
object PackDataChunkPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): PackDataChunkPacket {
        val pack = buffer.readString().split("_".toRegex(), 2).toTypedArray()
        val packId = UUID.fromString(pack[0])
        val packVersion = if (pack.size == 2) pack[1] else null
        val chunkIndex = buffer.readUnsignedIntLE()
        val progress = buffer.readLongLE()
        val data = buffer.readSlice(buffer.readVarUInt())
        return PackDataChunkPacket(packId, packVersion, chunkIndex, progress, data)
    }
}
