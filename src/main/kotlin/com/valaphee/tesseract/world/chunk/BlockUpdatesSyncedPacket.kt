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
@Restrict(Restriction.ToClient)
class BlockUpdatesSyncedPacket(
    val subChunk: Int3,
    val updates1: Array<Update>,
    val updates2: Array<Update>
) : Packet {
    data class Update(
        val position: Int3,
        val runtimeId: Int,
        val flags: Collection<BlockUpdatePacket.Flag>,
        val runtimeEntityId: Long,
        val type: BlockUpdateSyncedPacket.Type
    )

    override val id get() = 0xAC

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeInt3UnsignedY(subChunk)
        buffer.writeVarUInt(updates1.size)
        updates1.forEach {
            buffer.writeInt3UnsignedY(it.position)
            buffer.writeVarUInt(it.runtimeId)
            buffer.writeVarUIntFlags(it.flags)
            buffer.writeVarULong(it.runtimeEntityId)
            buffer.writeVarULong(it.type.ordinal.toLong())
        }
        buffer.writeVarUInt(updates2.size)
        updates2.forEach {
            buffer.writeInt3UnsignedY(it.position)
            buffer.writeVarUInt(it.runtimeId)
            buffer.writeVarUIntFlags(it.flags)
            buffer.writeVarULong(it.runtimeEntityId)
            buffer.writeVarULong(it.type.ordinal.toLong())
        }
    }

    override fun handle(handler: PacketHandler) = handler.blockUpdatesSynced(this)

    override fun toString() = "BlockUpdatesSyncedPacket(subChunk=$subChunk, updates1=${updates1.contentToString()}, updates2=${updates2.contentToString()})"
}

/**
 * @author Kevin Ludwig
 */
object BlockUpdatesSyncedPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = BlockUpdatesSyncedPacket(
        buffer.readInt3UnsignedY(),
        Array(buffer.readVarUInt()) { BlockUpdatesSyncedPacket.Update(buffer.readInt3UnsignedY(), buffer.readVarUInt(), buffer.readVarUIntFlags(), buffer.readVarULong(), BlockUpdateSyncedPacket.Type.values()[buffer.readVarULong().toInt()]) },
        Array(buffer.readVarUInt()) { BlockUpdatesSyncedPacket.Update(buffer.readInt3UnsignedY(), buffer.readVarUInt(), buffer.readVarUIntFlags(), buffer.readVarULong(), BlockUpdateSyncedPacket.Type.values()[buffer.readVarULong().toInt()]) }
    )
}
