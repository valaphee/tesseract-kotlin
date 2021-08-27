/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net.base

import com.google.gson.JsonElement
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Clientbound)
data class BehaviorTreePacket(
    var json: JsonElement? = null
) : Packet {
    override val id get() = 0x59

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeString(json.toString())
    }

    override fun handle(handler: PacketHandler) = handler.behaviorTree(this)
}
