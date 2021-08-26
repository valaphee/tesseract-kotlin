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
internal class LongTagImpl(
    override var name: String,
    private var value: Long = 0
) : NumberTag {
    override val type get() = TagType.Long

    override fun toByte(): Byte = value.toByte()

    override fun toShort(): Short = value.toShort()

    override fun toInt(): Int = value.toInt()

    override fun toLong(): Long = value

    override fun toFloat(): Float = value.toFloat()

    override fun toDouble(): Double = value.toDouble()

    override fun read(input: DataInput, depth: Int, remainingBytes: IntArray) {
        remainingBytes[0] -= Long.SIZE_BYTES
        if (remainingBytes[0] < 0) throw NbtException("Reached maximum allowed size")

        value = input.readLong()
    }

    override fun write(output: DataOutput) {
        output.writeLong(value)
    }

    override fun print(string: StringBuilder) {
        string.append(value).append('L')
    }

    override fun toString() = StringBuilder().apply(this::print).toString()
}
