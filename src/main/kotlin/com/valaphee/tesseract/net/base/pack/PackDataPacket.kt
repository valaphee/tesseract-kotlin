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
 *
 */

package com.valaphee.tesseract.net.base.pack

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
data class PackDataPacket(
    val packId: UUID,
    val packVersion: String?,
    val maximumChunkSize: Long,
    val chunkCount: Long,
    val compressedPackSize: Long,
    val hash: ByteArray,
    val premium: Boolean,
    val type: Type
) : Packet {
    enum class Type {
        Invalid,
        AddOn,
        Cached,
        CopyProtected,
        Behavior,
        PersonaPiece,
        Resource,
        Skins,
        WorldTemplate
    }

    override val id get() = 0x52

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeString("$packId${packVersion?.let { "_$packVersion" } ?: ""}")
        buffer.writeIntLE(maximumChunkSize.toInt())
        buffer.writeIntLE(chunkCount.toInt())
        buffer.writeLongLE(compressedPackSize)
        buffer.writeByteArray(hash)
        buffer.writeBoolean(premium)
        buffer.writeByte(type.ordinal)
    }

    override fun handle(handler: PacketHandler) = handler.packData(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PackDataPacket

        if (packId != other.packId) return false
        if (packVersion != other.packVersion) return false
        if (maximumChunkSize != other.maximumChunkSize) return false
        if (chunkCount != other.chunkCount) return false
        if (compressedPackSize != other.compressedPackSize) return false
        if (!hash.contentEquals(other.hash)) return false
        if (premium != other.premium) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = packId.hashCode()
        result = 31 * result + (packVersion?.hashCode() ?: 0)
        result = 31 * result + maximumChunkSize.hashCode()
        result = 31 * result + chunkCount.hashCode()
        result = 31 * result + compressedPackSize.hashCode()
        result = 31 * result + hash.contentHashCode()
        result = 31 * result + premium.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}

/**
 * @author Kevin Ludwig
 */
object PackDataPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): PackDataPacket {
        val pack = buffer.readString().split("_".toRegex(), 2).toTypedArray()
        val packId = UUID.fromString(pack[0])
        val packVersion = if (pack.size == 2) pack[1] else null
        val maximumChunkSize = buffer.readUnsignedIntLE()
        val chunkCount = buffer.readUnsignedIntLE()
        val compressedPackSize = buffer.readLongLE()
        val hash = buffer.readByteArray()
        val premium = buffer.readBoolean()
        val type = PackDataPacket.Type.values()[buffer.readUnsignedByte().toInt()]
        return PackDataPacket(packId, packVersion, maximumChunkSize, chunkCount, compressedPackSize, hash, premium, type)
    }
}
