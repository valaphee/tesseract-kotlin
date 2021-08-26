/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.foundry.ecs.system

import com.valaphee.foundry.ecs.Attribute
import com.valaphee.foundry.ecs.Context
import com.valaphee.foundry.ecs.Message
import kotlin.reflect.KClass

/**
 * @author Kevin Ludwig
 */
interface StateFacet<C : Context, M : Message<C>> : Facet<C, M> {
    suspend fun onEnter(message: M) {}

    suspend fun onExit(message: M) {}
}

/**
 * @author Kevin Ludwig
 */
abstract class BaseStateFacet<C : Context, P : Message<C>>(
    messageType: KClass<P>,
    vararg mandatoryAttributes: KClass<out Attribute>
) : BaseFacet<C, P>(messageType, *mandatoryAttributes), StateFacet<C, P>
