/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.player

import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.foundry.math.Int2
import com.valaphee.tesseract.actor.ActorType
import com.valaphee.tesseract.actor.location.LocationManagerMessage
import com.valaphee.tesseract.actor.location.position
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.ChunkAcquire
import com.valaphee.tesseract.world.chunk.ChunkRelease
import com.valaphee.tesseract.world.chunk.encodePosition
import com.valaphee.tesseract.world.whenTypeIs
import it.unimi.dsi.fastutil.longs.LongOpenHashSet

/**
 * @author Kevin Ludwig
 */
class View : BaseFacet<WorldContext, LocationManagerMessage>(LocationManagerMessage::class) {
    private lateinit var lastChunkPosition: Int2
    private val acquiredChunks = LongOpenHashSet()

    override suspend fun receive(message: LocationManagerMessage): Response {
        message.entity?.whenTypeIs<ActorType> {
            val (x, _, z) = it.position.toInt3()
            val chunkX = x shr 4
            val chunkZ = z shr 4
            val chunkPosition = Int2(chunkX, chunkZ)
            if (!this::lastChunkPosition.isInitialized || lastChunkPosition != chunkPosition) {
                lastChunkPosition = chunkPosition

                val chunksInDistance = LongOpenHashSet()
                for (sectorXr in -distance..distance) {
                    for (sectorZr in -distance..distance) {
                        chunksInDistance.add(encodePosition(chunkX + sectorXr, chunkZ + sectorZr))
                    }
                }

                val chunksToRelease = acquiredChunks.filterNot(chunksInDistance::contains)
                val chunksToAcquire = chunksInDistance.filterNot(acquiredChunks::contains)
                if (chunksToRelease.isNotEmpty()) {
                    acquiredChunks.removeAll(chunksToRelease)
                    message.context.world.receiveMessage(ChunkRelease(message.context, message.source, chunksToRelease.toLongArray()))
                }
                if (chunksToAcquire.isNotEmpty()) {
                    acquiredChunks.addAll(chunksToAcquire)
                    message.context.world.receiveMessage(ChunkAcquire(message.context, message.source, chunksToAcquire.toLongArray()))
                }
            }
        }

        return Pass
    }

    companion object {
        const val distance = 1
    }
}
