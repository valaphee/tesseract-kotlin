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

package com.valaphee.tesseract.net.init.pack

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
@Restrict(Restriction.ToServer)
data class PacksResponsePacket(
    var status: Status,
    var packIds: Array<UUID>
) : Packet {
    enum class Status {
        None, Refused, TransferPacks, HaveAllPacks, Completed
    }

    override val id get() = 0x08

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeByte(status.ordinal)
        buffer.writeShortLE(packIds.size)
        packIds.forEach { buffer.writeString(it.toString()) }
    }

    override fun handle(handler: PacketHandler) = handler.packsResponse(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PacksResponsePacket

        if (status != other.status) return false
        if (!packIds.contentEquals(other.packIds)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + packIds.contentHashCode()
        return result
    }
}

/**
 * @author Kevin Ludwig
 */
object PacksResponsePacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = PacksResponsePacket(
        PacksResponsePacket.Status.values()[buffer.readUnsignedByte().toInt()],
        Array(buffer.readUnsignedShortLE()) { UUID.fromString(buffer.readString().split("_".toRegex(), 2).toTypedArray()[0]) }
    )
}
