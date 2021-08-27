/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

import com.fasterxml.jackson.annotation.JsonIgnore
import com.valaphee.foundry.ecs.BaseAttribute
import com.valaphee.tesseract.actor.player.Player
import com.valaphee.tesseract.net.Connection

class ConnectionAttribute(
    @JsonIgnore val connection: Connection
) : BaseAttribute()

val Player.connection get() = findAttribute(ConnectionAttribute::class).connection
