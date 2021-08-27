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
class EntityRemove(
    override val context: WorldContext,
    override val source: AnyEntityOfWorld?,
    val entityIds: LongArray,
) : EntityManagerMessage {
    override val entity: AnyEntityOfWorld? get() = null
}

fun World.removeEntities(context: WorldContext, source: AnyEntityOfWorld?, vararg entityId: Long) {
    sendMessage(EntityRemove(context, source, entityId))
}
