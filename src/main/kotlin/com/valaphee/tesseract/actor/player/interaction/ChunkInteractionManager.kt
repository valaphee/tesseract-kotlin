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

package com.valaphee.tesseract.actor.player.interaction

import com.valaphee.foundry.ecs.Consumed
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.tesseract.data.Component
import com.valaphee.tesseract.data.block.BlockState
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.position
import com.valaphee.tesseract.world.chunk.terrain.blockStorage
import com.valaphee.tesseract.world.chunk.terrain.blockUpdates

/**
 * @author Kevin Ludwig
 */
@Component("tesseract:chunk_interaction_manager")
class ChunkInteractionManager :  BaseFacet<WorldContext, ChunkInteractionManagerMessage>(ChunkInteractionManagerMessage::class){
    override suspend fun receive(message: ChunkInteractionManagerMessage): Response {
        val chunk = message.chunks.first()
        when (message) {
            is BlockBreak -> {
                val (x, y, z) = message.position
                chunk.blockUpdates[x, y, z, -1] = airId
            }
            is BlockUse -> {
                val (x, y, z) = message.position
                val blockState = BlockState.byId(chunk.blockStorage[x, y, z])
                if (blockState.id != airId) {
                    if (blockState.block?.onUse(message.context, message.source, chunk.blockUpdates, x, y, z, message.direction, message.clickPosition) != true) message.stackInHand?.let {
                        if (it.item.item?.onUseBlock(message.context, message.source, chunk.position, chunk.blockUpdates, x, y, z, message.direction, message.clickPosition) != true && it.blockRuntimeId != 0) {
                            val (xOffset, yOffset, zOffset) = message.direction.axis
                            chunk.blockUpdates[x + xOffset, y + yOffset, z + zOffset] = it.blockRuntimeId
                        }
                    }
                }
            }
        }

        return Consumed
    }

    companion object {
        val airId = BlockState.byKeyWithStates("minecraft:air").id
    }
}
