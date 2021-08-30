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
