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
internal class EndTagImpl(
    override var name: String
) : Tag {
    override val type get() = TagType.End

    override fun read(input: DataInput, depth: Int, remainingBytes: IntArray) {}

    override fun write(output: DataOutput) {}

    override fun print(string: StringBuilder) {}
}
