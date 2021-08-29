/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.util.nbt

import java.io.Closeable
import java.io.DataOutput

/**
 * @author Kevin Ludwig
 */
class NbtOutputStream(
    private val output: DataOutput
) : Closeable {
    fun writeTag(tag: Tag?) = if (tag == null || tag.type == TagType.End) output.writeByte(TagType.End.ordinal) else {
        output.writeByte(tag.type.ordinal)
        output.writeUTF(tag.name)
        tag.write(output)
    }

    override fun close() {
        if (output is Closeable) (output as Closeable).close()
    }
}
