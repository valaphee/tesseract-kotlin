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
class ChunkRelease(
    context: WorldContext,
    source: AnyEntityOfWorld?,
    sectorPositions: LongArray
) : ChunkManagerMessage(context, source, sectorPositions) {
    override val entity: AnyEntityOfWorld? get() = null
}
