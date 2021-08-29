/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.inventory

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler

/**
 * @author Kevin Ludwig
 */
data class WindowClosePacket(
    var windowId: Int,
    var serverside: Boolean
) : Packet {
    override val id get() = 0x2F

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeByte(windowId)
        if (version >= 419) buffer.writeBoolean(serverside)
    }

    override fun handle(handler: PacketHandler) = handler.windowClose(this)
}
