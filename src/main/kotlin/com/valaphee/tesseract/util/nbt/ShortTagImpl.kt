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
internal class ShortTagImpl(
    override var name: String,
    private var value: Short = 0
) : NumberTag {
    override val type get() = TagType.Short

    override fun toByte() = value.toByte()

    override fun toShort() = value

    override fun toInt() = value.toInt()

    override fun toLong() = value.toLong()

    override fun toFloat() = value.toFloat()

    override fun toDouble() = value.toDouble()

    override fun read(input: DataInput, depth: Int, remainingBytes: IntArray) {
        remainingBytes[0] -= Short.SIZE_BYTES
        if (remainingBytes[0] < 0) throw NbtException("Reached maximum allowed size")

        value = input.readShort()
    }

    override fun write(output: DataOutput) {
        output.writeShort(value.toInt())
    }

    override fun print(string: StringBuilder) {
        string.append(value).append('s')
    }

    override fun toString() = StringBuilder().apply(this::print).toString()
}
