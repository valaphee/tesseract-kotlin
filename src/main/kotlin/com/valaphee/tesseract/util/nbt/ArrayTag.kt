/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.util.nbt

/**
 * @author Kevin Ludwig
 */
interface ArrayTag : Tag {
    override val isArray get() = true

    override fun asArrayTag() = this

    fun toByteArray(): ByteArray

    fun toIntArray(): IntArray

    fun toLongArray(): LongArray

    fun valueToString(): String
}
