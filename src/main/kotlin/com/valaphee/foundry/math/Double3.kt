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
object Double3Serializer : JsonSerializer<Double3>() {
    override fun serialize(value: Double3, generator: JsonGenerator, provider: SerializerProvider) {
        generator.writeArray(doubleArrayOf(value.x, value.y, value.z), 0, 3)
    }
}

/**
 * @author Kevin Ludwig
 */
object Double3Deserializer : JsonDeserializer<Double3>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Double3 {
        val array = parser.readValueAs(DoubleArray::class.java)
        return Double3(array[0], array[1], array[2])
    }
}
