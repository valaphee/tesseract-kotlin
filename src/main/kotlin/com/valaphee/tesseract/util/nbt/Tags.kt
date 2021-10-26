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

import kotlin.collections.set

private val falseTag = ByteTagImpl("", 0.toByte())
private val trueTag = ByteTagImpl("", 1.toByte())

fun ofBool(value: Boolean): NumberTag = if (value) trueTag else falseTag

fun ofByte(value: Byte): NumberTag = ByteTagImpl("", value)

fun ofShort(value: Short): NumberTag = ShortTagImpl("", value)

fun ofInt(value: Int): NumberTag = IntTagImpl("", value)

fun ofLong(value: Long): NumberTag = LongTagImpl("", value)

fun ofFloat(value: Float): NumberTag = FloatTagImpl("", value)

fun ofDouble(value: Double): NumberTag = DoubleTagImpl("", value)

fun listTag(): ListTag = ListTagImpl("")

fun listTag(type: TagType): ListTag = ListTagImpl("", mutableListOf(), type)

fun ofList(value: MutableList<Tag>): ListTag = ListTagImpl("", value, value[0].type)

fun compoundTag(): CompoundTag = CompoundTagImpl("")

fun compoundTag(key: Array<String>, value: Array<Tag>): CompoundTag {
    require(key.size == value.size) { "Length of keys and values differ" }
    return CompoundTagImpl("", LinkedHashMap<String, Tag>().apply { repeat(key.size) { this[key[it]] = value[it] } })
}

fun ofMap(value: MutableMap<String, Tag>): CompoundTag = CompoundTagImpl("", value)

fun ofByteArray(value: ByteArray): ArrayTag = ByteArrayTagImpl("", value)

fun ofIntArray(value: IntArray): ArrayTag = IntArrayTagImpl("", value)

fun ofLongArray(value: LongArray): ArrayTag = LongArrayTagImpl("", value)

fun ofString(value: String): ArrayTag = StringTagImpl("", value)

fun Any?.toTag(): Tag = when (this) {
    is Boolean -> ofBool(this)
    is Byte -> ofByte(this)
    is Short -> ofShort(this)
    is Int -> ofInt(this)
    is Long -> ofLong(this)
    is Float -> ofFloat(this)
    is Double -> ofDouble(this)
    is List<*> -> ofList(map { it.toTag() }.toMutableList())
    is Map<*, *> -> ofMap(map { it.key as String to it.value.toTag() }.toMap().toMutableMap())
    is ByteArray -> ofByteArray(this)
    is IntArray -> ofIntArray(this)
    is LongArray -> ofLongArray(this)
    is String -> ofString(this)
    is CustomTag -> toTag()
    else -> TODO("$this")
}
