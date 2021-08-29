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

    override fun toString() = StringBuilder().apply(this::print).toString()
}
