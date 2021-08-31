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
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.foundry.math.Int2
import com.valaphee.tesseract.Config
import com.valaphee.tesseract.actor.location.Location
import com.valaphee.tesseract.actor.location.LocationManagerMessage
import com.valaphee.tesseract.actor.location.position
import com.valaphee.tesseract.actor.player.PlayerType
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.ChunkAcquire
import com.valaphee.tesseract.world.chunk.ChunkRelease
import com.valaphee.tesseract.world.chunk.decodePosition
import com.valaphee.tesseract.world.chunk.encodePosition
import com.valaphee.tesseract.world.whenTypeIs
import it.unimi.dsi.fastutil.longs.LongArrayList
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import java.lang.Integer.min

/**
 * @author Kevin Ludwig
 */
class View @Inject constructor(
    private val config: Config
) : BaseFacet<WorldContext, LocationManagerMessage>(LocationManagerMessage::class, Location::class) {
    private lateinit var lastChunkPosition: Int2
    private val _acquiredChunks = LongOpenHashSet()
    val acquiredChunks: LongArray get() = _acquiredChunks.toLongArray()

    var distance = config.maximumViewDistance
        set(value) {
            field = min(value, config.maximumViewDistance)
        }

    override suspend fun receive(message: LocationManagerMessage): Response {
        message.entity?.whenTypeIs<PlayerType> {
            val position = it.position.toInt3()
            val chunkX = position.x shr 4
            val chunkZ = position.z shr 4
            val chunkPosition = Int2(chunkX, chunkZ)
            if (!this::lastChunkPosition.isInitialized || lastChunkPosition != chunkPosition) {
                lastChunkPosition = chunkPosition

                val distance2 = distance * distance
                val chunksInDistance = LongArrayList()
                for (chunkXr in -distance..distance) for (chunkZr in -distance..distance) if ((chunkXr * chunkXr) + (chunkZr * chunkZr) <= distance2) chunksInDistance.add(encodePosition(chunkX + chunkXr, chunkZ + chunkZr))

                chunksInDistance.sort { a, b -> chunkPosition.distance2(decodePosition(a)).compareTo(chunkPosition.distance2(decodePosition(b))) }

                val chunksToRelease = _acquiredChunks.filterNot(chunksInDistance::contains)
                val chunksToAcquire = chunksInDistance.filterNot(_acquiredChunks::contains)
                if (chunksToRelease.isNotEmpty()) {
                    _acquiredChunks.removeAll(chunksToRelease)
                    message.context.world.receiveMessage(ChunkRelease(message.context, it, chunksToRelease.toLongArray()))
                }
                if (chunksToAcquire.isNotEmpty()) {
                    _acquiredChunks.addAll(chunksToAcquire)
                    message.context.world.receiveMessage(ChunkAcquire(message.context, it, chunksToAcquire.toLongArray(), ViewChunk(message.context, it)))
                }
            }
        }

        return Pass
    }

    companion object {
        fun Int2.distance2(other: Int2): Int {
            val dx = x - other.x
            val dy = y - other.y
            return dx * dx + dy * dy
        }
    }
}
