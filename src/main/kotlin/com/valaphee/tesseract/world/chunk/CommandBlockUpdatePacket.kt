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

package com.valaphee.tesseract.world.chunk

import com.valaphee.foundry.math.Int3
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
data class CommandBlockUpdatePacket(
    val block: Boolean,
    val blockPosition: Int3?,
    val minecartRuntimeEntityId: Long,
    val name: String,
    val command: String,
    val lastOutput: String,
    val mode: Mode?,
    val powered: Boolean,
    val conditionMet: Boolean,
    val outputTracked: Boolean,
    val executeOnFirstTick: Boolean,
    val tickDelay: Long = 0
) : Packet {
    enum class Mode {
        Impulse, Repeat, Chain
    }

    override val id get() = 0x4E

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeBoolean(block)
        if (block) {
            buffer.writeInt3UnsignedY(blockPosition!!)
            buffer.writeVarUInt(mode!!.ordinal)
            buffer.writeBoolean(powered)
            buffer.writeBoolean(conditionMet)
        } else buffer.writeVarULong(minecartRuntimeEntityId)
        buffer.writeString(command)
        buffer.writeString(lastOutput)
        buffer.writeString(name)
        buffer.writeBoolean(outputTracked)
        buffer.writeIntLE(tickDelay.toInt())
        buffer.writeBoolean(executeOnFirstTick)
    }

    override fun handle(handler: PacketHandler) = handler.commandBlockUpdate(this)
}

/**
 * @author Kevin Ludwig
 */
object CommandBlockUpdatePacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): CommandBlockUpdatePacket {
        val block = buffer.readBoolean()
        val blockPosition: Int3?
        val minecartRuntimeEntityId: Long
        val mode: CommandBlockUpdatePacket.Mode?
        val powered: Boolean
        val conditionMet: Boolean
        if (block) {
            blockPosition = buffer.readInt3UnsignedY()
            minecartRuntimeEntityId = 0L
            mode = CommandBlockUpdatePacket.Mode.values()[buffer.readVarUInt()]
            powered = buffer.readBoolean()
            conditionMet = buffer.readBoolean()
        } else {
            blockPosition = null
            minecartRuntimeEntityId = buffer.readVarULong()
            mode = null
            powered = false
            conditionMet = false
        }
        val command = buffer.readString()
        val lastOutput = buffer.readString()
        val name = buffer.readString()
        val outputTracked = buffer.readBoolean()
        val tickDelay = buffer.readUnsignedIntLE()
        val executeOnFirstTick = buffer.readBoolean()
        return CommandBlockUpdatePacket(block, blockPosition, minecartRuntimeEntityId, name, command, lastOutput, mode, powered, conditionMet, outputTracked, executeOnFirstTick, tickDelay)
    }
}
