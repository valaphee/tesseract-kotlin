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

package com.valaphee.tesseract.inventory

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader

/**
 * @author Kevin Ludwig
 */
class PhotoPacket(
    val name: String,
    val data: ByteArray,
    val bookId: String,
    val type: Type?,
    val sourceType: Type?,
    val ownerId: Long,
    val newName: String?
) : Packet() {
    enum class Type {
        Portfolio, PhotoItem, Book
    }

    override val id get() = 0x63

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeString(name)
        buffer.writeByteArray(data)
        buffer.writeString(bookId)
        if (version >= 465) {
            buffer.writeByte(type!!.ordinal)
            buffer.writeByte(sourceType!!.ordinal)
            buffer.writeLongLE(ownerId)
            buffer.writeString(newName!!)
        }
    }

    override fun handle(handler: PacketHandler) = handler.photo(this)

    override fun toString() = "PhotoPacket(name='$name', data=${data.contentToString()}, bookId='$bookId', type=$type, sourceType=$sourceType, ownerId=$ownerId, newName=$newName)"
}

/**
 * @author Kevin Ludwig
 */
object PhotoPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): PhotoPacket {
        val name = buffer.readString()
        val data = buffer.readByteArray()
        val bookId = buffer.readString()
        val type: PhotoPacket.Type?
        val sourceType: PhotoPacket.Type?
        val ownerId: Long
        val newName: String?
        if (version >= 465) {
            type = PhotoPacket.Type.values()[buffer.readByte().toInt()]
            sourceType = PhotoPacket.Type.values()[buffer.readByte().toInt()]
            ownerId = buffer.readLongLE()
            newName = buffer.readString()
        } else {
            type = null
            sourceType = null
            ownerId = 0
            newName = null
        }
        return PhotoPacket(name, data, bookId, type, sourceType, ownerId, newName)
    }
}
