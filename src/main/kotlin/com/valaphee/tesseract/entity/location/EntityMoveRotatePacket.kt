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
import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader

/**
 * @author Kevin Ludwig
 */
class EntityMoveRotatePacket(
    val runtimeEntityId: Long,
    val positionDelta: Int3,
    val position: Float3,
    val rotation: Float2,
    val headRotationYaw: Float,
    val onGround: Boolean,
    val immediate: Boolean,
    val force: Boolean
) : Packet() {
    override val id get() = 0x6F

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarULong(runtimeEntityId)
        val flagsIndex = buffer.writerIndex()
        buffer.writeZero(Short.SIZE_BYTES)
        var flagsValue = 0
        val (rawX, rawY, rawZ) = positionDelta
        val (x, y, z) = position
        if (version >= 419) {
            if (!x.isNaN()) {
                buffer.writeFloatLE(x)
                flagsValue = flagsValue or flagHasX
            }
            if (!y.isNaN()) {
                buffer.writeFloatLE(y)
                flagsValue = flagsValue or flagHasY
            }
            if (!z.isNaN()) {
                buffer.writeFloatLE(z)
                flagsValue = flagsValue or flagHasZ
            }
        } else {
            if (rawX != 0) {
                buffer.writeVarInt(rawX)
                flagsValue = flagsValue or flagHasX
            }
            if (rawY != 0) {
                buffer.writeVarInt(rawY)
                flagsValue = flagsValue or flagHasY
            }
            if (rawZ != 0) {
                buffer.writeVarInt(rawZ)
                flagsValue = flagsValue or flagHasZ
            }
        }
        val (pitch, yaw) = rotation
        if (!pitch.isNaN()) {
            buffer.writeAngle(pitch)
            flagsValue = flagsValue or flagHasPitch
        }
        if (!yaw.isNaN()) {
            buffer.writeAngle(yaw)
            flagsValue = flagsValue or flagHasYaw
        }
        if (!headRotationYaw.isNaN()) {
            buffer.writeAngle(headRotationYaw)
            flagsValue = flagsValue or flagHasHeadYaw
        }
        if (onGround) flagsValue = flagsValue or flagOnGround
        if (immediate) flagsValue = flagsValue or flagImmediate
        if (force) flagsValue = flagsValue or flagForce
        buffer.setShortLE(flagsIndex, flagsValue)
    }

    override fun handle(handler: PacketHandler) = handler.entityMoveRotate(this)

    override fun toString() = "EntityMoveRotatePacket(runtimeEntityId=$runtimeEntityId, positionDelta=$positionDelta, position=$position, rotation=$rotation, headRotationYaw=$headRotationYaw, onGround=$onGround, immediate=$immediate, force=$force)"

    companion object {
        internal const val flagHasX = 1 shl 0
        internal const val flagHasY = 1 shl 1
        internal const val flagHasZ = 1 shl 2
        internal const val flagHasPitch = 1 shl 3
        internal const val flagHasYaw = 1 shl 4
        internal const val flagHasHeadYaw = 1 shl 5
        internal const val flagOnGround = 1 shl 6
        internal const val flagImmediate = 1 shl 7
        internal const val flagForce = 1 shl 8
    }
}

/**
 * @author Kevin Ludwig
 */
object EntityMoveRotatePacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): EntityMoveRotatePacket {
        val runtimeEntityId = buffer.readVarULong()
        val flagsValue = buffer.readShortLE().toInt()
        val position: Float3
        val positionDelta: Int3
        if (version >= 419) {
            position = Float3(
                if (flagsValue and EntityMoveRotatePacket.flagHasX != 0) buffer.readFloatLE() else Float.NaN,
                if (flagsValue and EntityMoveRotatePacket.flagHasY != 0) buffer.readFloatLE() else Float.NaN,
                if (flagsValue and EntityMoveRotatePacket.flagHasZ != 0) buffer.readFloatLE() else Float.NaN
            )
            positionDelta = Int3.Zero
        } else {
            position = Float3.Zero
            positionDelta = Int3(
                if (flagsValue and EntityMoveRotatePacket.flagHasX != 0) buffer.readVarInt() else 0,
                if (flagsValue and EntityMoveRotatePacket.flagHasY != 0) buffer.readVarInt() else 0,
                if (flagsValue and EntityMoveRotatePacket.flagHasZ != 0) buffer.readVarInt() else 0
            )
        }
        val rotation = Float2(
            if (flagsValue and EntityMoveRotatePacket.flagHasPitch != 0) buffer.readAngle() else Float.NaN,
            if (flagsValue and EntityMoveRotatePacket.flagHasYaw != 0) buffer.readAngle() else Float.NaN
        )
        val headRotationYaw = if (flagsValue and EntityMoveRotatePacket.flagHasHeadYaw != 0) buffer.readAngle() else Float.NaN
        val onGround = flagsValue and EntityMoveRotatePacket.flagOnGround != 0
        val immediate = flagsValue and EntityMoveRotatePacket.flagImmediate != 0
        val force = flagsValue and EntityMoveRotatePacket.flagForce != 0
        return EntityMoveRotatePacket(runtimeEntityId, positionDelta, position, rotation, headRotationYaw, onGround, immediate, force)
    }
}
