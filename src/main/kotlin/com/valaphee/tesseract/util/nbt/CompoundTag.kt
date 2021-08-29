/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.util.nbt

/**
 * @author Kevin Ludwig
 */
interface CompoundTag : Tag {
    override val isCompound get() = true

    override fun asCompoundTag() = this

    fun toMap(): Map<String, Tag>

    fun has(name: String): Boolean

    operator fun get(name: String): Tag?

    operator fun set(name: String, tag: Tag)

    fun setByte(name: String, value: Byte)

    fun setShort(name: String, value: Short)

    fun setInt(name: String, value: Int)

    fun setLong(name: String, value: Long)

    fun setFloat(name: String, value: Float)

    fun setDouble(name: String, value: Double)

    fun setByteArray(name: String, value: ByteArray)

    fun setString(name: String, value: String)

    fun setIntArray(name: String, value: IntArray)

    fun setLongArray(name: String, value: LongArray)

    fun remove(name: String): Tag?

    val size: Int

    val isEmpty: Boolean
}
