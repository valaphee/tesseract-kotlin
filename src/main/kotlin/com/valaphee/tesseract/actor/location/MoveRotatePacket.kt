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

package com.valaphee.tesseract.actor.location

import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.actor.AnyActorOfWorld
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler

/**
 * @author Kevin Ludwig
 */
data class MoveRotatePacket(
    var actor: AnyActorOfWorld,
    var positionDelta: Int3,
    var position: Float3,
    var rotation: Float2,
    var headRotationYaw: Float,
    var onGround: Boolean,
    var immediate: Boolean,
    var force: Boolean
) : Packet {
    override val id get() = 0x6F

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarULong(actor.id)
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

    override fun handle(handler: PacketHandler) = handler.moveRotate(this)

    companion object {
        private const val flagHasX = 1 shl 0
        private const val flagHasY = 1 shl 1
        private const val flagHasZ = 1 shl 2
        private const val flagHasPitch = 1 shl 3
        private const val flagHasYaw = 1 shl 4
        private const val flagHasHeadYaw = 1 shl 5
        private const val flagOnGround = 1 shl 6
        private const val flagImmediate = 1 shl 7
        private const val flagForce = 1 shl 8
    }
}
