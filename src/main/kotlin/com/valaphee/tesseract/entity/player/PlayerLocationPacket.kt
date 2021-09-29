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

package com.valaphee.tesseract.entity.player

import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader

/**
 * @author Kevin Ludwig
 */
data class PlayerLocationPacket(
    val runtimeEntityId: Long,
    val position: Float3,
    val rotation: Float2,
    val headRotationYaw: Float,
    val mode: Mode,
    val onGround: Boolean,
    val drivingRuntimeEntityId: Long,
    val teleportationCause: TeleportationCause = TeleportationCause.Unknown,
    val tick: Long
) : Packet {
    enum class Mode {
        Normal, Reset, Teleport, Vehicle
    }

    enum class TeleportationCause {
        Unknown, Projectile, ChorusFruit, Command, Behavior
    }

    override val id get() = 0x13

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarULong(runtimeEntityId)
        buffer.writeFloat3(position)
        buffer.writeFloat2(rotation)
        buffer.writeFloatLE(headRotationYaw)
        buffer.writeByte(mode.ordinal)
        buffer.writeBoolean(onGround)
        buffer.writeVarULong(drivingRuntimeEntityId)
        if (mode == Mode.Teleport) {
            buffer.writeIntLE(teleportationCause.ordinal)
            buffer.writeIntLE(0) // TODO
        }
        if (version >= 419) buffer.writeVarULong(tick)
    }

    override fun handle(handler: PacketHandler) = handler.playerLocation(this)
}

/**
 * @author Kevin Ludwig
 */
object PlayerLocationPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): PlayerLocationPacket {
        val runtimeEntityId = buffer.readVarULong()
        val position = buffer.readFloat3()
        val rotation = buffer.readFloat2()
        val headRotationYaw = buffer.readFloatLE()
        val mode = PlayerLocationPacket.Mode.values()[buffer.readUnsignedByte().toInt()]
        val onGround = buffer.readBoolean()
        val drivingRuntimeEntityId = buffer.readVarULong()
        val teleportationCause: PlayerLocationPacket.TeleportationCause
        if (mode == PlayerLocationPacket.Mode.Teleport) {
            teleportationCause = PlayerLocationPacket.TeleportationCause.values()[buffer.readIntLE()]
            buffer.readIntLE() // TODO
        } else {
            teleportationCause = PlayerLocationPacket.TeleportationCause.Unknown
        }
        val tick = if (version >= 419) buffer.readVarULong() else 0
        return PlayerLocationPacket(runtimeEntityId, position, rotation, headRotationYaw, mode, onGround, drivingRuntimeEntityId, teleportationCause, tick)
    }
}
