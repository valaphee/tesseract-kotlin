/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.nbt

import java.io.DataInput
import java.io.DataOutput

/**
 * @author Kevin Ludwig
 */
internal class DoubleTagImpl(
    override var name: String,
    private var value: Double = 0.0
) : NumberTag {
    override val type get() = TagType.Double

    override fun toByte() = value.toInt().toByte()

    override fun toShort() = value.toInt().toShort()

    override fun toInt() = value.toInt()

    override fun toLong() = value.toLong()

    override fun toFloat() = value.toFloat()

    override fun toDouble() = value

    override fun read(input: DataInput, depth: Int, remainingBytes: IntArray) {
        remainingBytes[0] -= Double.SIZE_BYTES
        if (remainingBytes[0] < 0) throw NbtException("Reached maximum allowed size")

        value = input.readDouble()
    }

    override fun write(output: DataOutput) {
        output.writeDouble(value)
    }

    override fun print(string: StringBuilder) {
        string.append(value).append('D')
    }

    override fun toString() = StringBuilder().apply(this::print).toString()
}
