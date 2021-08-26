/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.foundry.ecs.system

import com.valaphee.foundry.ecs.Attribute
import com.valaphee.foundry.ecs.Context
import com.valaphee.foundry.ecs.Message
import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import java.util.concurrent.atomic.AtomicLong
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/**
 * @author Kevin Ludwig
 */
interface Facet<C : Context, M : Message<C>> : System<C> {
    val messageType: KClass<M>

    suspend fun receive(message: M): Response

    suspend fun tryReceive(message: Message<C>): Response

    fun compose(other: Facet<C, M>, commonAncestor: KClass<M>): Facet<C, M> = CompositeFacet(setOf(this, other), commonAncestor)
}

typealias FacetWithContext<C> = Facet<out C, out Message<out C>>

/**
 * @author Kevin Ludwig
 */
abstract class BaseFacet<C : Context, P : Message<C>>(
    override val messageType: KClass<P>,
    vararg mandatoryAttribute: KClass<out Attribute>,
    override val id: Long = nextId.getAndIncrement()
) : Facet<C, P> {
    override val mandatoryAttributes = mandatoryAttribute.toSet()

    @Suppress("UNCHECKED_CAST")
    override suspend fun tryReceive(message: Message<C>) = if (message::class.isSubclassOf(messageType)) receive(message as P) else Pass

    companion object {
        internal val nextId = AtomicLong()
    }
}

/**
 * @author Kevin Ludwig
 */
@Suppress("DuplicatedCode")
class CompositeFacet<C : Context, P : Message<C>>(
    private val children: Set<Facet<C, P>>,
    override val messageType: KClass<P>,
    override val mandatoryAttributes: Set<KClass<out Attribute>> = children.flatMap { it.mandatoryAttributes }.toSet(),
    override val id: Long = BaseFacet.nextId.getAndIncrement(),
) : Facet<C, P> {
    override suspend fun receive(message: P) = tryReceive(message)

    override suspend fun tryReceive(message: Message<C>): Response {
        val childrenIterator = children.iterator()
        var response: Response = Pass
        while (childrenIterator.hasNext() && response is Pass) response = childrenIterator.next().tryReceive(message)
        return response
    }
}
