/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.inventory

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Clientbound)
data class WindowPropertyPacket(
    var windowId: Int,
    var property: Int,
    var value: Int
) : Packet {
    override val id get() = 0x33

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeByte(windowId)
        buffer.writeVarInt(property)
        buffer.writeVarInt(value)
    }

    override fun handle(handler: PacketHandler) = handler.windowProperty(this)
}
