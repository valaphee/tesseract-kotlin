/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.location

import com.valaphee.foundry.math.Double3
import com.valaphee.foundry.math.Float2
import com.valaphee.tesseract.actor.AnyActorOfWorld
import com.valaphee.tesseract.world.WorldContext

/**
 * @author Kevin Ludwig
 */
class Teleport(
    context: WorldContext,
    override val source: AnyActorOfWorld,
    val position: Double3,
    val rotation: Float2,
) : LocationManagerMessage(context)
