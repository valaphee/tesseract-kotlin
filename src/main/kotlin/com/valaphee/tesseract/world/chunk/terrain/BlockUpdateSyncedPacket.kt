/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain

import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Clientbound)
data class BlockUpdateSyncedPacket(
    var position: Int3,
    var runtimeId: Int,
    var flags: Collection<BlockUpdatePacket.Flag>,
    var layerId: Int,
    var runtimeEntityId: Long,
    var type: Type
) : Packet {
    enum class Type {
        None, Create, Destroy
    }

    override val id get() = 0x6E

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeInt3UnsignedY(position)
        buffer.writeVarUInt(runtimeId)
        buffer.writeVarUIntFlags(flags)
        buffer.writeVarUInt(layerId)
        buffer.writeVarULong(runtimeEntityId)
        buffer.writeVarULong(type.ordinal.toLong())
    }

    override fun handle(handler: PacketHandler) = handler.blockUpdateSynced(this)
}
