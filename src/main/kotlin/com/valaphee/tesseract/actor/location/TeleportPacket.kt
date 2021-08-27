/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.location

import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.actor.AnyActorOfWorld
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler

/**
 * @author Kevin Ludwig
 */
data class TeleportPacket(
    var entity: AnyActorOfWorld,
    var position: Float3,
    var rotation: Float2,
    var headRotationYaw: Float,
    var onGround: Boolean,
    var immediate: Boolean
) : Packet {
    override val id get() = 0x12

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarULong(entity.id)
        var flagsValue = if (onGround) flagOnGround else 0
        if (immediate) flagsValue = flagsValue or flagImmediate
        buffer.writeByte(flagsValue)
        buffer.writeFloat3(position)
        buffer.writeAngle2(rotation)
        buffer.writeAngle(headRotationYaw)
    }

    override fun handle(handler: PacketHandler) = handler.teleport(this)

    companion object {
        private const val flagOnGround = 1 shl 0
        private const val flagImmediate = 1 shl 1
    }
}
