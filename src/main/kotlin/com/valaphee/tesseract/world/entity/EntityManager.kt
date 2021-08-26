/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.entity

import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.tesseract.world.WorldContext

/**
 * @author Kevin Ludwig
 */
class EntityManager : BaseFacet<WorldContext, EntityManagerMessage>(EntityManagerMessage::class) {
    override suspend fun receive(message: EntityManagerMessage): Response {
        when (message) {
            is EntityAdd -> message.entities.forEach { message.context.engine.addEntity(it) }
            is EntityRemove -> message.entityIds.forEach { message.context.engine.findEntityOrNull(it)?.let(message.context.engine::removeEntity) }
        }

        return Pass
    }
}
