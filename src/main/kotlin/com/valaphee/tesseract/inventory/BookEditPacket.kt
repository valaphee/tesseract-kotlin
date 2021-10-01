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
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToServer)
data class BookEditPacket(
    val action: Action,
    val slotId: Int,
    val pageNumber: Int,
    val text: String?,
    val otherPageNumber: Int,
    val title: String?,
    val photoName: String?,
    val author: String?,
    val xboxUserId: String?
) : Packet {
    enum class Action {
        ReplacePage, AddPage, DeletePage, SwapPages, SignBook
    }

    override val id get() = 0x61

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeByte(action.ordinal)
        buffer.writeByte(slotId)
        when (action) {
            Action.ReplacePage, Action.AddPage -> {
                buffer.writeByte(pageNumber)
                buffer.writeString(text!!)
                buffer.writeString(photoName!!)
            }
            Action.DeletePage -> buffer.writeByte(pageNumber)
            Action.SwapPages -> {
                buffer.writeByte(pageNumber)
                buffer.writeByte(otherPageNumber)
            }
            Action.SignBook -> {
                buffer.writeString(title!!)
                buffer.writeString(author!!)
                buffer.writeString(xboxUserId!!)
            }
        }
    }

    override fun handle(handler: PacketHandler) = handler.bookEdit(this)
}

/**
 * @author Kevin Ludwig
 */
object BookEditPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): BookEditPacket {
        val action = BookEditPacket.Action.values()[buffer.readUnsignedByte().toInt()]
        val slotId = buffer.readUnsignedByte().toInt()
        val pageNumber: Int
        val text: String?
        val otherPageNumber: Int
        val title: String?
        val photoName: String?
        val author: String?
        val xboxUserId: String?
        when (action) {
            BookEditPacket.Action.ReplacePage, BookEditPacket.Action.AddPage -> {
                pageNumber = buffer.readUnsignedByte().toInt()
                text = buffer.readString()
                otherPageNumber = 0
                title = null
                photoName = buffer.readString()
                author = null
                xboxUserId = null
            }
            BookEditPacket.Action.DeletePage -> {
                pageNumber = buffer.readUnsignedByte().toInt()
                text = null
                otherPageNumber = 0
                title = null
                photoName = null
                author = null
                xboxUserId = null
            }
            BookEditPacket.Action.SwapPages -> {
                pageNumber = buffer.readUnsignedByte().toInt()
                text = null
                otherPageNumber = buffer.readUnsignedByte().toInt()
                title = null
                photoName = null
                author = null
                xboxUserId = null
            }
            BookEditPacket.Action.SignBook -> {
                pageNumber = 0
                text = null
                otherPageNumber = 0
                title = buffer.readString()
                photoName = null
                author = buffer.readString()
                xboxUserId = buffer.readString()
            }
        }
        return BookEditPacket(action, slotId, pageNumber, text, otherPageNumber, title, photoName, author, xboxUserId)
    }
}
