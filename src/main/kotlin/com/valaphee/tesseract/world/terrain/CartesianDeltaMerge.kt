/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.terrain

import com.valaphee.foundry.ecs.Message
import com.valaphee.tesseract.world.AnyEntityOfWorld
import com.valaphee.tesseract.world.WorldContext
import it.unimi.dsi.fastutil.ints.Int2ObjectMap

/**
 * @author Kevin Ludwig
 */
class CartesianDeltaMerge(
    override val context: WorldContext,
    val changes: Int2ObjectMap<Int>,
) : Message<WorldContext> {
    override val source: AnyEntityOfWorld? get() = null
}
