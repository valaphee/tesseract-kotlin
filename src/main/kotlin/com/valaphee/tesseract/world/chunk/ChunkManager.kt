/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk

import com.google.inject.Inject
import com.valaphee.foundry.ecs.MessageResponse
import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.tesseract.actor.player.PlayerType
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.terrain.generator.Generator
import com.valaphee.tesseract.world.entity.addEntities
import com.valaphee.tesseract.world.entity.removeEntities
import com.valaphee.tesseract.world.whenTypeIs
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap

/**
 * @author Kevin Ludwig
 */
class ChunkManager @Inject constructor(
    private val generator: Generator
) : BaseFacet<WorldContext, ChunkManagerMessage>(ChunkManagerMessage::class) {
    private val chunks = Long2ObjectOpenHashMap<Chunk>()

    override suspend fun receive(message: ChunkManagerMessage): Response {
        val context = message.context
        when (message) {
            is ChunkAcquire -> {
                context.world.addEntities(context, message.source, *message.chunkPositions.filterNot(chunks::containsKey).map {
                    /*context.provider.loadChunk(it) ?: run {*/
                    val position = decodePosition(it)
                    context.entityFactory(ChunkType, setOf(Ticket(), Location(position), generator.generate(position))).apply { chunks[it] = this }
                    /*}*/
                }.toTypedArray())
                return MessageResponse(ChunkAcquired(context, message.source, message.chunkPositions.map(chunks::get).filterNotNull().onEach { chunk -> message.source?.let { message.source?.whenTypeIs<PlayerType> { chunk.players += it } } }.toTypedArray()))
            }
            is ChunkRelease -> {
                val chunksRemoved = message.chunkPositions.filter { chunkPosition ->
                    val chunk = chunks.get(chunkPosition)
                    message.source?.whenTypeIs<PlayerType> { chunk.players -= it }
                    chunk.players.isEmpty()
                }.map(chunks::remove)
                context.provider.saveChunks(chunksRemoved)
                context.world.removeEntities(context, message.source, *chunksRemoved.map { it.id }.toLongArray())
            }
        }

        return Pass
    }
}
