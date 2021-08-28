/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */
package com.valaphee.tesseract.world.provider

import com.valaphee.tesseract.world.World
import com.valaphee.tesseract.world.chunk.Chunk
import com.valaphee.tesseract.world.chunk.encodePosition
import com.valaphee.tesseract.world.chunk.position
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap

/**
 * @author Kevin Ludwig
 */
class InMemoryProvider : Provider {
    private var world: World? = null
    private var chunks = Long2ObjectOpenHashMap<Chunk>()

    override fun loadWorld(): World? = world

    override fun saveWorld(world: World) {
        this.world = world
    }

    override fun loadChunk(chunkPosition: Long): Chunk? = chunks[chunkPosition]

    override fun saveChunk(chunk: Chunk) {
        val (x, y) = chunk.position
        chunks[encodePosition(x, y)] = chunk
    }
}
