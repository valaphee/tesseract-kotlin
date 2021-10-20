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
import com.valaphee.tesseract.util.nbt.Tag

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
class EquipmentPacket(
    val windowId: Int,
    val type: WindowType,
    val slotCount: Int,
    val uniqueEntityId: Long,
    val tag: Tag?
) : Packet {
    override val id get() = 0x51

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeByte(windowId)
        buffer.writeByte(type.id)
        buffer.writeVarInt(slotCount)
        buffer.writeVarLong(uniqueEntityId)
        buffer.toNbtOutputStream().use { it.writeTag(tag) }
    }

    override fun handle(handler: PacketHandler) = handler.equipment(this)

    override fun toString() = "EquipmentPacket(windowId=$windowId, type=$type, slotCount=$slotCount, uniqueEntityId=$uniqueEntityId, tag=$tag)"
}

/**
 * @author Kevin Ludwig
 */
object EquipmentPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = EquipmentPacket(buffer.readByte().toInt(), WindowType.byId(buffer.readByte().toInt()), buffer.readVarInt(), buffer.readVarLong(), buffer.toNbtInputStream().use { it.readTag() })
}
