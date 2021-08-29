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
interface Tag {
    val type: TagType
    var name: String

    val isNumber get() = false

    fun asNumberTag(): NumberTag? = null

    val isList get() = false

    fun asListTag(): ListTag? = null

    val isCompound get() = false

    fun asCompoundTag(): CompoundTag? = null

    val isArray get() = false

    fun asArrayTag(): ArrayTag? = null

    fun read(input: DataInput, depth: Int, remainingBytes: IntArray)

    fun write(output: DataOutput)

    fun print(string: StringBuilder)
}
