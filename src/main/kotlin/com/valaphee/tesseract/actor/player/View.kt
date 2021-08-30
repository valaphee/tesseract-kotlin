/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.player

import com.google.inject.Inject
import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.foundry.math.Int2
import com.valaphee.tesseract.Config
import com.valaphee.tesseract.actor.location.Location
import com.valaphee.tesseract.actor.location.LocationManagerMessage
import com.valaphee.tesseract.actor.location.position
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

                chunksInDistance.sort { position1, position2 ->
                    val (x1, z1) = decodePosition(position1)
                    val (x2, z2) = decodePosition(position2)
                    distance(chunkX, chunkZ, x1, z1).compareTo(distance(chunkX, chunkZ, x2, z2))
                }

                val chunksToRelease = _acquiredChunks.filterNot(chunksInDistance::contains)
                val chunksToAcquire = chunksInDistance.filterNot(_acquiredChunks::contains)
                if (chunksToRelease.isNotEmpty()) {
                    _acquiredChunks.removeAll(chunksToRelease)
                    message.context.world.receiveMessage(ChunkRelease(message.context, message.source, chunksToRelease.toLongArray()))
                }
                if (chunksToAcquire.isNotEmpty()) {
                    _acquiredChunks.addAll(chunksToAcquire)
                    message.context.world.receiveMessage(ChunkAcquire(message.context, message.source, chunksToAcquire.toLongArray()))
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
