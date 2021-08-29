/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
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
