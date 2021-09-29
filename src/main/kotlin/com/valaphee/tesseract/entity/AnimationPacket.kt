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
import com.valaphee.tesseract.util.Int2ObjectOpenHashBiMap

/**
 * @author Kevin Ludwig
 */
data class AnimationPacket(
    val animation: Animation,
    val runtimeEntityId: Long,
    val rowingTime: Float = 0.0f
) : Packet {
    enum class Animation {
        NoAction, SwingArm, WakeUp, CriticalHit, MagicCriticalHit, RowRight, RowLeft
    }

    override val id get() = 0x2C

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarInt(animations.getKey(animation))
        buffer.writeVarULong(runtimeEntityId)
        if (animation == Animation.RowRight || animation == Animation.RowLeft) buffer.writeFloatLE(rowingTime)
    }

    override fun handle(handler: PacketHandler) = handler.animation(this)

    companion object {
        internal val animations = Int2ObjectOpenHashBiMap<Animation>().apply {
            this[0x00] = Animation.NoAction
            this[0x01] = Animation.SwingArm
            this[0x03] = Animation.WakeUp
            this[0x04] = Animation.CriticalHit
            this[0x05] = Animation.MagicCriticalHit
            this[0x80] = Animation.RowRight
            this[0x81] = Animation.RowLeft
        }
    }
}

/**
 * @author Kevin Ludwig
 */
object AnimationPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): AnimationPacket {
        val animation = AnimationPacket.animations[buffer.readVarInt()]
        val runtimeEntityId = buffer.readVarULong()
        val rowingTime = if (animation == AnimationPacket.Animation.RowRight || animation == AnimationPacket.Animation.RowLeft) buffer.readFloatLE() else 0.0f
        return AnimationPacket(animation, runtimeEntityId, rowingTime)
    }
}
