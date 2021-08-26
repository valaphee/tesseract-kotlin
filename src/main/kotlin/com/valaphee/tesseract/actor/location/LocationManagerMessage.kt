/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.location

import com.valaphee.foundry.ecs.Message
import com.valaphee.tesseract.world.WorldContext

/**
 * @author Kevin Ludwig
 */
sealed class LocationManagerMessage(
    override val context: WorldContext
) : Message<WorldContext>
