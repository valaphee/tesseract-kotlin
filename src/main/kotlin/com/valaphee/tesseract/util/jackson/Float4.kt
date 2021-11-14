/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.util.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.valaphee.foundry.math.Float4

/**
 * @author Kevin Ludwig
 */
object Float4Serializer : JsonSerializer<Float4>() {
    override fun serialize(value: Float4, generator: JsonGenerator, serializer: SerializerProvider) {
        generator.writeArray(floatArrayOf(value.x, value.y, value.z, value.w), 0, 4)
    }
}

/**
 * @author Kevin Ludwig
 */
object Float4Deserializer : JsonDeserializer<Float4>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Float4 {
        val array = parser.readValueAs(FloatArray::class.java)
        return Float4(array[0], array[1], array[2], array[3])
    }
}
