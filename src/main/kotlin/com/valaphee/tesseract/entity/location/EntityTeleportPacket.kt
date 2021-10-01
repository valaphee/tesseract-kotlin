/*
 * MIT License
 *
 * Copyright (c) 2021, Valaphee.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.valaphee.tesseract.entity.location

import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader

/**
 * @author Kevin Ludwig
 */
class EntityTeleportPacket(
    val runtimeEntityId: Long,
    val position: Float3,
    val rotation: Float2,
    val headRotationYaw: Float,
    val onGround: Boolean,
    val immediate: Boolean
) : Packet {
    override val id get() = 0x12

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarULong(runtimeEntityId)
        var flagsValue = if (onGround) flagOnGround else 0
        if (immediate) flagsValue = flagsValue or flagImmediate
        buffer.writeByte(flagsValue)
        buffer.writeFloat3(position)
        buffer.writeAngle2(rotation)
        buffer.writeAngle(headRotationYaw)
    }

    override fun handle(handler: PacketHandler) = handler.entityTeleport(this)

    override fun toString() = "EntityTeleportPacket(runtimeEntityId=$runtimeEntityId, position=$position, rotation=$rotation, headRotationYaw=$headRotationYaw, onGround=$onGround, immediate=$immediate)"

    companion object {
        internal const val flagOnGround = 1 shl 0
        internal const val flagImmediate = 1 shl 1
    }
}

/**
 * @author Kevin Ludwig
 */
object EntityTeleportPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): EntityTeleportPacket {
        val runtimeEntityId = buffer.readVarULong()
        val flagsValue = buffer.readUnsignedByte().toInt()
        val onGround = flagsValue and EntityTeleportPacket.flagOnGround != 0
        val immediate = flagsValue and EntityTeleportPacket.flagImmediate != 0
        val position = buffer.readFloat3()
        val rotation = buffer.readAngle2()
        val headRotationYaw = buffer.readAngle()
        return EntityTeleportPacket(runtimeEntityId, position, rotation, headRotationYaw, onGround, immediate)
    }
}
