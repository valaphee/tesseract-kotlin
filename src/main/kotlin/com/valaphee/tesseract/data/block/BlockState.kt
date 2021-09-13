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

package com.valaphee.tesseract.data.block

import com.valaphee.tesseract.util.nbt.CompoundTag
import com.valaphee.tesseract.util.nbt.TagType

/**
 * @author Kevin Ludwig
 */
class BlockState(
    val key: String,
    val propertiesNbt: CompoundTag,
    val version: Int
) {
    val properties = propertiesNbt.toMap().mapValues { (_, blockStatePropertyTag) ->
        when (blockStatePropertyTag.type) {
            TagType.Byte -> blockStatePropertyTag.asNumberTag()!!.toByte() != 0.toByte()
            TagType.Int -> blockStatePropertyTag.asNumberTag()!!.toInt()
            TagType.String -> blockStatePropertyTag.asArrayTag()!!.valueToString()
            else -> TODO()
        }
    }
    var id = 0
    var block: Block? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BlockState

        if (id != other.id) return false

        return true
    }

    override fun hashCode() = id

    override fun toString() = StringBuilder().apply {
        append(key)
        if (properties.isNotEmpty()) {
            append('[')
            properties.forEach { (name, value) -> append(name).append('=').append(value).append(',') }
            setCharAt(length - 1, ']')
        }
    }.toString()

    companion object {
        private val values: MutableList<BlockState> = ArrayList()
        private var finished = false

        fun register(value: BlockState) {
            check(!finished) { "Already finished" }
            values += value
        }

        fun finish() {
            check(!finished) { "Already finished" }

            finished = true
            values.sortWith(compareBy { it.key.split(":", limit = 2)[1].lowercase() })
            values.forEachIndexed { i, it -> it.id = i }
        }

        fun byId(id: Int) = checkNotNull(byIdOrNull(id))

        fun byIdOrNull(id: Int) = if (id < values.size) values[id] else null

        fun byKey(key: String) = values.filter { key == it.key }

        fun byKeyWithStates(keyWithProperties: String) = checkNotNull(byKeyWithStatesOrNull(keyWithProperties))

        fun byKeyWithStatesOrNull(keyWithProperties: String): BlockState? {
            val propertiesBegin = keyWithProperties.indexOf('[')
            val propertiesEnd = keyWithProperties.indexOf(']')
            return if (propertiesBegin == -1 && propertiesEnd == -1) {
                byKey(keyWithProperties).firstOrNull()
            } else if (propertiesEnd == keyWithProperties.length - 1) {
                val properties = mutableMapOf<String, Any>()
                keyWithProperties.substring(propertiesBegin + 1, propertiesEnd).split(',').forEach {
                    val property = it.split('=', limit = 2)
                    properties[property[0]] = when (val propertyValue = property[1]) {
                        "false" -> false
                        "true" -> true
                        else -> propertyValue.toIntOrNull() ?: propertyValue
                    }
                }
                byKey(keyWithProperties.substring(0, propertiesBegin)).find { properties == it.properties }
            } else null
        }

        val all get() = values
    }
}
