/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.util.nbt

import java.io.DataInput
import java.io.DataOutput

/**
 * @author Kevin Ludwig
 */
internal class FloatTagImpl(
    override var name: String,
    private var value: Float = 0.0f
) : NumberTag {
    override val type get() = TagType.Float

    override val isNumber get() = true

    override fun toByte() = value.toInt().toByte()

    override fun toShort() = value.toInt().toShort()

    override fun toInt() = value.toInt()

    override fun toLong() = value.toLong()

    override fun toFloat() = value

    override fun toDouble() = value.toDouble()

    override fun read(input: DataInput, depth: Int, remainingBytes: IntArray) {
        remainingBytes[0] -= Float.SIZE_BYTES
        if (remainingBytes[0] < 0) throw NbtException("Reached maximum allowed size")

        value = input.readFloat()
    }

    override fun write(output: DataOutput) {
        output.writeFloat(value)
    }

    override fun print(string: StringBuilder) {
        string.append(value).append('f')
    }

    override fun toString() = StringBuilder().apply(this::print).toString()
}
