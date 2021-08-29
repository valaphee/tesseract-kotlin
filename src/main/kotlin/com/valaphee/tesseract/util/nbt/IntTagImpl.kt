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
internal class IntTagImpl(
    override var name: String,
    private var value: Int = 0
) : NumberTag {
    override val type get() = TagType.Int

    override fun toByte() = value.toByte()

    override fun toShort() = value.toShort()

    override fun toInt() = value

    override fun toLong() = value.toLong()

    override fun toFloat() = value.toFloat()

    override fun toDouble() = value.toDouble()

    override fun read(input: DataInput, depth: Int, remainingBytes: IntArray) {
        remainingBytes[0] -= Int.SIZE_BYTES
        if (remainingBytes[0] < 0) throw NbtException("Reached maximum allowed size")

        value = input.readInt()
    }

    override fun write(output: DataOutput) {
        output.writeInt(value)
    }

    override fun print(string: StringBuilder) {
        string.append(value)
    }

    override fun toString() = StringBuilder().apply(this::print).toString()
}
