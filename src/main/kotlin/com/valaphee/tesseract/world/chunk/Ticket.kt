/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk

import com.valaphee.foundry.ecs.BaseAttribute
import com.valaphee.tesseract.actor.player.Player

/**
 * @author Kevin Ludwig
 */
class Ticket : BaseAttribute() {
    val players = mutableListOf<Player>()
}

val Chunk.players get() = findAttribute(Ticket::class).players
