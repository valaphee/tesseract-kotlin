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
