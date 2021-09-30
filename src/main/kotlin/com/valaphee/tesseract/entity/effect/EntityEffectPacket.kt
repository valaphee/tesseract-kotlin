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
 *
 */

package com.valaphee.tesseract.entity.effect

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
data class EntityEffectPacket(
    val runtimeEntityId: Long,
    val action: Action,
    val effect: Effect,
    val amplifier: Int,
    val particles: Boolean,
    val duration: Int
) : Packet {
    enum class Action {
        Apply, Modify, Revoke
    }

    override val id get() = 0x1C

    constructor(runtimeEntityId: Long, action: Action, effect: Effect) : this(runtimeEntityId, action, effect, 0, false, 0)

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarULong(runtimeEntityId)
        buffer.writeByte(action.ordinal + 1)
        buffer.writeVarInt(effect.ordinal + 1)
        buffer.writeVarInt(amplifier)
        buffer.writeBoolean(particles)
        buffer.writeVarInt(duration)
    }

    override fun handle(handler: PacketHandler) = handler.entityEffect(this)
}

/**
 * @author Kevin Ludwig
 */
object EntityEffectPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = EntityEffectPacket(
        buffer.readVarULong(),
        EntityEffectPacket.Action.values()[buffer.readByte().toInt() - 1],
        Effect.values()[buffer.readVarInt() - 1],
        buffer.readVarInt(),
        buffer.readBoolean(),
        buffer.readVarInt()
    )
}
