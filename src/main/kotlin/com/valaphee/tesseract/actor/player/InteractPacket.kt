/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.player

import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.actor.Actor
import com.valaphee.tesseract.actor.AnyActorOfWorld
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.world.WorldContext

/**
 * @author Kevin Ludwig
 */
data class InteractPacket(
    var action: Action,
    var actor: AnyActorOfWorld?,
    var mousePosition: Float3?
) : Packet {
    enum class Action {
        None, Interact, Damage, LeaveVehicle, Mouseover, NpcOpen, OpenInventory
    }

    override val id get() = 0x21

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeByte(action.ordinal)
        buffer.writeVarULong(actor?.id ?: 0)
        if (action == Action.Mouseover || action == Action.NpcOpen) buffer.writeFloat3(mousePosition!!)
    }

    override fun handle(handler: PacketHandler) = handler.interact(this)
}

/**
 * @author Kevin Ludwig
 */
class InteractPacketReader(
    private val context: WorldContext
) : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): InteractPacket {
        val action = InteractPacket.Action.values()[buffer.readByte().toInt()]
        @Suppress("UNCHECKED_CAST")
        val actor = context.engine.findEntityOrNull(buffer.readVarULong()) as? Actor
        val mousePosition = if (action == InteractPacket.Action.Mouseover || action == InteractPacket.Action.NpcOpen) buffer.readFloat3() else null
        return InteractPacket(action, actor, mousePosition)
    }
}
