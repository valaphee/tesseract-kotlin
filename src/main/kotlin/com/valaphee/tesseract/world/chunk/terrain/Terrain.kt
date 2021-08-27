/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.valaphee.foundry.ecs.BaseAttribute
import com.valaphee.tesseract.world.chunk.Chunk
import com.valaphee.tesseract.world.chunk.terrain.blocks.BlockStorage

/**
 * @author Kevin Ludwig
 */
class Terrain(
    val blockStorage: BlockStorage,
) : BaseAttribute() {
    @JsonIgnore
    val cartesianDelta = CartesianDelta(blockStorage)

    @JsonIgnore
    var lastCartesianDeltaMerge = 0
}

val Chunk.terrain get() = findAttribute(Terrain::class)
