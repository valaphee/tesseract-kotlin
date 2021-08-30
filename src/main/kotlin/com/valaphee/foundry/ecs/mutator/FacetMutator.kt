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
