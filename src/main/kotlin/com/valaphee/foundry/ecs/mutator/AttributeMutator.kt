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

import com.valaphee.foundry.ecs.Attribute
import com.valaphee.foundry.ecs.accessor.AttributeAccessor
import kotlinx.collections.immutable.toPersistentHashMap
import kotlin.reflect.KClass

/**
 * @author Kevin Ludwig
 */
interface AttributeMutator : AttributeAccessor {
    fun addAttribute(attribute: Attribute)

    fun removeAttribute(attribute: Attribute)
}

fun attributeMutator(attributes: Set<Attribute>): AttributeMutator = DefaultAttributeMutator(attributes)

/**
 * @author Kevin Ludwig
 */
class DefaultAttributeMutator(
    attributes: Set<Attribute>
) : AttributeMutator {
    private var _attributes = attributes.associateBy { it.id }.toPersistentHashMap()
    override val attributes get() = _attributes.values.asSequence()
    override val hasAttributes get() = _attributes.isNotEmpty()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Attribute> findAttributeOrNull(`class`: KClass<T>) = _attributes.values.firstOrNull { `class`.isInstance(it) } as? T

    override fun addAttribute(attribute: Attribute) {
        _attributes = _attributes.put(attribute.id, attribute)
    }

    override fun removeAttribute(attribute: Attribute) {
        _attributes = _attributes.remove(attribute.id)
    }
}
