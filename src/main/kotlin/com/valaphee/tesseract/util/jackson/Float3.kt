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
import com.valaphee.foundry.math.Float3

/**
 * @author Kevin Ludwig
 */
object Float3Serializer : JsonSerializer<Float3>() {
    override fun serialize(value: Float3, generator: JsonGenerator, provider: SerializerProvider) {
        generator.writeArray(floatArrayOf(value.x, value.y, value.z), 0, 3)
    }
}

/**
 * @author Kevin Ludwig
 */
object Float3Deserializer : JsonDeserializer<Float3>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Float3 {
        val array = parser.readValueAs(FloatArray::class.java)
        return Float3(array[0], array[1], array[2])
    }
}
