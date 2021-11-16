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

package com.valaphee.tesseract.pack.block

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.valaphee.tesseract.data.Data
import com.valaphee.tesseract.data.DataModule
import com.valaphee.tesseract.data.DataType
import com.valaphee.tesseract.pack.ComponentsDeserializer
import com.valaphee.tesseract.pack.ComponentsSerializer
import com.valaphee.tesseract.util.nbt.Nbt
import com.valaphee.tesseract.util.nbt.compoundTag
import com.valaphee.tesseract.util.nbt.listTag
import com.valaphee.tesseract.util.nbt.ofBool
import com.valaphee.tesseract.util.nbt.toTag

/**
 * @author Kevin Ludwig
 */
@DataType("minecraft:block")
class Block(
    @get:JsonProperty("description") val description: Description,
    @get:JsonProperty("events") val events: Map<String, Map<String, Any>>? = null,
    @get:JsonSerialize(using = ComponentsSerializer::class) @get:JsonDeserialize(using = ComponentsDeserializer::class) @get:JsonProperty("components") val components: List<BlockComponent>? = null,
    @get:JsonProperty("permutations") val permutations: List<Permutation>? = null
) : Data, Nbt {
    class Description(
        @get:JsonProperty("identifier") val key: String,
        @get:JsonProperty("properties") val properties: LinkedHashMap<String, LinkedHashSet<*>>? = null
    )

    class Permutation(
        @get:JsonProperty("condition") val condition: String,
        @get:JsonSerialize(using = ComponentsSerializer::class) @get:JsonDeserialize(using = ComponentsDeserializer::class) @get:JsonProperty("components") val components: List<BlockComponent>
    ) : Nbt {
        override fun toTag() = compoundTag().apply {
            setString("condition", condition)
            this["components"] = compoundTag().apply { components.forEach { this[DataModule.typeByClass(it::class)] = it.toTag() } }
        }
    }

    override fun toTag() = compoundTag().apply {
        setInt("molangVersion", 0)
        description.properties?.let {
            if (it.isNotEmpty()) this["properties"] = listTag().apply {
                description.properties.forEach {
                    put(compoundTag().apply {
                        setString("name", it.key)
                        this["enum"] = listTag().apply {
                            when (it.value.first()) {
                                is Boolean -> it.value.forEach { put(ofBool(it as Boolean)) }
                                is Int -> it.value.forEach { putInt(it as Int) }
                                is String -> it.value.forEach { putString(it as String) }
                                else -> TODO()
                            }
                        }
                    })
                }
            }
        }
        components?.let { this["components"] = compoundTag().apply { it.forEach { this[DataModule.typeByClass(it::class)] = it.toTag() } } }
        permutations?.let { if (permutations.isNotEmpty()) this["permutations"] = it.toTag() }
    }
}
