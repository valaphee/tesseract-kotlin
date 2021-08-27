/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain

import com.valaphee.foundry.ecs.system.BaseBehavior
import com.valaphee.tesseract.world.AnyEntityOfWorld
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.ChunkType
import com.valaphee.tesseract.world.whenTypeIs

/**
 * @author Kevin Ludwig
 */
class CartesianDeltaMerger : BaseBehavior<WorldContext>() {
    override suspend fun update(entity: AnyEntityOfWorld, context: WorldContext): Boolean {
        entity.whenTypeIs<ChunkType> {
            val terrain = it.terrain
            if (terrain.lastCartesianDeltaMerge++ > cartesianDeltaMergeDelay) {
                terrain.lastCartesianDeltaMerge = 0
                terrain.cartesianDelta.merge(context, it)
            }
        }

        return true
    }

    companion object {
        private const val cartesianDeltaMergeDelay = 25
    }
}
