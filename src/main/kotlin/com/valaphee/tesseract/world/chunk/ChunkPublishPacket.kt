/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk

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
data class ChunkPublishPacket(
    var position: Int3? = null,
    var radius: Int = 0
) : Packet {
    override val id get() = 0x79

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeInt3(position!!)
        buffer.writeVarUInt(radius)
    }

    override fun handle(handler: PacketHandler) = handler.chunkPublish(this)
}
