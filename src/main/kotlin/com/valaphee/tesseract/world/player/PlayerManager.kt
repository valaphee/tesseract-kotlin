/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.player

import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.actor.Actor
import com.valaphee.tesseract.actor.location.position
import com.valaphee.tesseract.actor.player.Player
import com.valaphee.tesseract.actor.player.PlayerType
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.connection
import com.valaphee.tesseract.world.World
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.entity.EntityAdd
import com.valaphee.tesseract.world.entity.EntityManagerMessage
import com.valaphee.tesseract.world.entity.EntityRemove
import com.valaphee.tesseract.world.filterType

/**
 * @author Kevin Ludwig
 */
class PlayerManager : BaseFacet<WorldContext, EntityManagerMessage>(EntityManagerMessage::class) {
    private val players = mutableListOf<Player>()

    override suspend fun receive(message: EntityManagerMessage): Response {
        when (message) {
            is EntityAdd -> {
                val players = message.entities.filterType<PlayerType>()
                /*broadcast(PlayerListPacket(PlayerListPacket.Action.Add, players.map {
                    val authExtra = it.authExtra
                    val user = it.user
                    PlayerListPacket.Entry(authExtra.userId, it.id, authExtra.userName, authExtra.xboxUserId, "", user.operatingSystem, user.appearance, false, false)
                }.toTypedArray()))*/
                this.players.addAll(players)
                /*val packet = PlayerListPacket(PlayerListPacket.Action.Add, this.players.map {
                    val authExtra = it.authExtra
                    val user = it.user
                    PlayerListPacket.Entry(authExtra.userId, it.id, authExtra.userName, authExtra.xboxUserId, "", user.operatingSystem, user.appearance, false, false)
                }.toTypedArray())
                players.forEach { it.connection.write(packet) }*/
            }
            is EntityRemove -> {
                val engine = message.context.engine
                val players = message.entityIds.map { engine.findEntityOrNull(it) }.filterNotNull().filterType<PlayerType>()
                this.players.removeAll(players)
                /*broadcast(PlayerListPacket(PlayerListPacket.Action.Remove, players.map { PlayerListPacket.Entry(it.authExtra.userId) }.toTypedArray()))*/
            }
        }

        return Pass
    }

    fun geocast(origin: Float3, distance2: Float, vararg packets: Packet) {
        @Suppress("UNCHECKED_CAST")
        players.forEach { if (origin.distance2((it as Actor).position) <= distance2) packets.forEach(it.connection::write) }
    }

    fun broadcast(vararg packets: Packet) {
        players.forEach { packets.forEach(it.connection::write) }
    }

    fun broadcast(player: Player, vararg packets: Packet) {
        players.forEach { if (it != player) packets.forEach(it.connection::write) }
    }
}

fun World.geocast(origin: Float3, distance2: Float, vararg packets: Packet) = findFacet(PlayerManager::class).geocast(origin, distance2, *packets)

fun World.broadcast(vararg packets: Packet) = findFacet(PlayerManager::class).broadcast(*packets)

fun World.broadcast(source: Player, vararg packets: Packet) = findFacet(PlayerManager::class).broadcast(source, *packets)
