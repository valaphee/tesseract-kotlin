/*
 * MIT License
 *
 * Copyright (c) 2021, Valaphee.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.valaphee.tesseract.util.nbt

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
