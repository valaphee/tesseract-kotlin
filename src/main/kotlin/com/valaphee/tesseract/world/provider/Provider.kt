/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.provider

import com.valaphee.tesseract.world.World
import com.valaphee.tesseract.world.chunk.Chunk

/**
 * @author Kevin Ludwig
 */
interface Provider {
    fun loadWorld(): World?

    fun saveWorld(world: World)

    fun loadChunk(chunkPosition: Long): Chunk?

    fun saveChunks(chunks: Iterable<Chunk>)
}
