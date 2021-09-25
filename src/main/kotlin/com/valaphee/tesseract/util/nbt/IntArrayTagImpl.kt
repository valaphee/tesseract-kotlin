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
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

/**
 * @author Kevin Ludwig
 */
internal class IntArrayTagImpl(
    override var name: String,
    private var value: IntArray? = null
) : ArrayTag {
    override val type get() = TagType.IntArray

    override fun toByteArray() = ByteBuffer.allocate(value!!.size * Int.SIZE_BYTES).apply { asIntBuffer().put(value) }.array()

    override fun toIntArray() = value!!.clone()

    override fun toLongArray() = ByteBuffer.allocate(value!!.size * Int.SIZE_BYTES).apply { asIntBuffer().put(value) }.asLongBuffer().array()

    override fun valueToString() = String(toByteArray(), StandardCharsets.UTF_8)

    override fun read(input: DataInput, depth: Int, remainingBytes: IntArray) {
        remainingBytes[0] -= Int.SIZE_BYTES
        if (remainingBytes[0] < 0) throw NbtException("Reached maximum allowed size")

        val valueLength = input.readInt()
        remainingBytes[0] -= valueLength * Int.SIZE_BYTES
        if (remainingBytes[0] < 0) throw NbtException("Reached maximum allowed size")

        value = IntArray(valueLength) { input.readInt() }
    }

    override fun write(output: DataOutput) {
        value?.let {
            output.writeInt(it.size)
            it.forEach { output.writeInt(it) }
        } ?: output.writeInt(0)
    }

    override fun print(string: StringBuilder) {
        string.append("[I;")
        value?.let {
            it.forEach { string.append(it).append(',') }
            if (it.isEmpty()) string.append(']') else string.setCharAt(string.length - 1, ']')
        } ?: string.append(']')
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IntArrayTagImpl

        if (value != null) {
            if (other.value == null) return false
            if (!value.contentEquals(other.value)) return false
        } else if (other.value != null) return false

        return true
    }

    override fun hashCode() = value?.contentHashCode() ?: 0

    override fun toString() = StringBuilder().apply(this::print).toString()
}
