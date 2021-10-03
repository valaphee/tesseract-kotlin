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

package com.valaphee.tesseract.inventory

import com.valaphee.tesseract.inventory.item.Enchantment
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
class EnchantOptionsPacket(
    val options: Array<Option>
) : Packet {
    class Slot(
        val enchantment: Enchantment,
        val level: Short
    )

    class Option(
        val cost: Int,
        val primarySlotId: Int,
        val slots1: Array<Slot>,
        val slots2: Array<Slot>,
        val slots3: Array<Slot>,
        val description: String,
        val netId: Int
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Option

            if (cost != other.cost) return false
            if (primarySlotId != other.primarySlotId) return false
            if (!slots1.contentEquals(other.slots1)) return false
            if (!slots2.contentEquals(other.slots2)) return false
            if (!slots3.contentEquals(other.slots3)) return false
            if (description != other.description) return false
            if (netId != other.netId) return false

            return true
        }

        override fun hashCode(): Int {
            var result = cost
            result = 31 * result + primarySlotId
            result = 31 * result + slots1.contentHashCode()
            result = 31 * result + slots2.contentHashCode()
            result = 31 * result + slots3.contentHashCode()
            result = 31 * result + description.hashCode()
            result = 31 * result + netId
            return result
        }
    }

    override val id get() = 0x92

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarUInt(options.size)
        options.forEach {
            buffer.writeVarUInt(it.cost)
            buffer.writeIntLE(it.primarySlotId)
            buffer.writeVarUInt(it.slots1.size)
            it.slots1.forEach {
                buffer.writeByte(it.enchantment.ordinal)
                buffer.writeByte(it.level.toInt())
            }
            buffer.writeVarUInt(it.slots2.size)
            it.slots2.forEach {
                buffer.writeByte(it.enchantment.ordinal)
                buffer.writeByte(it.level.toInt())
            }
            buffer.writeVarUInt(it.slots3.size)
            it.slots3.forEach {
                buffer.writeByte(it.enchantment.ordinal)
                buffer.writeByte(it.level.toInt())
            }
            buffer.writeString(it.description)
            buffer.writeVarUInt(it.netId)
        }
    }

    override fun handle(handler: PacketHandler) = handler.enchantOptions(this)

    override fun toString() = "EnchantOptionsPacket(options=${options.contentToString()})"
}

/**
 * @author Kevin Ludwig
 */
object EnchantOptionsPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = EnchantOptionsPacket(Array(buffer.readVarUInt()) {
        EnchantOptionsPacket.Option(
            buffer.readVarUInt(),
            buffer.readIntLE(),
            Array(buffer.readVarUInt()) { EnchantOptionsPacket.Slot(Enchantment.values()[buffer.readUnsignedByte().toInt()], buffer.readUnsignedByte()) },
            Array(buffer.readVarUInt()) { EnchantOptionsPacket.Slot(Enchantment.values()[buffer.readUnsignedByte().toInt()], buffer.readUnsignedByte()) },
            Array(buffer.readVarUInt()) { EnchantOptionsPacket.Slot(Enchantment.values()[buffer.readUnsignedByte().toInt()], buffer.readUnsignedByte()) },
            buffer.readString(),
            buffer.readVarUInt()
        )
    })
}
