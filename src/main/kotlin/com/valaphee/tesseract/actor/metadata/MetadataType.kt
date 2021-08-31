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

package com.valaphee.tesseract.actor.metadata

import com.valaphee.foundry.math.Float3
import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.util.nbt.Tag

/**
 * @author Kevin Ludwig
 */
interface MetadataType<T> {
    fun read(buffer: PacketBuffer): T

    fun write(buffer: PacketBuffer, value: T)

    companion object {
        val Byte = object : MetadataType<Number> {
            override fun read(buffer: PacketBuffer) = buffer.readByte()

            override fun write(buffer: PacketBuffer, value: Number) {
                buffer.writeByte(value.toInt())
            }
        }

        val Short = object : MetadataType<Number> {
            override fun read(buffer: PacketBuffer) = buffer.readShortLE()

            override fun write(buffer: PacketBuffer, value: Number) {
                buffer.writeShortLE(value.toInt())
            }
        }

        val Int = object : MetadataType<Number> {
            override fun read(buffer: PacketBuffer) = buffer.readVarInt()

            override fun write(buffer: PacketBuffer, value: Number) {
                buffer.writeVarInt(value.toInt())
            }
        }

        val Float = object : MetadataType<Number> {
            override fun read(buffer: PacketBuffer) = buffer.readFloatLE()

            override fun write(buffer: PacketBuffer, value: Number) {
                buffer.writeFloatLE(value.toFloat())
            }
        }

        val String = object : MetadataType<String> {
            override fun read(buffer: PacketBuffer) = buffer.readString()

            override fun write(buffer: PacketBuffer, value: String) {
                buffer.writeString(value)
            }
        }

        val Tag = object : MetadataType<Tag?> {
            override fun read(buffer: PacketBuffer) = buffer.toNbtInputStream().use { it.readTag() }

            override fun write(buffer: PacketBuffer, value: Tag?) {
                buffer.toNbtOutputStream().use { it.writeTag(value) }
            }
        }

        val Int3 = object : MetadataType<Int3> {
            override fun read(buffer: PacketBuffer) = buffer.readInt3()

            override fun write(buffer: PacketBuffer, value: Int3) {
                buffer.writeInt3(value)
            }
        }

        val Long = object : MetadataType<Number> {
            override fun read(buffer: PacketBuffer) = buffer.readVarLong()

            override fun write(buffer: PacketBuffer, value: Number) {
                buffer.writeVarLong(value.toLong())
            }
        }

        val Float3 = object : MetadataType<Float3> {
            override fun read(buffer: PacketBuffer) = buffer.readFloat3()

            override fun write(buffer: PacketBuffer, value: Float3) {
                buffer.writeFloat3(value)
            }
        }

        val Flags = object : MetadataType<Collection<Flag>> {
            override fun read(buffer: PacketBuffer) = buffer.readVarLongFlags<Flag>()

            override fun write(buffer: PacketBuffer, value: Collection<Flag>) {
                buffer.writeVarLongFlags(value)
            }
        }

        val Flags2 = object : MetadataType<Collection<Flag2>> {
            override fun read(buffer: PacketBuffer) = buffer.readVarLongFlags<Flag2>()

            override fun write(buffer: PacketBuffer, value: Collection<Flag2>) {
                buffer.writeVarLongFlags(value)
            }
        }
    }
}
