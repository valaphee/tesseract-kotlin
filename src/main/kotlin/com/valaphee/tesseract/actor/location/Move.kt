/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.location

import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.actor.AnyActorOfWorld
import com.valaphee.tesseract.world.WorldContext

/**
 * @author Kevin Ludwig
 */
class Move(
    context: WorldContext,
    override val source: AnyActorOfWorld,
    val move: Float3
) : LocationManagerMessage(context) {
    override val entity get() = source
}
