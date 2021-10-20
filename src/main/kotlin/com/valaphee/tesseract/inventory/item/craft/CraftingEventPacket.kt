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

package com.valaphee.tesseract.inventory.item.craft

import com.valaphee.tesseract.inventory.item.stack.Stack
import com.valaphee.tesseract.inventory.item.stack.readStack
import com.valaphee.tesseract.inventory.item.stack.readStackPre431
import com.valaphee.tesseract.inventory.item.stack.writeStack
import com.valaphee.tesseract.inventory.item.stack.writeStackPre431
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
class CraftingEventPacket(
    val windowId: Int,
    val type: Type,
    val recipeId: UUID,
    val inputs: Array<Stack?>,
    val outputs: Array<Stack?>,
) : Packet {
    enum class Type {
        Inventory, Crafting, Workbench
    }

    override val id get() = 0x35

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeByte(windowId)
        buffer.writeVarInt(type.ordinal)
        buffer.writeUuid(recipeId)
        buffer.writeVarUInt(inputs.size)
        inputs.forEach { if (version >= 431) buffer.writeStack(it) else buffer.writeStackPre431(it) }
        buffer.writeVarUInt(outputs.size)
        outputs.forEach { if (version >= 431) buffer.writeStack(it) else buffer.writeStackPre431(it) }
    }

    override fun handle(handler: PacketHandler) = handler.craftingEvent(this)

    override fun toString() = "CraftingEventPacket(windowId=$windowId, type=$type, recipeId=$recipeId, inputs=${inputs.contentToString()}, outputs=${outputs.contentToString()})"
}

/**
 * @author Kevin Ludwig
 */
object CraftingEventPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = CraftingEventPacket(buffer.readUnsignedByte().toInt(), CraftingEventPacket.Type.values()[buffer.readVarInt()], buffer.readUuid(), Array(buffer.readVarUInt()) { if (version >= 431) buffer.readStack() else buffer.readStackPre431() }, Array(buffer.readVarUInt()) { if (version >= 431) buffer.readStack() else buffer.readStackPre431() })
}
