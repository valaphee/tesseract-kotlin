/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.foundry.ecs.system

import com.valaphee.foundry.ecs.Attribute
import com.valaphee.foundry.ecs.Context
import com.valaphee.foundry.ecs.Message
import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.entity.EntityType
import java.util.concurrent.atomic.AtomicLong
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/**
 * @author Kevin Ludwig
 */
interface Actor<T : EntityType, C : Context, P : Message<C>> : Facet<C, P>, Behavior<C>

/**
 * @author Kevin Ludwig
 */
abstract class BaseActor<C : Context, P : Message<C>>(
    messageType: KClass<P>,
    vararg mandatoryAttribute: KClass<out Attribute>,
    override val id: Long = nextId.getAndIncrement()
) : BaseFacet<C, P>(messageType), Behavior<C> {
    override val mandatoryAttributes = mandatoryAttribute.toSet()

    @Suppress("UNCHECKED_CAST")
    override suspend fun tryReceive(message: Message<C>) = if (message::class.isSubclassOf(messageType)) receive(message as P) else Pass

    companion object {
        internal val nextId = AtomicLong()
    }
}
