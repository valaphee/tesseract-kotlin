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
import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.data.Component
import com.valaphee.tesseract.data.block.BlockState
import com.valaphee.tesseract.data.block.Blocks
import com.valaphee.tesseract.net.connection
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.position
import com.valaphee.tesseract.world.chunk.terrain.BlockUpdatePacket
import com.valaphee.tesseract.world.chunk.terrain.blockStorage
import com.valaphee.tesseract.world.chunk.terrain.blockUpdates

/**
 * @author Kevin Ludwig
 */
@Component("tesseract:player.chunk_interaction_manager")
class ChunkInteractionManager :  BaseFacet<WorldContext, ChunkInteractionManagerMessage>(ChunkInteractionManagerMessage::class){
    override suspend fun receive(message: ChunkInteractionManagerMessage): Response {
        val chunk = message.chunks.first()
        when (message) {
            is BlockBreak -> {
                val (x, y, z) = message.position
                chunk.blockUpdates[x, y, z, -1] = Blocks.airId
            }
            is BlockUse -> {
                val (x, y, z) = message.position
                val (xOffset, yOffset, zOffset) = message.direction.axis
                val blockStateId = chunk.blockStorage[x, y, z]
                val blockUpdates = chunk.blockUpdates
                val adjacentBlockStateId = blockUpdates[x + xOffset, y + yOffset, z + zOffset]
                if (!Blocks.isTransparent(blockStateId)) {
                    if (BlockState.byId(blockStateId).block?.onUse(message.context, message.entity, blockUpdates, x, y, z, message.direction, message.clickPosition) != true) message.stackInHand?.let {
                        if (Blocks.isTransparent(adjacentBlockStateId)) {
                            if (it.item.item?.onUseBlock(message.context, message.entity, chunk.position, blockUpdates, x, y, z, message.direction, message.clickPosition) != true && it.blockRuntimeId != 0) blockUpdates[x + xOffset, y + yOffset, z + zOffset] = it.blockRuntimeId
                        } else {
                            // Already a non-transparent block
                            val (chunkX, chunkZ) = chunk.position
                            message.entity.connection.write(BlockUpdatePacket(Int3(chunkX * 16 + x + xOffset, y + yOffset, chunkZ * 16 + z + zOffset), adjacentBlockStateId, BlockUpdatePacket.Flag.All, 0))
                        }
                    } // ?: No item in hand
                } else {
                    // Block building on is transparent
                    val (chunkX, chunkZ) = chunk.position
                    message.entity.connection.write(BlockUpdatePacket(Int3(chunkX * 16 + x + xOffset, y + yOffset, chunkZ * 16 + z + zOffset), adjacentBlockStateId, BlockUpdatePacket.Flag.All, 0))
                }
            }
        }

        return Consumed
    }
}
