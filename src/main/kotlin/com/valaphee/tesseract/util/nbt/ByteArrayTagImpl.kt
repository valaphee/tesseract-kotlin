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
internal class ByteArrayTagImpl(
    override var name: String,
    private var value: ByteArray? = null
) : ArrayTag {
    override val type get() = TagType.ByteArray

    override fun toByteArray() = value!!.clone()

    override fun toIntArray() = ByteBuffer.wrap(value).asIntBuffer().array()

    override fun toLongArray() = ByteBuffer.wrap(value).asLongBuffer().array()

    override fun valueToString() = String(value!!, StandardCharsets.UTF_8)

    override fun read(input: DataInput, depth: Int, remainingBytes: IntArray) {
        remainingBytes[0] -= Int.SIZE_BYTES
        if (remainingBytes[0] < 0) throw NbtException("Reached maximum allowed size")

        val valueLength = input.readInt()
        remainingBytes[0] -= valueLength
        if (remainingBytes[0] < 0) throw NbtException("Reached maximum allowed size")

        value = ByteArray(valueLength).apply { input.readFully(this) }
    }

    override fun write(output: DataOutput) {
        value?.let {
            output.writeInt(it.size)
            output.write(it)
        } ?: output.writeInt(0)
    }

    override fun print(string: StringBuilder) {
        string.append("[b;")
        value?.let {
            it.forEach { string.append(it).append(',') }
            if (it.isEmpty()) string.append(']') else string.setCharAt(string.length - 1, ']')
        } ?: string.append(']')
    }

    override fun toString() = StringBuilder().apply(this::print).toString()
}
