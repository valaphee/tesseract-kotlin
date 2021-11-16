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

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.common.collect.HashBiMap
import com.google.inject.AbstractModule
import com.google.inject.Inject
import com.google.inject.Key
import com.google.inject.TypeLiteral
import com.google.inject.binder.AnnotatedBindingBuilder
import com.google.inject.util.Types
import com.valaphee.tesseract.data.block.Block
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.util.getCompoundTag
import com.valaphee.tesseract.util.getListTag
import com.valaphee.tesseract.util.getString
import com.valaphee.tesseract.util.nbt.NbtInputStream
import com.valaphee.tesseract.util.nbt.TagType
import io.github.classgraph.ClassGraph
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.Unpooled
import org.apache.logging.log4j.LogManager
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmName

/**
 * @author Kevin Ludwig
 */
class DataModule(
    private val path: File = File("data"),
    private vararg val classLoader: ClassLoader = arrayOf(DataModule::class.java.classLoader)
) : AbstractModule() {
    @Inject lateinit var objectMapper: ObjectMapper

    override fun configure() {
        if (!::objectMapper.isInitialized) objectMapper = jacksonObjectMapper().apply {
            registerModule(AfterburnerModule())
            propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
            typeFactory = TypeFactory.defaultInstance().withClassLoader(classLoader.first())
            setSerializationInclusion(JsonInclude.Include.NON_DEFAULT)
            enable(SerializationFeature.INDENT_OUTPUT)
            enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            enable(JsonParser.Feature.ALLOW_COMMENTS)
        }.also { bind(ObjectMapper::class.java).toInstance(it) }

        classLoader.forEach { classLoader ->
            ClassGraph().overrideClassLoaders(classLoader).enableClassInfo().enableAnnotationInfo().scan().use {
                val dataType = DataType::class.jvmName
                classByType.putAll(it.getClassesWithAnnotation(dataType).associate { it.getAnnotationInfo(dataType).parameterValues.getValue("value") as String to classLoader.loadClass(it.name).kotlin })
            }
        }

        classLoader.first().getResourceAsStream("/runtime_block_states.dat")?.let {
            var buffer: PacketBuffer? = null
            try {
                buffer = PacketBuffer(Unpooled.wrappedBuffer(it.readBytes()))
                val blocks = mutableMapOf<String, MutableList<Map<String, Any>>>()
                NbtInputStream(ByteBufInputStream(buffer)).use { it.readTag()!!.asCompoundTag()!! }.getListTag("blocks").toList().map { it.asCompoundTag()!! }.forEach {
                    blocks.getOrPut(it.getString("name")) { mutableListOf() }.add(it.getCompoundTag("states").toMap().mapValues {
                        when (it.value.type) {
                            TagType.Byte -> it.value.asNumberTag()!!.toByte() != 0.toByte()
                            TagType.Int -> it.value.asNumberTag()!!.toInt()
                            TagType.String -> it.value.asArrayTag()!!.valueToString()
                            else -> TODO(it.value.type::class.jvmName)
                        }
                    })
                }
                bind(object : Key<Map<String, @JvmSuppressWildcards Block>>() {}).toInstance(blocks.mapValues { Block(it.key, it.value) })
                log.info("Bound tesseract:block, with {} entries", blocks.size)
            } finally {
                buffer?.release()
            }
        }

        ClassGraph().overrideClassLoaders(*classLoader).acceptPaths("data").scan().use {
            val (keyed, other) = (it.allResources.map { it.url } + path.walk().filter { it.isFile }.map { it.toURI().toURL() })
                .map {
                    when (val extension = it.file.substring(it.file.lastIndexOf('.') + 1)) {
                        "json" -> {
                            @Suppress("UNCHECKED_CAST")
                            objectMapper.readValue<Data>(it)
                        }
                        else -> TODO(extension)
                    }
                }
                .partition { it is KeyedData }
            keyed.filterIsInstance<KeyedData>()
                .groupBy { typeByClass(it::class) }
                .forEach { (_, value) ->
                    @Suppress("UNCHECKED_CAST")
                    (bind(TypeLiteral.get(Types.mapOf(String::class.java, value.first()::class.java))) as AnnotatedBindingBuilder<Any>).toInstance(value.associateBy { it.key })
                    log.info("Bound {}, with {} entries", it, value.size)
                }
            other.forEach {
                @Suppress("UNCHECKED_CAST")
                (bind(it::class.java) as AnnotatedBindingBuilder<Any>).toInstance(it)
                log.info("Bound {}", it::class.findAnnotation<DataType>()!!.value)
            }
        }
    }

    companion object {
        private val log = LogManager.getLogger(DataModule::class.java)
        internal val classByType = HashBiMap.create<String, KClass<*>>()

        fun classByType(type: String) = classByType[type] ?: throw UnknownDataTypeException(type)

        fun typeByClass(`class`: KClass<*>) = classByType.inverse()[`class`] ?: throw UnknownDataTypeException(`class`.jvmName)
    }
}
