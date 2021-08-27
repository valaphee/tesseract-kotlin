/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk

import com.valaphee.tesseract.world.AnyEntityOfWorld
import com.valaphee.tesseract.world.WorldContext

/**
 * @author Kevin Ludwig
 */
class ChunkAcquire(
    context: WorldContext,
    source: AnyEntityOfWorld?,
    chunkPositions: LongArray
) : ChunkManagerMessage(context, source, chunkPositions) {
    override val entity: AnyEntityOfWorld? get() = null
}
