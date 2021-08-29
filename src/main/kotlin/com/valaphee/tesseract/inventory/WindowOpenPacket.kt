/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.inventory

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
data class WindowOpenPacket(
    var windowId: Int,
    var type: WindowType,
    var blockPosition: Int3,
    var uniqueEntityId: Long
) : Packet {
    override val id get() = 0x2E

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeByte(windowId)
        buffer.writeByte(type.id)
        buffer.writeInt3UnsignedY(blockPosition)
        buffer.writeVarLong(uniqueEntityId)
    }

    override fun handle(handler: PacketHandler) = handler.windowOpen(this)
}
