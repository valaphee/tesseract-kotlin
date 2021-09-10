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
