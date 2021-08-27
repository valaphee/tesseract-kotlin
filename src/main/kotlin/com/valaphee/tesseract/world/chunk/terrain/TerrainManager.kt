/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain

import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.tesseract.world.WorldContext

/**
 * @author Kevin Ludwig
 */
class TerrainManager : BaseFacet<WorldContext, CartesianDeltaMerge>(CartesianDeltaMerge::class) {
    override suspend fun receive(message: CartesianDeltaMerge): Response {
        val blocks = message.entity.terrain.blockStorage
        message.changes.forEach { (position, value) ->
            val (x, y, z) = decodePosition(position)
            blocks[x, y, z] = value
        }

        return Pass
    }
}
