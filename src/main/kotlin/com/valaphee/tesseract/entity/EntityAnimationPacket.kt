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

package com.valaphee.tesseract.entity

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.util.Registry

/**
 * @author Kevin Ludwig
 */
class EntityAnimationPacket(
    val animation: Animation,
    val runtimeEntityId: Long,
    val rowingTime: Float = 0.0f
) : Packet {
    enum class Animation {
        NoAction, SwingArm, WakeUp, CriticalHit, MagicCriticalHit, RowRight, RowLeft;

        companion object {
            val registry = Registry<Animation>().apply {
                this[0x00] = NoAction
                this[0x01] = SwingArm
                this[0x03] = WakeUp
                this[0x04] = CriticalHit
                this[0x05] = MagicCriticalHit
                this[0x80] = RowRight
                this[0x81] = RowLeft
            }
        }
    }

    override val id get() = 0x2C

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarInt(Animation.registry.getId(animation))
        buffer.writeVarULong(runtimeEntityId)
        if (animation == Animation.RowRight || animation == Animation.RowLeft) buffer.writeFloatLE(rowingTime)
    }

    override fun handle(handler: PacketHandler) = handler.entityAnimation(this)

    override fun toString() = "EntityAnimationPacket(animation=$animation, runtimeEntityId=$runtimeEntityId, rowingTime=$rowingTime)"

    companion object {

    }
}

/**
 * @author Kevin Ludwig
 */
object EntityAnimationPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): EntityAnimationPacket {
        val animation = checkNotNull(EntityAnimationPacket.Animation.registry[buffer.readVarInt()])
        val runtimeEntityId = buffer.readVarULong()
        val rowingTime = if (animation == EntityAnimationPacket.Animation.RowRight || animation == EntityAnimationPacket.Animation.RowLeft) buffer.readFloatLE() else 0.0f
        return EntityAnimationPacket(animation, runtimeEntityId, rowingTime)
    }
}
