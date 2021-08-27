/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain

import com.valaphee.foundry.ecs.Message
import com.valaphee.tesseract.world.AnyEntityOfWorld
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.Chunk
import it.unimi.dsi.fastutil.shorts.Short2IntMap

/**
 * @author Kevin Ludwig
 */
class CartesianDeltaMerge(
    override val context: WorldContext,
    override val entity: Chunk,
    val changes: Short2IntMap,
) : Message<WorldContext> {
    override val source: AnyEntityOfWorld? get() = null
}
