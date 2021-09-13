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

package com.valaphee.tesseract.data.entity

import com.google.inject.Inject
import com.google.inject.Injector
import com.valaphee.foundry.ecs.Attribute
import com.valaphee.foundry.ecs.Context
import com.valaphee.foundry.ecs.entity.DefaultEntity
import com.valaphee.foundry.ecs.entity.EntityType
import com.valaphee.foundry.ecs.system.Behavior
import com.valaphee.foundry.ecs.system.Facet
import com.valaphee.foundry.ecs.system.FacetWithContext
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/**
 * @author Kevin Ludwig
 */
class EntityFactory<C : Context> @Inject constructor(
    private val injector: Injector,
    data: Map<String, @JvmSuppressWildcards EntityTypeData>
) {
    private val behaviorClassesByType = mutableMapOf<String, List<KClass<*>>>()
    private val facetClassesByType = mutableMapOf<String, List<KClass<*>>>()

    init {
        data.forEach { (key, data) ->
            val (behaviors, others) = data.components.keys.partition { it.isSubclassOf(Behavior::class) }
            behaviorClassesByType[key] = behaviors
            facetClassesByType[key] = others.filter { it.isSubclassOf(Facet::class) }
        }
    }

    operator fun <T : EntityType> invoke(type: T, attributes: Set<Attribute>, id: Long = Random.nextLong()) = DefaultEntity(type, attributes, behaviorClassesByType[type.key]?.map {
        @Suppress("UNCHECKED_CAST")
        injector.getInstance(it.java) as Behavior<C>
    }?.toSet() ?: emptySet(), facetClassesByType[type.key]?.map {
        @Suppress("UNCHECKED_CAST")
        injector.getInstance(it.java) as FacetWithContext<C>
    }?.toSet() ?: emptySet(), id)
}
