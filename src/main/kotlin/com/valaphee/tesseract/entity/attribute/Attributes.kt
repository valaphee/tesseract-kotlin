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

package com.valaphee.tesseract.entity.attribute

import com.valaphee.tesseract.net.PacketBuffer

/**
 * @author Kevin Ludwig
 */
class Attributes {
    private val attributes = mutableMapOf<String, AttributeValue>()

    operator fun get(field: AttributeField) = attributes[field.key]

    fun getValue(field: AttributeField) = attributes[field.key]?.value ?: field.defaultValue

    fun add(field: AttributeField) = field.attributeValue().also { attributes[field.key] = it }

    operator fun set(field: AttributeField, value: Float) = attributes.getOrPut(field.key, field::attributeValue).apply { this.value = value }

    val modified get() = attributes.values.any { it.modified }

    fun readFromBuffer(buffer: PacketBuffer, withDefault: Boolean) {
        repeat(buffer.readVarUInt()) {
            if (withDefault) {
                val minimum = buffer.readFloatLE()
                val maximum = buffer.readFloatLE()
                val value = buffer.readFloatLE()
                val defaultValue = buffer.readFloatLE()
                val key = buffer.readString()
                attributes[key] = AttributeValue(key, minimum, maximum, defaultValue, value)
            } else {
                val key = buffer.readString()
                val minimum = buffer.readFloatLE()
                val value = buffer.readFloatLE()
                val maximum = buffer.readFloatLE()
                attributes[key] = AttributeValue(key, minimum, maximum, value)
            }
        }
    }

    fun writeToBuffer(buffer: PacketBuffer, withDefault: Boolean) {
        val modifiedAttributes = attributes.filter { it.value.modified }
        buffer.writeVarUInt(modifiedAttributes.count())
        modifiedAttributes.forEach { (field, value) ->
            if (withDefault) {
                buffer.writeFloatLE(value.minimum)
                buffer.writeFloatLE(value.maximum)
                buffer.writeFloatLE(value.value)
                buffer.writeFloatLE(value.defaultValue)
                buffer.writeString(field)
            } else {
                buffer.writeString(field)
                buffer.writeFloatLE(value.minimum)
                buffer.writeFloatLE(value.value)
                buffer.writeFloatLE(value.maximum)
            }
            value.modified = false
        }
    }

    override fun toString() = StringBuilder("Attributes(").apply {
        attributes.forEach { (field, value) -> append(field).append('=').append(value.value).append(',') }
        if (attributes.isEmpty()) append(')') else setCharAt(length - 1, ')')
    }.toString()
}
