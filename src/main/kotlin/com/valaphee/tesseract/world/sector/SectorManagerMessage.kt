/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.sector

import com.valaphee.foundry.ecs.Message
import com.valaphee.tesseract.world.AnyEntityOfWorld
import com.valaphee.tesseract.world.WorldContext

/**
 * @author Kevin Ludwig
 */
sealed class SectorManagerMessage(
    override val context: WorldContext,
    override val source: AnyEntityOfWorld?,
    val sectorPositions: LongArray
) : Message<WorldContext>
