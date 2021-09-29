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

package com.valaphee.tesseract.entity

import com.valaphee.tesseract.inventory.item.stack.Stack
import com.valaphee.tesseract.inventory.item.stack.readStack
import com.valaphee.tesseract.inventory.item.stack.readStackPre431
import com.valaphee.tesseract.inventory.item.stack.writeStack
import com.valaphee.tesseract.inventory.item.stack.writeStackPre431
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader

/**
 * @author Kevin Ludwig
 */
data class EntityEquipmentPacket(
    val runtimeEntityId: Long,
    val stack: Stack?,
    val slotId: Int,
    val hotbarSlot: Int,
    val windowId: Int
) : Packet {
    override val id get() = 0x1F

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarULong(runtimeEntityId)
        if (version >= 431) buffer.writeStack(stack) else buffer.writeStackPre431(stack)
        buffer.writeByte(slotId)
        buffer.writeByte(hotbarSlot)
        buffer.writeByte(windowId)
    }

    override fun handle(handler: PacketHandler) = handler.entityEquipment(this)
}

/**
 * @author Kevin Ludwig
 */
object EntityEquipmentPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = EntityEquipmentPacket(
        buffer.readVarULong(),
        if (version >= 431) buffer.readStack() else buffer.readStackPre431(),
        buffer.readUnsignedByte().toInt(),
        buffer.readUnsignedByte().toInt(),
        buffer.readByte().toInt()
    )
}
