/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.util.nbt

import java.io.Closeable
import java.io.DataInput

/**
 * @author Kevin Ludwig
 */
class NbtInputStream(
    private val input: DataInput
) : Closeable {
    fun readTag(maximumDepth: Int = 512, maximumBytes: Int = Int.MAX_VALUE): Tag? = readTag(maximumDepth, intArrayOf(maximumBytes))

    private fun readTag(depth: Int, remainingBytes: IntArray): Tag? {
        remainingBytes[0] -= 1
        if (remainingBytes[0] < 0) throw NbtException("Reached maximum allowed size")

        val type = TagType.values()[input.readUnsignedByte()]
        return type.tag?.invoke(input.readUTF())?.apply { if (type != TagType.End) read(input, depth, remainingBytes) }
    }

    override fun close() {
        if (input is Closeable) (input as Closeable).close()
    }
}
