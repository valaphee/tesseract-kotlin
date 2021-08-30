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
internal class LongArrayTagImpl(
    override var name: String,
    private var value: LongArray? = null
) : ArrayTag {
    override val type get() = TagType.LongArray

    override fun toByteArray() = ByteBuffer.allocate(value!!.size * Long.SIZE_BYTES).apply { asLongBuffer().put(value) }.array()

    override fun toIntArray() = ByteBuffer.allocate(value!!.size * Long.SIZE_BYTES).apply { asLongBuffer().put(value) }.asIntBuffer().array()

    override fun toLongArray() = value!!.clone()

    override fun valueToString() = String(toByteArray(), StandardCharsets.UTF_8)

    override fun read(input: DataInput, depth: Int, remainingBytes: IntArray) {
        remainingBytes[0] -= Int.SIZE_BYTES
        if (remainingBytes[0] < 0) throw NbtException("Reached maximum allowed size")

        val valueLength = input.readInt()
        remainingBytes[0] -= valueLength * Long.SIZE_BYTES
        if (remainingBytes[0] < 0) throw NbtException("Reached maximum allowed size")

        value = LongArray(valueLength) { input.readLong() }
    }

    override fun write(output: DataOutput) {
        value?.let {
            output.writeInt(it.size)
            it.forEach { output.writeLong(it) }
        } ?: output.writeInt(0)
    }

    override fun print(string: StringBuilder) {
        string.append("[L;")
        value?.let {
            it.forEach { string.append(it).append(',') }
            if (it.isEmpty()) string.append(']') else string.setCharAt(string.length - 1, ']')
        } ?: string.append(']')
    }

    override fun toString() = StringBuilder().apply(this::print).toString()
}
