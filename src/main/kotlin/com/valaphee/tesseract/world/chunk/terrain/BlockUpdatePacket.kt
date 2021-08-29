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
import java.util.EnumSet

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Clientbound)
data class BlockUpdatePacket(
    var position: Int3,
    var runtimeId: Int,
    var flags: Collection<Flag>,
    var layerId: Int
) : Packet {
    enum class Flag {
        Neighbors, Network, NonVisual, Priority;

        companion object {
            val All: EnumSet<Flag> = EnumSet.of(Neighbors, Network)
            val AllPriority: EnumSet<Flag> = EnumSet.of(Neighbors, Network, Priority)
        }
    }

    override val id get() = 0x15

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeInt3UnsignedY(position)
        buffer.writeVarUInt(runtimeId)
        buffer.writeVarUIntFlags(flags)
        buffer.writeVarUInt(layerId)
    }

    override fun handle(handler: PacketHandler) = handler.blockUpdate(this)
}
