/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.persistence

import com.valaphee.tesseract.world.World
import com.valaphee.tesseract.world.chunk.Chunk

/**
 * @author Kevin Ludwig
 */
interface Backend {
    fun loadWorld(): World?

    fun saveWorld(world: World)

    fun loadChunk(chunkPosition: Long): Chunk?

    fun saveChunk(chunk: Chunk)
}
