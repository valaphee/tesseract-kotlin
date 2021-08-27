/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk

import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.terrain.Terrain
import com.valaphee.tesseract.world.chunk.terrain.blocks.Blocks
import com.valaphee.tesseract.world.entity.addEntities
import com.valaphee.tesseract.world.entity.removeEntities
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap

/**
 * @author Kevin Ludwig
 */
class ChunkManager : BaseFacet<WorldContext, ChunkManagerMessage>(ChunkManagerMessage::class) {
    private val chunks = Long2ObjectOpenHashMap<Chunk>()

    override suspend fun receive(message: ChunkManagerMessage): Response {
        val context = message.context
        when (message) {
            is ChunkAcquire -> context.world.addEntities(context, message.source, *message.sectorPositions.filterNot(chunks::containsKey).map { context.backend.loadChunk(it) ?: context.entityFactory(ChunkType, setOf(Location(decodePosition(it)), Terrain(Blocks()))).apply { chunks[it] = this } }.toTypedArray())
            is ChunkRelease -> context.world.removeEntities(context, message.source, *message.sectorPositions.map { chunks.remove(it)?.also { context.backend.saveChunk(it) }?.id }.filterNotNull().toLongArray())
        }

        return Pass
    }
}
