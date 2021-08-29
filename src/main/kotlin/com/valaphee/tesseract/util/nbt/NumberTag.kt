/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.util.nbt

/**
 * @author Kevin Ludwig
 */
interface NumberTag : Tag {
    override val isNumber get() = true

    override fun asNumberTag() = this

    fun toByte(): Byte

    fun toShort(): Short

    fun toInt(): Int

    fun toLong(): Long

    fun toFloat(): Float

    fun toDouble(): Double
}
