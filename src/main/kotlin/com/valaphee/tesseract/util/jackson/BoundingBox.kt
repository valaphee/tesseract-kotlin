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

package com.valaphee.tesseract.util.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.valaphee.foundry.math.collision.BoundingBox

/**
 * @author Kevin Ludwig
 */
object BoundingBoxSerializer : JsonSerializer<BoundingBox>() {
    override fun serialize(value: BoundingBox, generator: JsonGenerator, serializer: SerializerProvider) {
        val (xMin, yMin, zMin) = value.minimum
        val (xMax, yMax, zMax) = value.maximum
        generator.writeArray(floatArrayOf(xMin, yMin, zMin, xMax, yMax, zMax), 0, 6)
    }
}

/**
 * @author Kevin Ludwig
 */
object BoundingBoxDeserializer : JsonDeserializer<BoundingBox>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): BoundingBox {
        val array = parser.readValueAs(FloatArray::class.java)
        return when (array.size) {
            1 -> {
                val size = array[0]
                val halfSize = array[0] / 2
                BoundingBox(-halfSize, 0.0f, -halfSize, halfSize, size, halfSize)
            }
            2 -> {
                val xzHalfSize = array[0] / 2
                val ySize = array[1]
                BoundingBox(-xzHalfSize, 0.0f, -xzHalfSize, xzHalfSize, ySize, xzHalfSize)
            }
            3 -> {
                val xHalfSize = array[0] / 2
                val ySize = array[1]
                val zHalfSize = array[2] / 2
                BoundingBox(-xHalfSize, 0.0f, -zHalfSize, xHalfSize, ySize, zHalfSize)
            }
            else -> TODO()
        }
    }
}
