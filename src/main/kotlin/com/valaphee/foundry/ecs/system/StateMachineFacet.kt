/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.foundry.ecs.system

import com.valaphee.foundry.ecs.Context
import com.valaphee.foundry.ecs.Message
import com.valaphee.foundry.ecs.MessageResponse
import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.StateResponse
import com.valaphee.foundry.ecs.message.StateChanged
import kotlin.reflect.KClass

fun <C : Context, A : Message<C>, M : A> Facet<C, M>.toStateMachine(commonAncestor: KClass<A>) = StateMachineFacet(commonAncestor, this)

/**
 * @author Kevin Ludwig
 */
class StateMachineFacet<C : Context, P : Message<C>>(
    override val messageType: KClass<P>,
    initialState: Facet<C, out P>
) : Facet<C, P> {
    override val id get() = currentState.id
    override val mandatoryAttributes get() = currentState.mandatoryAttributes

    var currentState = initialState
        private set

    override suspend fun tryReceive(message: Message<C>): Response {
        val result = currentState.tryReceive(message)
        return if (result is StateResponse<*, *, *>) {
            val oldState = currentState
            @Suppress("UNCHECKED_CAST")
            currentState = result.facet as Facet<C, P>
            MessageResponse(StateChanged(message.context, message.source, oldState, currentState))
        } else Pass
    }

    override suspend fun receive(message: P) = tryReceive(message)
}
