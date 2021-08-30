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

package com.valaphee.tesseract.world.chunk

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.valaphee.foundry.ecs.BaseAttribute
import com.valaphee.foundry.ecs.entity.BaseEntityType
import com.valaphee.foundry.math.Int2
import com.valaphee.tesseract.world.EntityOfWorld

/**
 * @author Kevin Ludwig
 */
object ChunkType : BaseEntityType("chunk")

typealias Chunk = EntityOfWorld<ChunkType>

/**
 * @author Kevin Ludwig
 */
class Location(
    @JsonSerialize(using = PositionSerializer::class)
    @JsonDeserialize(using = PositionDeserializer::class)
    val position: Int2
) : BaseAttribute()

val Chunk.position get() = findAttribute(Location::class).position

fun encodePosition(x: Int, z: Int) = ((x and 0x3FFFFF).toLong() shl 42) or ((z and 0x3FFFFF).toLong() shl 20)

fun decodePosition(position: Long) = Int2((position shr 42).toInt(), ((position shl 22) shr 42).toInt())

/**
 * @author Kevin Ludwig
 */
object PositionSerializer : JsonSerializer<Int2>() {
    override fun serialize(value: Int2, generator: JsonGenerator, provider: SerializerProvider) {
        generator.writeNumber(encodePosition(value.x, value.y))
    }
}

/**
 * @author Kevin Ludwig
 */
object PositionDeserializer : JsonDeserializer<Int2>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext) = decodePosition(parser.longValue)
}
