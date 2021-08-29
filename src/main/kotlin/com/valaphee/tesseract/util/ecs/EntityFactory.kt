/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.util.ecs

import com.google.inject.Injector
import com.valaphee.foundry.ecs.Attribute
import com.valaphee.foundry.ecs.Context
import com.valaphee.foundry.ecs.entity.DefaultEntity
import com.valaphee.foundry.ecs.entity.EntityType
import com.valaphee.foundry.ecs.system.Behavior
import com.valaphee.foundry.ecs.system.FacetWithContext
import kotlin.random.Random

/**
 * @author Kevin Ludwig
 */
class EntityFactory<C : Context>(
    private val injector: Injector
) {
    private val behaviorClassesByType = mutableMapOf<EntityType, MutableSet<Class<Behavior<C>>>>()
    private val facetClassesByType = mutableMapOf<EntityType, MutableSet<Class<FacetWithContext<C>>>>()

    fun <T : EntityType> register(type: T, block: Builder<T, C>.() -> Unit) = Builder<T, C>(type).apply(block).also {
        behaviorClassesByType.getOrPut(type) { mutableSetOf() }.addAll(it.behaviorClasses)
        facetClassesByType.getOrPut(type) { mutableSetOf() }.addAll(it.facetClasses)
    }

    operator fun <T : EntityType> invoke(type: T, attributes: Set<Attribute>, id: Long = Random.nextLong()) = invoke(type, attributes, behaviorClassesByType[type] ?: emptySet(), facetClassesByType[type] ?: emptySet(), id)

    internal operator fun <T : EntityType> invoke(type: T, attributes: Set<Attribute>, behaviorClasses: Set<Class<*>>, facetClasses: Set<Class<*>>, id: Long = Random.nextLong()) = DefaultEntity(type, attributes, behaviorClasses.map {
        @Suppress("UNCHECKED_CAST")
        injector.getInstance(it) as Behavior<C>
    }.toSet(), facetClasses.map {
        @Suppress("UNCHECKED_CAST")
        injector.getInstance(it) as FacetWithContext<C>
    }.toSet(), id)

    class Builder<T : EntityType, C : Context>(
        private val type: T
    ) {
        internal var behaviorClasses = setOf<Class<Behavior<C>>>()
        internal var facetClasses = setOf<Class<FacetWithContext<C>>>()

        fun behaviors(vararg behaviors: Class<*>) = also {
            @Suppress("UNCHECKED_CAST")
            this.behaviorClasses = behaviors.toSet() as Set<Class<Behavior<C>>>
        }

        fun facets(vararg facets: Class<*>) = also {
            @Suppress("UNCHECKED_CAST")
            this.facetClasses = facets.toSet() as Set<Class<FacetWithContext<C>>>
        }
    }
}
