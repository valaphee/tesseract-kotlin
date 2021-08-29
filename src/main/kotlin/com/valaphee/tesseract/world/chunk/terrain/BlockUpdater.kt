/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain

import com.valaphee.foundry.ecs.system.BaseBehavior
import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.world.AnyEntityOfWorld
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.broadcast
import com.valaphee.tesseract.world.chunk.ChunkType
import com.valaphee.tesseract.world.chunk.position
import com.valaphee.tesseract.world.chunk.terrain.block.BlockState
import com.valaphee.tesseract.world.whenTypeIs

/**
 * @author Kevin Ludwig
 */
class BlockUpdater : BaseBehavior<WorldContext>() {
    override suspend fun update(entity: AnyEntityOfWorld, context: WorldContext): Boolean {
        entity.whenTypeIs<ChunkType> {
            val (chunkX, chunkZ) = it.position
            val terrain = it.terrain
            val blockStorage = terrain.blockStorage
            val blockUpdates = terrain.blockUpdates
            val blockUpdateChanges = blockUpdates.changes
            if (blockUpdateChanges.isNotEmpty()) {
                it.broadcast(*blockUpdateChanges.map { (position, value) ->
                    val (x, y, z) = decodePosition(position)
                    blockStorage[x, y, z] = value
                    BlockUpdatePacket(Int3((chunkX * BlockStorage.XZSize) + x, y, (chunkZ * BlockStorage.XZSize) + z), value, BlockUpdatePacket.Flag.All, 0)
                }.toTypedArray())
                blockUpdateChanges.clear()
            }

            val blockUpdatePendingIterator = blockUpdates.pending.iterator()
            blockUpdatePendingIterator.forEach {
                when (it.value) {
                    1 -> {
                        val (x, y, z) = decodePosition(it.key)
                        val blockState = BlockState.byId(blockStorage[x, y, z])
                        blockState?.block?.onUpdate?.invoke(blockUpdates, x, y, z, blockState)
                        blockUpdatePendingIterator.remove()
                    }
                    else -> {
                        it.setValue(it.value - 1)
                    }
                }
            }
        }

        return true
    }
}