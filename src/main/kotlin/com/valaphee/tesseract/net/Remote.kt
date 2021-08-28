/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net

import com.fasterxml.jackson.annotation.JsonIgnore
import com.valaphee.foundry.ecs.BaseAttribute
import com.valaphee.tesseract.actor.player.Player

/**
 * @author Kevin Ludwig
 */
class Remote(
    @JsonIgnore val connection: Connection
) : BaseAttribute()

val Player.connection get() = findAttribute(Remote::class).connection
