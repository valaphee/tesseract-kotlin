/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.foundry.ecs.system

import com.valaphee.foundry.ecs.Attribute
import com.valaphee.foundry.ecs.Context
import com.valaphee.foundry.ecs.entity.Entity
import com.valaphee.foundry.ecs.entity.EntityType
import java.util.concurrent.atomic.AtomicLong
import kotlin.reflect.KClass

/**
 * @author Kevin Ludwig
 */
interface Behavior<C : Context> : System<C> {
    suspend fun update(entity: Entity<out EntityType, C>, context: C): Boolean

    infix fun and(other: Behavior<C>): Behavior<C> = CompositeAndBehavior(this, other)

    infix fun or(other: Behavior<C>): Behavior<C> = CompositeOrBehavior(this, other)
}

/**
 * @author Kevin Ludwig
 */
abstract class BaseBehavior<C : Context>(
    vararg mandatoryAttribute: KClass<out Attribute>,
    override val id: Long = nextId.getAndIncrement()
) : Behavior<C> {
    override val mandatoryAttributes = mandatoryAttribute.toSet()

    companion object {
        internal val nextId = AtomicLong()
    }
}

/**
 * @author Kevin Ludwig
 */
class CompositeOrBehavior<C : Context>(
    private val first: Behavior<C>,
    private val second: Behavior<C>
) : BaseBehavior<C>() {
    override suspend fun update(entity: Entity<out EntityType, C>, context: C) = first.update(entity, context) || second.update(entity, context)
}

/**
 * @author Kevin Ludwig
 */
class CompositeAndBehavior<C : Context>(
    private val first: Behavior<C>,
    private val second: Behavior<C>
) : BaseBehavior<C>() {
    override suspend fun update(entity: Entity<out EntityType, C>, context: C) = first.update(entity, context) && second.update(entity, context)
}
