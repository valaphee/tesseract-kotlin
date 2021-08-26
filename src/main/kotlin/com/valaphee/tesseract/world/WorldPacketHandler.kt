/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world

import com.valaphee.foundry.math.Double3
import com.valaphee.foundry.math.Float2
import com.valaphee.tesseract.actor.Location
import com.valaphee.tesseract.actor.player.AuthExtra
import com.valaphee.tesseract.actor.player.Player
import com.valaphee.tesseract.actor.player.PlayerType
import com.valaphee.tesseract.actor.player.User
import com.valaphee.tesseract.net.Connection
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.world.entity.addEntities
import com.valaphee.tesseract.world.entity.removeEntities

/**
 * @author Kevin Ludwig
 */
class WorldPacketHandler(
    private val context: WorldContext,
    private val connection: Connection,
    private val authExtra: AuthExtra,
    private val user: User,
    private val clientCacheSupported: Boolean
) : PacketHandler {
    lateinit var player: Player

    override fun initialize() {
        player = context.entityFactory(PlayerType, setOf(Location(Double3.Zero, Float2.Zero)))
        context.world.addEntities(context, player)
    }

    override fun destroy() {
        context.world.removeEntities(context, player.id)
    }

    override fun other(packet: Packet) = Unit
}
