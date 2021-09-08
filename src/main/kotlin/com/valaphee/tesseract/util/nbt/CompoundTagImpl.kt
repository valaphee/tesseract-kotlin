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

package com.valaphee.tesseract.util.nbt

import java.io.DataInput
import java.io.DataOutput

/**
 * @author Kevin Ludwig
 */
internal class CompoundTagImpl(
    override var name: String,
    private val value: MutableMap<String, Tag> = LinkedHashMap()
) : CompoundTag {
    override val type get() = TagType.Compound

    override fun toMap() = value

    override fun has(name: String) = value.containsKey(name)

    override fun get(name: String) = value[name]

    override fun set(name: String, tag: Tag) {
        value[name] = tag
    }

    override fun setByte(name: String, value: Byte) {
        this.value[name] = ByteTagImpl(name, value)
    }

    override fun setShort(name: String, value: Short) {
        this.value[name] = ShortTagImpl(name, value)
    }

    override fun setInt(name: String, value: Int) {
        this.value[name] = IntTagImpl(name, value)
    }

    override fun setLong(name: String, value: Long) {
        this.value[name] = LongTagImpl(name, value)
    }

    override fun setFloat(name: String, value: Float) {
        this.value[name] = FloatTagImpl(name, value)
    }

    override fun setDouble(name: String, value: Double) {
        this.value[name] = DoubleTagImpl(name, value)
    }

    override fun setByteArray(name: String, value: ByteArray) {
        this.value[name] = ByteArrayTagImpl(name, value)
    }

    override fun setString(name: String, value: String) {
        this.value[name] = StringTagImpl(name, value)
    }

    override fun setIntArray(name: String, value: IntArray) {
        this.value[name] = IntArrayTagImpl(name, value)
    }

    override fun setLongArray(name: String, value: LongArray) {
        this.value[name] = LongArrayTagImpl(name, value)
    }

    override fun remove(name: String): Tag? = value.remove(name)

    override val size get() = value.size

    override val isEmpty get() = value.isEmpty()

    override fun read(input: DataInput, depth: Int, remainingBytes: IntArray) {
        if (depth == 0) throw NbtException("Reached maximum allowed depth")

        value.clear()

        remainingBytes[0] -= Byte.SIZE_BYTES
        if (remainingBytes[0] < 0) throw NbtException("Reached maximum allowed size")

        var type = TagType.values()[input.readUnsignedByte()]
        while (type != TagType.End) {
            val name = input.readUTF()
            value[name] = type.tag!!.invoke(name).apply { read(input, depth - 1, remainingBytes) }

            remainingBytes[0] -= Byte.SIZE_BYTES
            if (remainingBytes[0] < 0) throw NbtException("Reached maximum allowed size")

            type = TagType.values()[input.readUnsignedByte()]
        }
    }

    override fun write(output: DataOutput) {
        value.forEach { (key, tag) ->
            output.writeByte(tag.type.ordinal)
            output.writeUTF(key)
            tag.write(output)
        }
        output.writeByte(TagType.End.ordinal)
    }

    override fun print(string: StringBuilder) {
        string.append('{')
        value.forEach { (key, tag) ->
            string.append(key).append(':')
            tag.print(string)
            string.append(',')
        }
        if (value.isEmpty()) string.append('}') else string.setCharAt(string.length - 1, '}')
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CompoundTagImpl

        if (value != other.value) return false

        return true
    }

    override fun hashCode() = value.hashCode()

    override fun toString() = StringBuilder().apply(this::print).toString()
}
