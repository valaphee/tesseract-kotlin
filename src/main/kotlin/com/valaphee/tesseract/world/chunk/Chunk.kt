/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
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
