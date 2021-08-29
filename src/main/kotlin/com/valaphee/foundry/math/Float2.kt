/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.foundry.math

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

/**
 * @author Kevin Ludwig
 */
object Float2Serializer : JsonSerializer<Float2>() {
    override fun serialize(value: Float2, generator: JsonGenerator, provider: SerializerProvider) {
        generator.writeArray(floatArrayOf(value.x, value.y), 0, 2)
    }
}

/**
 * @author Kevin Ludwig
 */
object Float2Deserializer : JsonDeserializer<Float2>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Float2 {
        val array = parser.readValueAs(FloatArray::class.java)
        return Float2(array[0], array[1])
    }
}
