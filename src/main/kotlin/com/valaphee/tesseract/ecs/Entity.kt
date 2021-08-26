/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.ecs

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

/**
 * @author Kevin Ludwig
 */
object EntitySerializer : JsonSerializer<Entity<*, *>>() {
    override fun serialize(value: Entity<*, *>, generator: JsonGenerator, provider: SerializerProvider) {
        generator.writeStartObject()
        generator.writeNumberField("id", value.id)
        generator.writeObjectField("attributes", value.attributes.associateBy { it::class.java.name }.toMap())
        generator.writeObjectField("behaviors", value.behaviors.map { it::class.java.name })
        generator.writeObjectField("facets", value.facets.map { it::class.java.name })
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
