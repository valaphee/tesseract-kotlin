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
