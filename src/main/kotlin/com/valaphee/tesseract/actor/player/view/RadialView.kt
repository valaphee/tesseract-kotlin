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

package com.valaphee.tesseract.actor.player.view

import com.google.inject.Inject
import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.math.Int2
import com.valaphee.tesseract.actor.location.LocationManagerMessage
import com.valaphee.tesseract.actor.location.location
import com.valaphee.tesseract.actor.player.PlayerType
import com.valaphee.tesseract.data.Component
import com.valaphee.tesseract.data.Config
import com.valaphee.tesseract.world.chunk.ChunkAcquire
import com.valaphee.tesseract.world.chunk.ChunkRelease
import com.valaphee.tesseract.world.chunk.decodePosition
import com.valaphee.tesseract.world.chunk.encodePosition
import com.valaphee.tesseract.world.filter
import it.unimi.dsi.fastutil.longs.LongArrayList

/**
 * @author Kevin Ludwig
 */
@Component("tesseract:player.radial_view")
class RadialView @Inject constructor(
    config: Config
) : View(config) {
    private lateinit var lastChunkPosition: Int2

    override suspend fun receive(message: LocationManagerMessage): Response {
        message.entity?.filter<PlayerType> { player ->
            val position = player.location.position.toInt3()
            val chunkX = position.x shr 4
            val chunkZ = position.z shr 4
            val chunkPosition = Int2(chunkX, chunkZ)
            if (!this::lastChunkPosition.isInitialized || lastChunkPosition != chunkPosition) {
                lastChunkPosition = chunkPosition

                val distance2 = distance * distance
                val chunksInDistance = LongArrayList()
                for (chunkXr in -distance..distance) for (chunkZr in -distance..distance) if ((chunkXr * chunkXr) + (chunkZr * chunkZr) <= distance2) chunksInDistance.add(encodePosition(chunkX + chunkXr, chunkZ + chunkZr))

                chunksInDistance.sort { position1, position2 ->
                    val (x1, z1) = decodePosition(position1)
                    val (x2, z2) = decodePosition(position2)
                    distance(chunkX, chunkZ, x1, z1).compareTo(distance(chunkX, chunkZ, x2, z2))
                }

                val chunksToRelease = _acquiredChunks.filterNot(chunksInDistance::contains)
                val chunksToAcquire = chunksInDistance.filterNot(_acquiredChunks::contains)
                if (chunksToRelease.isNotEmpty()) {
                    _acquiredChunks.removeAll(chunksToRelease)
                    message.context.world.receiveMessage(ChunkRelease(message.context, player, chunksToRelease.toLongArray()))
                }
                if (chunksToAcquire.isNotEmpty()) {
                    _acquiredChunks.addAll(chunksToAcquire)
                    message.context.world.receiveMessage(ChunkAcquire(message.context, player, chunksToAcquire.toLongArray(), ViewChunk(message.context, player, position, distance * 16)))
                }
            }
        }

        return Pass
    }

    companion object {
        fun distance(x1: Int, z1: Int, x2: Int, z2: Int): Int {
            val dx = x1 - x2
            val dz = z1 - z2
            return dx * dx + dz * dz
        }
    }
}
