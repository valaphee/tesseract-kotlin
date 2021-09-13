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

package com.valaphee.tesseract.data

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DatabindContext
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase
import com.google.common.collect.HashBiMap
import io.github.classgraph.ClassGraph
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

/**
 * @author Kevin Ludwig
 */
object ComponentRegistry {
    private val byKey = HashBiMap.create<String, KClass<*>>()

    fun scan() {
        ClassGraph().enableClassInfo().enableAnnotationInfo().scan().use { byKey.putAll(it.getClassesWithAnnotation(Component::class.jvmName).associate { it.getAnnotationInfo(Component::class.jvmName).parameterValues.getValue("value") as String to Class.forName(it.name).kotlin }) }
    }

    fun byKeyOrNull(key: String) = byKey[key]

    fun byValueOrNull(value: KClass<*>) = byKey.inverse()[value]
}

/**
 * @author Kevin Ludwig
 */
object ComponentKeyResolver : TypeIdResolverBase() {
    override fun idFromValue(value: Any) = ComponentRegistry.byValueOrNull(value::class) ?: throw UnknownComponentException(value::class.jvmName)

    override fun idFromValueAndType(value: Any, suggestedType: Class<*>) = idFromValue(value)

    override fun typeFromId(context: DatabindContext, key: String) = ComponentRegistry.byKeyOrNull(key)?.let { context.constructType(it.java) } ?: throw UnknownComponentException(key)

    override fun getMechanism() = JsonTypeInfo.Id.NAME
}

/**
 * @author Kevin Ludwig
 */
object ComponentKeySerializer : JsonSerializer<KClass<*>>() {
    override fun serialize(value: KClass<*>, generator: JsonGenerator, provider: SerializerProvider) {
        ComponentRegistry.byValueOrNull(value)?.let { generator.writeFieldName(it) } ?: throw UnknownComponentException(value.toString())
    }
}

/**
 * @author Kevin Ludwig
 */
object ComponentKeyDeserializer : KeyDeserializer() {
    override fun deserializeKey(key: String, context: DeserializationContext) = ComponentRegistry.byKeyOrNull(key) ?: throw UnknownComponentException(key)
}
