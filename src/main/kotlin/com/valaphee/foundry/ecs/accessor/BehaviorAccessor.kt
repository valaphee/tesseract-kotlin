/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.foundry.ecs.accessor

import com.valaphee.foundry.ecs.Context
import com.valaphee.foundry.ecs.mutator.DefaultBehaviorMutator
import com.valaphee.foundry.ecs.system.Behavior
import kotlin.reflect.KClass

/**
 * @author Kevin Ludwig
 */
interface BehaviorAccessor<C : Context> {
    val behaviors: Sequence<Behavior<C>>
    val hasBehaviors: Boolean

    fun <T : Behavior<C>> findBehavior(`class`: KClass<T>) = checkNotNull(findBehaviorOrNull(`class`))

    fun <T : Behavior<C>> findBehaviorOrNull(`class`: KClass<T>): T?
}

fun <C : Context> behaviorAccessor(behaviors: Set<Behavior<C>>): BehaviorAccessor<C> = DefaultBehaviorMutator(behaviors)
