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

package com.valaphee.tesseract.data.item

import com.valaphee.foundry.math.Float3
import com.valaphee.foundry.math.Int2
import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.world.chunk.actor.location.location
import com.valaphee.tesseract.actor.player.Player
import com.valaphee.tesseract.data.Index
import com.valaphee.tesseract.data.block.BlockState
import com.valaphee.tesseract.data.block.Blocks
import com.valaphee.tesseract.net.connection
import com.valaphee.tesseract.util.math.Direction
import com.valaphee.tesseract.util.math.toHorizontalDirection
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.terrain.BlockUpdatePacket
import com.valaphee.tesseract.world.chunk.terrain.PropagationBlockUpdateList

/**
 * @author Kevin Ludwig
 */
@Index
class Chest : Item {
    override val key get() = "minecraft:chest"

    private val facingDirections = BlockState.byKey("minecraft:chest").associate { it.properties["facing_direction"] as Int to it.id }

    override fun onUseBlock(context: WorldContext, player: Player, chunk: Int2, blockUpdates: PropagationBlockUpdateList, x: Int, y: Int, z: Int, direction: Direction, clickPosition: Float3): Boolean {
        val (xOffset, yOffset, zOffset) = direction.axis
        val adjacentBlockStateId = blockUpdates[x + xOffset, y + yOffset, z + zOffset]
        if (Blocks.isTransparent(adjacentBlockStateId)) blockUpdates[x + xOffset, y + yOffset, z + zOffset] = facingDirections[player.location.rotation.toHorizontalDirection().opposite().ordinal]!!
        else {
            val (chunkX, chunkZ) = chunk
            player.connection.write(BlockUpdatePacket(Int3(chunkX * 16 + x + xOffset, y + yOffset, chunkZ * 16 + z + zOffset), adjacentBlockStateId, BlockUpdatePacket.Flag.All, 0))
        }

        return true
    }
}
