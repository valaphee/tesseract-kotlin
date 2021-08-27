/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain

import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.position

/**
 * @author Kevin Ludwig
 */
class CartesianDeltaMergePacketizer : BaseFacet<WorldContext, CartesianDeltaMerge>(CartesianDeltaMerge::class) {
    override suspend fun receive(message: CartesianDeltaMerge): Response {
        val (chunkX, chunkZ) = message.entity.position
        message.changes.forEach { (position, value) ->
            val (x, y, z) = decodePosition(position)
            BlockUpdatePacket(Int3((chunkX shr 4) + x, y, (chunkZ shr 4) + z), value, BlockUpdatePacket.Flag.All, 0)
        }

        return Pass
    }
}
