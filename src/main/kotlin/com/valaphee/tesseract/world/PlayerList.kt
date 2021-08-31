/*
 * MIT License
 *
 * Copyright (c) 2021, Valaphee.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
            is EntityAdd -> message.entities.first().whenTypeIs<PlayerType> {
                players.add(it)
                broadcastSystemMessage("${it.authExtra.userName} entered the world")
            }
            is EntityRemove -> message.context.engine.findEntityOrNull(message.entityIds.first())?.whenTypeIs<PlayerType> {
                broadcastSystemMessage("${it.authExtra.userName} exited the world")
                players.remove(it)
            }
        }

        return Pass
    }

    fun broadcast(vararg packets: Packet) = players.forEach { packets.forEach(it.connection::write) }

    fun broadcast(source: Player, vararg packets: Packet) = players.forEach { if (it != source) packets.forEach(it.connection::write) }

    fun broadcastSystemMessage(message: String) {
        broadcast(TextPacket(TextPacket.Type.System, false, null, message, null, "", ""))
        chatLog.info("System: {}", message)
    }

    companion object {
        private val chatLog: Logger = LogManager.getLogger("Chat")
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
