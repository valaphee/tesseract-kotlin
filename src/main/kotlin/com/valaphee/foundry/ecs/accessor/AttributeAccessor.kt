/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.foundry.ecs.accessor

import com.valaphee.foundry.ecs.Attribute
import com.valaphee.foundry.ecs.mutator.DefaultAttributeMutator
import kotlin.reflect.KClass

/**
 * @author Kevin Ludwig
 */
interface AttributeAccessor {
    val attributes: Sequence<Attribute>
    val hasAttributes: Boolean

    fun <T : Attribute> findAttribute(`class`: KClass<T>) = checkNotNull(findAttributeOrNull(`class`))

    fun <T : Attribute> findAttributeOrNull(`class`: KClass<T>): T?

    companion object {
        @JvmStatic
        fun create(attributes: Set<Attribute>): AttributeAccessor = DefaultAttributeMutator(attributes)
    }
}
