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
internal class ListTagImpl(
    override var name: String,
    private val value: MutableList<Tag> = mutableListOf(),
    private var valueType: TagType? = null
) : ListTag {
    override val type get() = TagType.List

    override fun toArray() = value.toTypedArray()

    override fun toList() = value

    override fun has(index: Int) = index < value.size

    override fun get(index: Int) = value[index]

    override fun put(tag: Tag) {
        value += tag
    }

    override fun putByte(value: Byte) {
        this.value += ByteTagImpl("", value)
    }

    override fun putShort(value: Short) {
        this.value += ShortTagImpl("", value)
    }

    override fun putInt(value: Int) {
        this.value += IntTagImpl("", value)
    }

    override fun putLong(value: Long) {
        this.value += LongTagImpl("", value)
    }

    override fun putFloat(value: Float) {
        this.value += FloatTagImpl("", value)
    }

    override fun putDouble(value: Double) {
        this.value += DoubleTagImpl("", value)
    }

    override fun putByteArray(value: ByteArray) {
        this.value += ByteArrayTagImpl("", value)
    }

    override fun putString(value: String) {
        this.value += StringTagImpl("", value)
    }

    override fun putIntArray(value: IntArray) {
        this.value += IntArrayTagImpl("", value)
    }

    override fun putLongArray(value: LongArray) {
        this.value += LongArrayTagImpl("", value)
    }

    override fun set(index: Int, tag: Tag) {
        value[index] = tag
    }

    override fun setByte(index: Int, value: Byte) {
        this.value[index] = ByteTagImpl("", value)
    }

    override fun setShort(index: Int, value: Short) {
        this.value[index] = ShortTagImpl("", value)
    }

    override fun setInt(index: Int, value: Int) {
        this.value[index] = IntTagImpl("", value)
    }

    override fun setLong(index: Int, value: Long) {
        this.value[index] = LongTagImpl("", value)
    }

    override fun setFloat(index: Int, value: Float) {
        this.value[index] = FloatTagImpl("", value)
    }

    override fun setDouble(index: Int, value: Double) {
        this.value[index] = DoubleTagImpl("", value)
    }

    override fun setByteArray(index: Int, value: ByteArray) {
        this.value[index] = ByteArrayTagImpl("", value)
    }

    override fun setString(index: Int, value: String) {
        this.value[index] = StringTagImpl("", value)
    }

    override fun setIntArray(index: Int, value: IntArray) {
        this.value[index] = IntArrayTagImpl("", value)
    }

    override fun setLongArray(index: Int, value: LongArray) {
        this.value[index] = LongArrayTagImpl("", value)
    }

    override fun remove(index: Int): Tag = value.removeAt(index)

    override val size get() = value.size

    override val isEmpty get() = value.isEmpty()

    override fun read(input: DataInput, depth: Int, remainingBytes: IntArray) {
        if (depth == 0) throw NbtException("Reached maximum allowed depth")

        value.clear()

        remainingBytes[0] -= Byte.SIZE_BYTES + Int.SIZE_BYTES
        if (remainingBytes[0] < 0) throw NbtException("Reached maximum allowed size")

        valueType = TagType.values()[input.readUnsignedByte()].also {
            val valueCount = input.readInt()
            if (it == TagType.End && valueCount > 0) throw NbtException("Reached maximum allowed size")

            repeat(valueCount) { _ -> value += it.tag!!.invoke("").apply { read(input, depth - 1, remainingBytes) } }
        }
    }

    override fun write(output: DataOutput) {
        if (valueType == null) valueType = if (value.isEmpty()) TagType.End else value[0].type

        output.writeByte(valueType!!.ordinal)
        output.writeInt(value.size)
        value.forEach { it.write(output) }
    }

    override fun print(string: StringBuilder) {
        string.append('[')
        value.forEach { string.append(it).append(',') }
        if (value.isEmpty()) string.append(']') else string.setCharAt(string.length - 1, ']')
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ListTagImpl

        if (value != other.value) return false

        return true
    }

    override fun hashCode() = value.hashCode()

    override fun toString() = StringBuilder().apply(this::print).toString()
}
