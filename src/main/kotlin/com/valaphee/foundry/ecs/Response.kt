/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.foundry.ecs

import com.valaphee.foundry.ecs.system.Facet

/**
 * @author Kevin Ludwig
 */
sealed class Response

/**
 * @author Kevin Ludwig
 */
object Consumed : Response()

/**
 * @author Kevin Ludwig
 */
object Pass : Response()

/**
 * @author Kevin Ludwig
 */
data class MessageResponse<C : Context>(
    val message: Message<C>
) : Response()

/**
 * @author Kevin Ludwig
 */
data class StateResponse<C : Context, P : Message<C>, F : Facet<C, P>>(
    val facet: F
) : Response()
