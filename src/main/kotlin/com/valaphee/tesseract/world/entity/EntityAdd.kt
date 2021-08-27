/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.entity

import com.valaphee.tesseract.world.AnyEntityOfWorld
import com.valaphee.tesseract.world.World
import com.valaphee.tesseract.world.WorldContext

/**
 * @author Kevin Ludwig
 */
class EntityAdd(
    override val context: WorldContext,
    override val source: AnyEntityOfWorld?,
    val entities: Array<AnyEntityOfWorld>
) : EntityManagerMessage {
    override val entity: AnyEntityOfWorld? get() = null
}

fun World.addEntities(context: WorldContext, vararg entity: AnyEntityOfWorld) {
    @Suppress("UNCHECKED_CAST")
    sendMessage(EntityAdd(context, null, entity as Array<AnyEntityOfWorld>))
}
