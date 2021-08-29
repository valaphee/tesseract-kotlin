/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.inventory

import com.valaphee.tesseract.item.stack.Stack
import com.valaphee.tesseract.item.stack.writeStack
import com.valaphee.tesseract.item.stack.writeStackPre431
import com.valaphee.tesseract.item.stack.writeStackWithNetIdPre431
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Clientbound)
data class InventorySlotPacket(
    var windowId: Int,
    var slotId: Int,
    var stack: Stack<*>?
) : Packet {
    override val id get() = 0x32

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarUInt(windowId)
        buffer.writeVarUInt(slotId)
        if (version >= 431) buffer.writeStack(stack) else if (version >= 407) buffer.writeStackWithNetIdPre431(stack) else buffer.writeStackPre431(stack)
    }

    override fun handle(handler: PacketHandler) = handler.inventorySlot(this)
}
