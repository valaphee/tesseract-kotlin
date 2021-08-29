/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain

import com.valaphee.foundry.ecs.BaseAttribute
import com.valaphee.tesseract.world.chunk.Chunk

/**
 * @author Kevin Ludwig
 */
class Terrain(
    val blockStorage: BlockStorage
) : BaseAttribute() {
    val blockUpdates = BlockUpdateList(blockStorage)
}

val Chunk.terrain get() = findAttribute(Terrain::class)
