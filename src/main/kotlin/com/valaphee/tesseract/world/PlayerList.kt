/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world

import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.tesseract.actor.player.Player
import com.valaphee.tesseract.actor.player.PlayerType
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.connection
import com.valaphee.tesseract.world.chunk.Chunk
import com.valaphee.tesseract.world.chunk.players
import com.valaphee.tesseract.world.entity.EntityAdd
import com.valaphee.tesseract.world.entity.EntityManagerMessage
import com.valaphee.tesseract.world.entity.EntityRemove

/**
 * @author Kevin Ludwig
 */
class PlayerList : BaseFacet<WorldContext, EntityManagerMessage>(EntityManagerMessage::class) {
    val players = mutableListOf<Player>()

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
}

@JvmName("worldBroadcast")
fun World.broadcast(vararg packets: Packet) = findFacet(PlayerList::class).players.forEach { packets.forEach(it.connection::write) }

@JvmName("worldBroadcast")
fun World.broadcast(source: Player, vararg packets: Packet) = findFacet(PlayerList::class).players.forEach { if (it != source) packets.forEach(it.connection::write) }

@JvmName("chunkBroadcast")
fun Chunk.broadcast(vararg packets: Packet) = players.forEach { packets.forEach(it.connection::write) }

@JvmName("chunkBroadcast")
fun Chunk.broadcast(source: Player, vararg packets: Packet) = players.forEach { if (it != source) packets.forEach(it.connection::write) }
