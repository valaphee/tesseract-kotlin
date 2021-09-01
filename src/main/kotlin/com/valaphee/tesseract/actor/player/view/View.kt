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
import com.valaphee.tesseract.world.chunk.encodePosition
import com.valaphee.tesseract.world.whenTypeIs
import it.unimi.dsi.fastutil.longs.LongArrayList
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import java.lang.Integer.min
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

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

                val chunksInView = LongArrayList()
                repeat(distance) { distance ->
                    val chunksInDistance = LongArrayList()
                    val minAngle = acos(1.0f - 1.0f / distance)
                    var angle = 0.0f
                    while (angle <= 360.0f) {
                        val chunk = encodePosition((chunkX + distance * cos(angle)).toInt(),  (chunkZ + distance * sin(angle)).toInt())
                        chunksInDistance.add(chunk)
                        chunksInView.add(chunk)
                        angle += minAngle
                    }

                    val chunksToAcquire = chunksInDistance.filterNot(_acquiredChunks::contains)
                    _acquiredChunks.addAll(chunksToAcquire)
                    message.context.world.sendMessage(ChunkAcquire(message.context, it, chunksToAcquire.toLongArray(), ViewChunk(message.context, it, position, distance + 1)))
                }
                val chunksToRelease = _acquiredChunks.filterNot(chunksInView::contains)
                if (chunksToRelease.isNotEmpty()) {
                    _acquiredChunks.removeAll(chunksToRelease)
                    message.context.world.sendMessage(ChunkRelease(message.context, it, chunksToRelease.toLongArray()))
                }
            }
        }

        return Pass
    }
}
