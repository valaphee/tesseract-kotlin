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
import com.valaphee.tesseract.actor.player.authExtra
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.base.TextPacket
import com.valaphee.tesseract.net.connection
import com.valaphee.tesseract.world.chunk.Chunk
import com.valaphee.tesseract.world.chunk.players
import com.valaphee.tesseract.world.entity.EntityAdd
import com.valaphee.tesseract.world.entity.EntityManagerMessage
import com.valaphee.tesseract.world.entity.EntityRemove
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * @author Kevin Ludwig
 */
class PlayerList : BaseFacet<WorldContext, EntityManagerMessage>(EntityManagerMessage::class) {
    private val players = mutableListOf<Player>()

    override suspend fun receive(message: EntityManagerMessage): Response {
        when (message) {
            is EntityAdd -> {
                message.entities.first().whenTypeIs<PlayerType> {
                    players.add(it)
                    log.info("Added player {}, id is {}", it.authExtra.userName, it.id)
                    broadcastSystemMessage("${it.authExtra.userName} entered the world")
                }
            }
            is EntityRemove -> {
                val engine = message.context.engine
                engine.findEntityOrNull(message.entityIds.first())?.whenTypeIs<PlayerType> {
                    broadcastSystemMessage("${it.authExtra.userName} exited the world")
                    log.info("Removed player {}, id was {}", it.authExtra.userName, it.id)
                    players.remove(it)
                }
            }
        }

        return Pass
    }

    fun broadcast(vararg packets: Packet) = players.forEach { packets.forEach(it.connection::write) }

    fun broadcast(source: Player, vararg packets: Packet) = players.forEach { if (it != source) packets.forEach(it.connection::write) }

    fun broadcastSystemMessage(message: String) {
        broadcast(TextPacket(TextPacket.Type.System, false, null, message, null, "", ""))
        log.info("System: {}", message)
    }

    companion object {
        private val log: Logger = LogManager.getLogger(PlayerList::class.java)
    }
}

@JvmName("worldBroadcast")
fun World.broadcast(vararg packets: Packet) = findFacet(PlayerList::class).broadcast(*packets)

@JvmName("worldBroadcast")
fun World.broadcast(source: Player, vararg packets: Packet) = findFacet(PlayerList::class).broadcast(source, *packets)

fun World.broadcastSystemMessage(message: String) = findFacet(PlayerList::class).broadcastSystemMessage(message)

@JvmName("chunkBroadcast")
fun Chunk.broadcast(vararg packets: Packet) = players.forEach { packets.forEach(it.connection::write) }

@JvmName("chunkBroadcast")
fun Chunk.broadcast(source: Player, vararg packets: Packet) = players.forEach { if (it != source) packets.forEach(it.connection::write) }
