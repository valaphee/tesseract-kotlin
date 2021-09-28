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

import com.valaphee.tesseract.inventory.item.stack.Stack
import com.valaphee.tesseract.inventory.item.stack.readStackInstance
import com.valaphee.tesseract.inventory.item.stack.readStackWithNetIdPre431
import com.valaphee.tesseract.inventory.item.stack.writeStackInstance
import com.valaphee.tesseract.inventory.item.stack.writeStackWithNetIdPre431
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
data class CreativeInventoryPacket(
    val content: Array<Stack/*<*>*/?>
) : Packet {
    override val id get() = 0x91

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarUInt(content.size)
        content.forEach {
            if (version >= 431) {
                buffer.writeVarUInt(it?.netId ?: 0)
                buffer.writeStackInstance(it)
            } else buffer.writeStackWithNetIdPre431(it)
        }
    }

    override fun handle(handler: PacketHandler) = handler.creativeInventory(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreativeInventoryPacket

        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode() = content.contentHashCode()
}

/**
 * @author Kevin Ludwig
 */
object CreativeInventoryPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = CreativeInventoryPacket(Array(buffer.readVarUInt()) {
        if (version >= 431) {
            val netId = buffer.readVarUInt()
            buffer.readStackInstance().also { it?.let { it.netId = netId } }
        } else buffer.readStackWithNetIdPre431()
    })
}
