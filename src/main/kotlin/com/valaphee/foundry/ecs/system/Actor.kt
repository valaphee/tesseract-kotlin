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
