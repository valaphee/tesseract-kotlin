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

package com.valaphee.tesseract.util.ecs

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.node.ObjectNode
import com.valaphee.foundry.ecs.Attribute
import com.valaphee.foundry.ecs.entity.Entity
import com.valaphee.foundry.ecs.entity.EntityType
import com.valaphee.tesseract.world.WorldContext
import kotlin.reflect.full.hasAnnotation

/**
 * @author Kevin Ludwig
 */
object EntitySerializer : JsonSerializer<Entity<*, *>>() {
    override fun serialize(value: Entity<*, *>, generator: JsonGenerator, provider: SerializerProvider) {
        generator.writeStartObject()
        generator.writeNumberField("id", value.id)
        generator.writeObjectField("attributes", value.attributes.filter { !it::class.hasAnnotation<Runtime>() }.associateBy { it::class.qualifiedName }.toMap())
        /*generator.writeObjectField("behaviors", value.behaviors.map { it::class.qualifiedName })
        generator.writeObjectField("facets", value.facets.map { it::class.qualifiedName })*/
        generator.writeEndObject()
    }
}

/**
 * @author Kevin Ludwig
 */
class EntityDeserializer : JsonDeserializer<Entity<*, *>>() {
    internal lateinit var entityFactory: EntityFactory<WorldContext>

    override fun deserialize(parser: JsonParser, context: DeserializationContext): Entity<*, *> {
        val codec = parser.codec
        val `object` = codec.readTree<ObjectNode>(parser)
        val attributes = `object`.get("attributes").fields().asSequence().map { codec.treeToValue(it.value, Class.forName(it.key)) as Attribute }.toSet()
        return entityFactory(attributes.filterIsInstance(EntityType::class.java).first(), attributes, `object`.get("id").asLong())
    }
}
