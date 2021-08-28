/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net.init

import com.valaphee.tesseract.item.stack.Stack
import com.valaphee.tesseract.item.stack.writeStackInstance
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
data class CreativeInventoryPacket(
    var content: Array<Stack<*>?>
) : Packet {
    override val id get() = 0x91

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarUInt(content.size)
        content.forEach {
            if (version >= 431) {
                buffer.writeVarUInt(it?.netId ?: 0)
                buffer.writeStackInstance(it)
            } else buffer.writeStackWithNetIdPre431(it)
        }
    }

    override fun handle(handler: PacketHandler) = handler.creativeInventory(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreativeInventoryPacket

        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode() = content.contentHashCode()
}
