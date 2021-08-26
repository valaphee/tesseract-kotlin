/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.sector

import com.valaphee.tesseract.world.AnyEntityOfWorld
import com.valaphee.tesseract.world.WorldContext

/**
 * @author Kevin Ludwig
 */
class SectorAcquire(
    context: WorldContext,
    source: AnyEntityOfWorld?,
    sectorPositions: LongArray
) : SectorManagerMessage(context, source, sectorPositions)
