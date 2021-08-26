/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.nbt

/**
 * @author Kevin Ludwig
 */
interface ListTag : Tag {
    override val isList get() = true

    override fun asListTag() = this

    fun toArray(): Array<Tag>

    fun toList(): List<Tag>

    fun has(index: Int): Boolean

    operator fun get(index: Int): Tag

    fun put(tag: Tag)

    fun putByte(value: Byte)

    fun putShort(value: Short)

    fun putInt(value: Int)

    fun putLong(value: Long)

    fun putFloat(value: Float)

    fun putDouble(value: Double)

    fun putByteArray(value: ByteArray)

    fun putString(value: String)

    fun putIntArray(value: IntArray)

    fun putLongArray(value: LongArray)

    operator fun set(index: Int, tag: Tag)

    fun setByte(index: Int, value: Byte)

    fun setShort(index: Int, value: Short)

    fun setInt(index: Int, value: Int)

    fun setLong(index: Int, value: Long)

    fun setFloat(index: Int, value: Float)

    fun setDouble(index: Int, value: Double)

    fun setByteArray(index: Int, value: ByteArray)

    fun setString(index: Int, value: String)

    fun setIntArray(index: Int, value: IntArray)

    fun setLongArray(index: Int, value: LongArray)

    fun remove(index: Int): Tag

    val size: Int

    val isEmpty: Boolean
}
