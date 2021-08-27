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
object NullBackend : Backend {
    override fun loadWorld(): World? = null

    override fun saveWorld(world: World) = Unit

    override fun loadChunk(chunkPosition: Long): Chunk? = null

    override fun saveChunk(chunk: Chunk) = Unit
}
