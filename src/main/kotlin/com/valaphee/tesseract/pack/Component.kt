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

package com.valaphee.tesseract.pack

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.valaphee.tesseract.data.DataModule
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaType

/**
 * @author Kevin Ludwig
 */
interface Component

/**
 * @author Kevin Ludwig
 */
abstract class WrapperComponent<T>(
    val value: T
) : Component

/**
 * @author Kevin Ludwig
 */
class ComponentsSerializer : JsonSerializer<List<Component>>() {
    override fun serialize(value: List<Component>, generator: JsonGenerator, serializer: SerializerProvider) {
        generator.writeStartObject()
        value.forEach { generator.writeObjectField(DataModule.typeByClass(it::class), if (it is WrapperComponent<*>) it.value else it) }
        generator.writeEndObject()
    }
}

/**
 * @author Kevin Ludwig
 */
class ComponentsDeserializer : JsonDeserializer<List<Component>>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): List<Component> {
        val node = parser.readValueAsTree<JsonNode>()
        val codec = parser.codec
        return if (node.isObject) mutableListOf<Component>().apply {
            node.fields().forEach {
                val `class` = DataModule.classByType(it.key)
                add((if (`class`.isSubclassOf(WrapperComponent::class)) `class`.primaryConstructor!!.call(it.value.traverse(codec).readValueAs<Any>(object : TypeReference<Any>() {
                    override fun getType() = `class`.primaryConstructor!!.parameters.first().type.javaType
                })) else it.value.traverse(codec).readValueAs(`class`.java)) as Component)
            }
        } else error("")
    }
}
