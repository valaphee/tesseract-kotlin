/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk

import com.valaphee.foundry.ecs.MessageResponse
import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.terrain.generator.DefaultGenerator
import com.valaphee.tesseract.world.chunk.terrain.generator.Generator
import com.valaphee.tesseract.world.entity.addEntities
import com.valaphee.tesseract.world.entity.removeEntities
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap

/**
 * @author Kevin Ludwig
 */
class ChunkManager : BaseFacet<WorldContext, ChunkManagerMessage>(ChunkManagerMessage::class) {
    private val generator: Generator = DefaultGenerator()
    private val chunks = Long2ObjectOpenHashMap<Chunk>()

    override suspend fun receive(message: ChunkManagerMessage): Response {
        val context = message.context
        when (message) {
            is ChunkAcquire -> {
                context.world.addEntities(context, message.source, *message.chunkPositions.filterNot(chunks::containsKey).map {
                    context.backend.loadChunk(it) ?: run {
                        val position = decodePosition(it)
                        context.entityFactory(ChunkType, setOf(Location(position), generator.generate(position))).apply { chunks[it] = this }
                    }
                }.toTypedArray())
                return MessageResponse(ChunkAcquired(context, message.source, message.chunkPositions.map(chunks::get).filterNotNull().toTypedArray()))
            }
            is ChunkRelease -> context.world.removeEntities(context, message.source, *message.chunkPositions.map { chunks.remove(it)?.also { context.backend.saveChunk(it) }?.id }.filterNotNull().toLongArray())
        }

        return Pass
    }
}
