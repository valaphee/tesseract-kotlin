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
internal class ByteTagImpl(
    override var name: String,
    private var value: Byte = 0
) : NumberTag {
    override val type get() = TagType.Byte

    override fun toByte() = value

    override fun toShort() = value.toShort()

    override fun toInt() = value.toInt()

    override fun toLong() = value.toLong()

    override fun toFloat() = value.toFloat()

    override fun toDouble() = value.toDouble()

    override fun read(input: DataInput, depth: Int, remainingBytes: IntArray) {
        remainingBytes[0] -= Byte.SIZE_BYTES
        if (remainingBytes[0] < 0) throw NbtException("Reached maximum allowed size")

        value = input.readByte()
    }

    override fun write(output: DataOutput) {
        output.writeByte(value.toInt())
    }

    override fun print(string: StringBuilder) {
        string.append(value.toInt()).append('b')
    }

    override fun toString() = StringBuilder().apply(this::print).toString()
}