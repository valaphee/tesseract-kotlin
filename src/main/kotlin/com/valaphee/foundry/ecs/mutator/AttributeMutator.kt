/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
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
