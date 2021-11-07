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

package com.valaphee.tesseract.util

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
@JsonSerialize(using = Version.Serializer::class)
@JsonDeserialize(using = Version.Deserializer::class)
data class Version(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val build: Int = 0
) : Comparable<Version> {
    override fun compareTo(other: Version): Int {
        var result = Integer.compareUnsigned(major, other.major)
        if (result == 0) {
            result = Integer.compareUnsigned(minor, other.minor)
            if (result == 0) result = Integer.compareUnsigned(patch, other.patch)
        }
        return result
    }

    override fun toString() = "$major.$minor.$patch${if (build != 0) ".$build" else ""}"

    internal class Serializer(
        `class`: Class<Version>? = null
    ) : StdSerializer<Version>(`class`) {
        override fun serialize(value: Version, generator: JsonGenerator, provider: SerializerProvider) {
            generator.writeArray(intArrayOf(value.major, value.minor, value.patch), 0, 3)
        }
    }

    internal class Deserializer(
        `class`: Class<*>? = null
    ) : StdDeserializer<Version>(`class`) {
        override fun deserialize(parser: JsonParser, context: DeserializationContext): Version {
            val array = parser.codec.readValue(parser, IntArray::class.java)
            return Version(array[0], array[1], array[2], if (array.size >= 4) array[3] else 0)
        }
    }
}
