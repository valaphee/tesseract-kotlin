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
 *
 */

package com.valaphee.tesseract.pack

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer

/**
 * @author Kevin Ludwig
 */
@JsonSerialize(using = SemanticVersion.Serializer::class)
@JsonDeserialize(using = SemanticVersion.Deserializer::class)
data class SemanticVersion(
    val major: Int,
    val minor: Int,
    val patch: Int
) : Comparable<SemanticVersion> {
    constructor(version: IntArray) : this(version[0], version[1], version[2])

    override fun compareTo(other: SemanticVersion): Int {
        var result = Integer.compareUnsigned(major, other.major)
        if (result == 0) {
            result = Integer.compareUnsigned(minor, other.minor)
            if (result == 0) result = Integer.compareUnsigned(patch, other.patch)
        }
        return result
    }

    override fun toString() = "$major.$minor.$patch"

    internal class Serializer(
        `class`: Class<SemanticVersion>? = null
    ) : StdSerializer<SemanticVersion>(`class`) {
        override fun serialize(value: SemanticVersion, generator: JsonGenerator, provider: SerializerProvider) {
            generator.writeArray(intArrayOf(value.major, value.minor, value.patch), 0, 3)
        }
    }

    internal class Deserializer(
        `class`: Class<*>? = null
    ) : StdDeserializer<SemanticVersion>(`class`) {
        override fun deserialize(parser: JsonParser, context: DeserializationContext) = SemanticVersion(parser.codec.readValue(parser, IntArray::class.java))
    }
}
