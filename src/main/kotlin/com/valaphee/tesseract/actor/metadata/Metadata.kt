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

package com.valaphee.tesseract.actor.metadata

import com.valaphee.foundry.ecs.BaseAttribute
import com.valaphee.tesseract.actor.AnyActorOfWorld
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.util.Int2ObjectOpenHashBiMap
import com.valaphee.tesseract.util.ecs.Runtime
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap

/**
 * @author Kevin Ludwig
 */
@Runtime
class Metadata : BaseAttribute() {
    private val values = Int2ObjectOpenHashMap<MetadataValue<Any?>>()

    fun has(fieldId: Int) = values.containsKey(fieldId)

    fun has(fieldId: Int, type: MetadataType<*>) = values[fieldId]?.type == type

    fun has(field: MetadataField<*>) = values[field.id]?.type == field.type

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(fieldId: Int) = values[fieldId]?.value as T?

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(field: MetadataField<T>) = values[field.id]?.value as T?

    @Suppress("UNCHECKED_CAST")
    operator fun <T> set(fieldId: Int, type: MetadataType<T>, value: T) {
        values[fieldId]?.let { it.value = value } ?: run { values[fieldId] = MetadataValue(type, value) as MetadataValue<Any?> }
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T> set(field: MetadataField<T>, value: T) {
        values[field.id]?.let { it.value = value } ?: run { values[field.id] = MetadataValue(field.type, value) as MetadataValue<Any?> }
    }

    val modified get() = values.values.any { it.modified }

    fun readFromBuffer(buffer: PacketBuffer) {
        repeat(buffer.readVarUInt()) {
            val fieldId = buffer.readVarUInt()
            val type = types[buffer.readVarUInt()]
            @Suppress("UNCHECKED_CAST")
            values[fieldId] = MetadataValue(type, type.read(buffer)) as MetadataValue<Any?>?
        }
    }

    fun writeToBuffer(buffer: PacketBuffer) {
        val modifiedValues = values.filterValues { it.modified }.onEach { it.value.modified = false }
        buffer.writeVarUInt(modifiedValues.size)
        modifiedValues.forEach { (fieldId, value) ->
            buffer.writeVarUInt(fieldId)
            val type = value.type
            buffer.writeVarUInt(types.getKey(type))
            type.write(buffer, value.value)
        }
    }

    override fun toString() = StringBuilder("Metadata(").apply {
        val modifiedValues = values.filter { it.value.modified }
        modifiedValues.forEach { (fieldId, value) -> append(fieldId).append('=').append(value.value).append(',') }
        if (modifiedValues.isEmpty()) append(')') else setCharAt(length - 1, ')')
    }.toString()

    companion object {
        private val types = Int2ObjectOpenHashBiMap<MetadataType<*>>().apply {
            this[0] = MetadataType.Byte
            this[1] = MetadataType.Short
            this[2] = MetadataType.Int
            this[3] = MetadataType.Float
            this[4] = MetadataType.String
            this[5] = MetadataType.Tag
            this[6] = MetadataType.Int3
            this[7] = MetadataType.Long
            this[7] = MetadataType.Flags
            this[7] = MetadataType.Flags2
            this[8] = MetadataType.Float3
        }
    }
}

val AnyActorOfWorld.metadata get() = findAttribute(Metadata::class)
