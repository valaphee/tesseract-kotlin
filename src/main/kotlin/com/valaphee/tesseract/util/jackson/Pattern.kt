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
import java.util.regex.Pattern

/**
 * @author Kevin Ludwig
 */
object PatternSerializer : JsonSerializer<Pattern>() {
    override fun serialize(value: Pattern, generator: JsonGenerator, provider: SerializerProvider) {
        generator.writeString(value.pattern())
    }
}

/**
 * @author Kevin Ludwig
 */
object PatternDeserializer : JsonDeserializer<Pattern>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Pattern = Pattern.compile(parser.codec.readValue(parser, String::class.java))
}
