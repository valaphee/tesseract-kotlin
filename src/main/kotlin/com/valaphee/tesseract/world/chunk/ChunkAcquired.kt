/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk

import com.valaphee.foundry.ecs.Message
import com.valaphee.tesseract.world.AnyEntityOfWorld
import com.valaphee.tesseract.world.WorldContext

/**
 * @author Kevin Ludwig
 */
class ChunkAcquired(
    override val context: WorldContext,
    override val source: AnyEntityOfWorld?,
    val chunks: Array<Chunk>
) : Message<WorldContext> {
    override val entity: AnyEntityOfWorld? = null
}
