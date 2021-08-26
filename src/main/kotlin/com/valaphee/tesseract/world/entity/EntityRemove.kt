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
) : EntityManagerMessage

fun World.removeEntities(context: WorldContext, vararg entityId: Long) {
    sendMessage(EntityRemove(context, null, entityId))
}
