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

package com.valaphee.tesseract.actor.stack

import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.actor.metadata.Metadata
import com.valaphee.tesseract.inventory.item.stack.Stack
import com.valaphee.tesseract.inventory.item.stack.readStack
import com.valaphee.tesseract.inventory.item.stack.readStackPre431
import com.valaphee.tesseract.inventory.item.stack.writeStack
import com.valaphee.tesseract.inventory.item.stack.writeStackPre431
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
data class StackAddPacket(
    val uniqueEntityId: Long,
    val runtimeEntityId: Long,
    val stack: Stack<*>?,
    val position: Float3,
    val velocity: Float3,
    val metadata: Metadata,
    val fromFishing: Boolean
) : Packet {
    override val id get() = 0x0F

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarLong(uniqueEntityId)
        buffer.writeVarULong(runtimeEntityId)
        if (version >= 431) buffer.writeStack(stack) else buffer.writeStackPre431(stack)
        buffer.writeFloat3(position)
        buffer.writeFloat3(velocity)
        metadata.writeToBuffer(buffer)
        buffer.writeBoolean(fromFishing)
    }

    override fun handle(handler: PacketHandler) = handler.stackAdd(this)
}

/**
 * @author Kevin Ludwig
 */
object StackAddPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = StackAddPacket(
        buffer.readVarLong(),
        buffer.readVarULong(),
        if (version >= 431) buffer.readStack() else buffer.readStackPre431(),
        buffer.readFloat3(),
        buffer.readFloat3(),
        Metadata().apply { readFromBuffer(buffer) },
        buffer.readBoolean()
    )
}
