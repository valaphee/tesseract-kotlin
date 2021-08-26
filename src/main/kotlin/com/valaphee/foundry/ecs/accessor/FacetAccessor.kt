/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.foundry.ecs.accessor

import com.valaphee.foundry.ecs.Context
import com.valaphee.foundry.ecs.mutator.DefaultFacetMutator
import com.valaphee.foundry.ecs.system.FacetWithContext
import kotlin.reflect.KClass

/**
 * @author Kevin Ludwig
 */
interface FacetAccessor<C : Context> {
    val facets: Sequence<FacetWithContext<C>>
    val hasFacets: Boolean

    fun <T : FacetWithContext<C>> findFacet(`class`: KClass<T>) = checkNotNull(findFacetOrNull(`class`))

    fun <T : FacetWithContext<C>> findFacetOrNull(`class`: KClass<T>): T?
}

fun <C : Context> facetAccessor(facets: Set<FacetWithContext<C>>): FacetAccessor<C> = DefaultFacetMutator(facets)
