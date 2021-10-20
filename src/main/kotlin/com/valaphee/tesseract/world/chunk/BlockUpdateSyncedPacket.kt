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
class BlockUpdateSyncedPacket(
    val position: Int3,
    val runtimeId: Int,
    val flags: Collection<BlockUpdatePacket.Flag>,
    val layer: Int,
    val runtimeEntityId: Long,
    val type: Type
) : Packet {
    enum class Type {
        None, Create, Destroy
    }

    constructor(position: Int3, runtimeId: Int, flags: Collection<BlockUpdatePacket.Flag>, layer: Int) : this(position, runtimeId, flags, layer, -1, Type.None)

    override val id get() = 0x6E

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeInt3UnsignedY(position)
        buffer.writeVarUInt(runtimeId)
        buffer.writeVarUIntFlags(flags)
        buffer.writeVarUInt(layer)
        buffer.writeVarULong(runtimeEntityId)
        buffer.writeVarULong(type.ordinal.toLong())
    }

    override fun handle(handler: PacketHandler) = handler.blockUpdateSynced(this)

    override fun toString() = "BlockUpdateSyncedPacket(position=$position, runtimeId=$runtimeId, flags=$flags, layer=$layer, runtimeEntityId=$runtimeEntityId, type=$type)"
}

/**
 * @author Kevin Ludwig
 */
object BlockUpdateSyncedPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = BlockUpdateSyncedPacket(buffer.readInt3UnsignedY(), buffer.readVarUInt(), buffer.readVarUIntFlags(), buffer.readVarUInt(), buffer.readVarULong(), BlockUpdateSyncedPacket.Type.values()[buffer.readVarULong().toInt()])
}
