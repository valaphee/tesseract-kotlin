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

package com.valaphee.tesseract.world.chunk.terrain

import com.valaphee.foundry.ecs.system.BaseBehavior
import com.valaphee.foundry.math.Float3
import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.data.Component
import com.valaphee.tesseract.world.AnyEntityOfWorld
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.WorldEventPacket
import com.valaphee.tesseract.world.chunk.ChunkType
import com.valaphee.tesseract.world.chunk.Location
import com.valaphee.tesseract.world.chunk.broadcast
import com.valaphee.tesseract.world.chunk.position
import com.valaphee.tesseract.world.chunk.terrain.block.BlockState
import com.valaphee.tesseract.world.filter

/**
 * @author Kevin Ludwig
 */
@Component("tesseract:block_updater")
class BlockUpdater : BaseBehavior<WorldContext>(Location::class, Terrain::class) {
    override suspend fun update(entity: AnyEntityOfWorld, context: WorldContext): Boolean {
        entity.filter<ChunkType> {
            val (chunkX, chunkZ) = it.position
            val terrain = it.findAttribute(Terrain::class)
            val blockStorage = terrain.blockStorage
            val blockUpdates = terrain.blockUpdates
            val blockUpdateMemory = blockUpdates.memory
            blockUpdateMemory.filterValues { it < 0 }.forEach { (position, data) ->
                val (x, y, z) = decodePosition(position)
                when (data) {
                    -1 -> it.broadcast(WorldEventPacket(WorldEventPacket.Event.ParticleDestroyBlock, Float3(chunkX * 16.0f + x, y.toFloat(), chunkZ * 16.0f + z), blockStorage[x, y, z]))
                }
                blockUpdateMemory.remove(position)
            }

            val blockUpdateChanges = blockUpdates.changes
            if (blockUpdateChanges.isNotEmpty()) {
                it.broadcast(*blockUpdateChanges.map { (position, value) ->
                    val (x, y, z) = decodePosition(position)
                    blockStorage[x, y, z] = value
                    BlockUpdatePacket(Int3((chunkX * BlockStorage.XZSize) + x, y, (chunkZ * BlockStorage.XZSize) + z), value, BlockUpdatePacket.Flag.All, 0)
                }.toTypedArray())
                blockUpdateChanges.clear()
                terrain.modified = true
            }

            val cycle = context.cycle.toInt()
            val propagationBlockUpdates = it.blockUpdates
            blockUpdateMemory.filterValues { it > 0 }.forEach { (position, data) ->
                if (cycle and data == 0) {
                    val (x, y, z) = decodePosition(position)
                    val blockState = BlockState.byId(blockStorage[x, y, z])
                    blockState.block.onUpdate?.invoke(propagationBlockUpdates, x, y, z, blockState)
                    blockUpdateMemory.remove(position)
                }
            }
        }

        return true
    }
}
