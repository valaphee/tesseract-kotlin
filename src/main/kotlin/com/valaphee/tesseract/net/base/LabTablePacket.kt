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

package com.valaphee.tesseract.net.base

import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader

/**
 * @author Kevin Ludwig
 */
class LabTablePacket(
    val action: Action,
    val position: Int3,
    val reactionType: ReactionType
) : Packet() {
    enum class Action {
        Combine, React, Reset
    }

    enum class ReactionType {
        None,
        IceBomb,
        Bleach,
        ElephantToothpaste,
        Fertilizer,
        HeatBlock,
        MagnesiumSalts,
        MiscFire,
        MiscExplosion,
        MiscLava,
        MiscMystical,
        MiscSmoke,
        MiscLargeSmoke
    }

    override val id get() = 0x6D

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeByte(action.ordinal)
        buffer.writeInt3(position)
        buffer.writeByte(reactionType.ordinal)
    }

    override fun handle(handler: PacketHandler) = handler.labTable(this)

    override fun toString() = "LabTablePacket(action=$action, position=$position, reactionType=$reactionType)"
}

/**
 * @author Kevin Ludwig
 */
object LabTablePacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = LabTablePacket(LabTablePacket.Action.values()[buffer.readByte().toInt()], buffer.readInt3(), LabTablePacket.ReactionType.values()[buffer.readByte().toInt()])
}
