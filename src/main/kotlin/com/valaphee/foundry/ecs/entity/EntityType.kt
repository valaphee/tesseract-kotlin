/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.foundry.ecs.entity

import com.valaphee.foundry.ecs.Attribute
import com.valaphee.foundry.ecs.BaseAttribute
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap

/**
 * @author Kevin Ludwig
 */
interface EntityType : Attribute {
    val key: String

    companion object {
        private val byId = Long2ObjectOpenHashMap<EntityType>()
        private val byKey = mutableMapOf<String, EntityType>()

        fun register(value: EntityType) {
            byId[value.id] = value
            byKey[value.key] = value
        }

        fun byId(id: Long): EntityType? = byId[id]

        fun byKey(key: String) = byKey[key]

        val all get() = byId.values
    }
}

/**
 * @author Kevin Ludwig
 */
abstract class BaseEntityType(
    override val key: String,
    override val id: Long = BaseAttribute.nextId.getAndIncrement(),
) : EntityType {
    init {
        @Suppress("LeakingThis")
        EntityType.register(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseEntityType

        if (key != other.key) return false // TODO

        return true
    }

    override fun hashCode() = key.hashCode() // TODO

    override fun toString() = key
}
