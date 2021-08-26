/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.foundry.ecs.mutator

import com.valaphee.foundry.ecs.Context
import com.valaphee.foundry.ecs.accessor.FacetAccessor
import com.valaphee.foundry.ecs.system.FacetWithContext
import kotlin.reflect.KClass

/**
 * @author Kevin Ludwig
 */
interface FacetMutator<C : Context> : FacetAccessor<C> {
    fun addFacet(facet: FacetWithContext<C>)

    fun removeFacet(facet: FacetWithContext<C>)
}

fun <C : Context> facetMutator(facets: Set<FacetWithContext<C>>): FacetMutator<C> = DefaultFacetMutator(facets)

/**
 * @author Kevin Ludwig
 */
class DefaultFacetMutator<C : Context>(
    facets: Set<FacetWithContext<C>>
) : FacetMutator<C> {
    private val _facets = facets.toMutableSet()
    override val facets get() = _facets.toSet().asSequence()
    override val hasFacets get() = _facets.isNotEmpty()

    @Suppress("UNCHECKED_CAST")
    override fun <T : FacetWithContext<C>> findFacetOrNull(`class`: KClass<T>) = _facets.firstOrNull { `class`.isInstance(it) } as? T

    override fun addFacet(facet: FacetWithContext<C>) {
        _facets.add(facet)
    }

    override fun removeFacet(facet: FacetWithContext<C>) {
        _facets.remove(facet)
    }
}
