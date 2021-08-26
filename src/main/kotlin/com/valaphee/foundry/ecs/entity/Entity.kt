/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
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
import kotlin.random.Random

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
    private val type: T
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
        return DefaultEntity(type, attributes, behaviors, facets)
    }
}

fun <T : EntityType, C : Context> entity(type: T, block: EntityBuilder<T, C>.() -> Unit) = EntityBuilder<T, C>(type).apply(block).build()

/**
 * @author Kevin Ludwig
 */
abstract class BaseEntity<T : EntityType, C : Context>(
    override val type: T,
    attributes: Set<Attribute> = setOf(),
    behaviors: Set<Behavior<C>> = setOf(),
    facets: Set<FacetWithContext<C>> = setOf(),
    override val id: Long = Random.nextLong(),
) : MutableEntity<T, C>, AttributeMutator by attributeMutator(attributes), BehaviorMutator<C> by behaviorMutator(behaviors), FacetMutator<C> by facetMutator(facets) {
    override fun asMutableEntity() = this

    /*companion object {
        internal val nextId = AtomicLong()
    }*/

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
    id: Long = Random.nextLong(),
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
