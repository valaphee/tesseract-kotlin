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

package com.valaphee.tesseract.actor.player

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
data class EmotesPacket(
    var runtimeEntityId: Long,
    var pieceIds: Array<UUID>
) : Packet {
    override val id get() = 0x98

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarULong(runtimeEntityId)
        buffer.writeVarUInt(pieceIds.size)
        pieceIds.forEach { buffer.writeUuid(it) }
    }

    override fun handle(handler: PacketHandler) = handler.emotes(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmotesPacket

        if (runtimeEntityId != other.runtimeEntityId) return false
        if (!pieceIds.contentEquals(other.pieceIds)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = runtimeEntityId.hashCode()
        result = 31 * result + pieceIds.contentHashCode()
        return result
    }
}

/**
 * @author Kevin Ludwig
 */
object EmotesPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = EmotesPacket(buffer.readVarULong(), Array(buffer.readVarUInt()) { buffer.readUuid() })
}
