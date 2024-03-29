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

package com.valaphee.foundry.ecs.entity

import com.valaphee.foundry.ecs.Attribute
import com.valaphee.foundry.ecs.Consumed
import com.valaphee.foundry.ecs.Context
import com.valaphee.foundry.ecs.Message
import com.valaphee.foundry.ecs.MessageResponse
import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.accessor.AttributeAccessor
import com.valaphee.foundry.ecs.accessor.BehaviorAccessor
import com.valaphee.foundry.ecs.accessor.FacetAccessor
import com.valaphee.foundry.ecs.mutator.AttributeMutator
import com.valaphee.foundry.ecs.mutator.BehaviorMutator
import com.valaphee.foundry.ecs.mutator.FacetMutator
import com.valaphee.foundry.ecs.mutator.attributeMutator
import com.valaphee.foundry.ecs.mutator.behaviorMutator
import com.valaphee.foundry.ecs.mutator.facetMutator
import com.valaphee.foundry.ecs.system.Behavior
import com.valaphee.foundry.ecs.system.Facet
import com.valaphee.foundry.ecs.system.FacetWithContext

/**
 * @author Kevin Ludwig
 */
interface Entity<T : EntityType, C : Context> : AttributeAccessor, BehaviorAccessor<C>, FacetAccessor<C> {
    val id: Long
    val needsUpdate: Boolean
    val type: T

    fun sendMessage(message: Message<C>): Boolean

    suspend fun receiveMessage(message: Message<C>): Response

    suspend fun update(context: C): Boolean

    fun asMutableEntity(): MutableEntity<T, C>
}

/**
 * @author Kevin Ludwig
 */
class EntityBuilder<T : EntityType, C : Context>(
    private val type: T,
    private val id: Long
) {
    private var attributes = setOf<Attribute>()
    private var behaviors = setOf<Behavior<C>>()
    private var facets = setOf<FacetWithContext<C>>()

    fun attributes(vararg attributes: Attribute) = also {
        this.attributes = attributes.toSet()
    }

    fun behaviors(vararg behaviors: Behavior<C>) = also {
        this.behaviors = behaviors.toSet()
    }

    fun facets(vararg facets: FacetWithContext<C>) = also {
        this.facets = facets.toSet()
    }

    fun build(): Entity<T, C> {
        require(behaviors.flatMap { it.mandatoryAttributes }.plus(facets.flatMap { it.mandatoryAttributes }).toSet().subtract(this.attributes.map { it::class }).isEmpty())
        return DefaultEntity(type, attributes, behaviors, facets, id)
    }
}

fun <T : EntityType, C : Context> entity(type: T, id: Long, block: EntityBuilder<T, C>.() -> Unit) = EntityBuilder<T, C>(type, id).apply(block).build()

/**
 * @author Kevin Ludwig
 */
abstract class BaseEntity<T : EntityType, C : Context>(
    override val type: T,
    attributes: Set<Attribute> = setOf(),
    behaviors: Set<Behavior<C>> = setOf(),
    facets: Set<FacetWithContext<C>> = setOf(),
    override val id: Long,
) : MutableEntity<T, C>, AttributeMutator by attributeMutator(attributes), BehaviorMutator<C> by behaviorMutator(behaviors), FacetMutator<C> by facetMutator(facets) {
    override fun asMutableEntity() = this

    /*companion object {
        internal val nextId = AtomicLong()
    }*/

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseEntity<*, *>

        if (id != other.id) return false

        return true
    }

    override fun hashCode() = id.hashCode()

    override fun toString() = "Entity(type=$type, id=$id)"
}

/**
 * @author Kevin Ludwig
 */
open class DefaultEntity<T : EntityType, C : Context>(
    type: T,
    attributes: Set<Attribute> = setOf(),
    behaviors: Set<Behavior<C>> = setOf(),
    facets: Set<FacetWithContext<C>> = setOf(),
    id: Long,
) : BaseEntity<T, C>(type, attributes + type, behaviors, facets, id) {
    private val messages = mutableListOf<Message<C>>()
    override val needsUpdate get() = hasBehaviors || messages.isNotEmpty()

    override fun sendMessage(message: Message<C>) = messages.add(message)

    @Suppress("UNCHECKED_CAST")
    override suspend fun receiveMessage(message: Message<C>) = if (hasFacets) {
        val facetsIterator = facets.iterator() as Iterator<Facet<C, Message<C>>>
        var response: Response = Pass
        var lastMessage = message
        while (facetsIterator.hasNext() && response != Consumed) {
            response = facetsIterator.next().tryReceive(lastMessage)
            if (response is MessageResponse<*>) lastMessage = response.message as Message<C>
        }
        response
    } else Pass

    override suspend fun update(context: C): Boolean {
        val messages = messages.toList()
        this.messages.clear()
        messages.forEach { receiveMessage(it) }
        return behaviors.fold(false) { result, behavior -> result or behavior.update(this, context) }
    }
}
