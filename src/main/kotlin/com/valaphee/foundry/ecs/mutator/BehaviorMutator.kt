/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.foundry.ecs.mutator

import com.valaphee.foundry.ecs.Context
import com.valaphee.foundry.ecs.accessor.BehaviorAccessor
import com.valaphee.foundry.ecs.system.Behavior
import kotlinx.collections.immutable.toPersistentSet
import kotlin.reflect.KClass

/**
 * @author Kevin Ludwig
 */
interface BehaviorMutator<C : Context> : BehaviorAccessor<C> {
    fun addBehavior(behavior: Behavior<C>)

    fun removeBehavior(behavior: Behavior<C>)
}

fun <C : Context> behaviorMutator(behaviors: Set<Behavior<C>>): BehaviorMutator<C> = DefaultBehaviorMutator(behaviors)

/**
 * @author Kevin Ludwig
 */
class DefaultBehaviorMutator<C : Context>(
    behaviors: Set<Behavior<C>>
) : BehaviorMutator<C> {
    private var _behaviors = behaviors.toPersistentSet()
    override val behaviors get() = _behaviors.asSequence()
    override val hasBehaviors get() = _behaviors.isNotEmpty()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Behavior<C>> findBehaviorOrNull(`class`: KClass<T>) = _behaviors.firstOrNull { `class`.isInstance(it) } as? T

    override fun addBehavior(behavior: Behavior<C>) {
        _behaviors = _behaviors.add(behavior)
    }

    override fun removeBehavior(behavior: Behavior<C>) {
        _behaviors = _behaviors.remove(behavior)
    }
}
