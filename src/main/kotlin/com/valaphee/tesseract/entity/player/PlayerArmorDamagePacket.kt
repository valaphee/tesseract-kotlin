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
data class PlayerArmorDamagePacket(
    val damages: Array<Int?>
) : Packet {
    init {
        require(damages.size == 4)
    }

    override val id get() = 0x95

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.skipBytes(1)
        val writerIndex = buffer.writerIndex()
        var flagsValue = 0
        damages.forEachIndexed { i, damage ->
            damage?.let {
                flagsValue = flagsValue or (1 shl i)
                buffer.writeVarInt(it)
            }
        }
        buffer.setByte(writerIndex, flagsValue)
    }

    override fun handle(handler: PacketHandler) = handler.playerArmorDamage(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayerArmorDamagePacket

        if (!damages.contentEquals(other.damages)) return false

        return true
    }

    override fun hashCode(): Int {
        return damages.contentHashCode()
    }
}

/**
 * @author Kevin Ludwig
 */
object PlayerArmorDamagePacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): PlayerArmorDamagePacket {
        val flagsValue = buffer.readByte().toInt()
        val damages = arrayOfNulls<Int>(4)
        repeat(4) { damages[it] = if ((flagsValue and (1 shl it)) != 0) buffer.readVarInt() else null }
        return PlayerArmorDamagePacket(damages)
    }
}
