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

package com.valaphee.tesseract.world.provider

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Inject
import com.google.inject.name.Named
import com.valaphee.tesseract.actor.player.Player
import com.valaphee.tesseract.world.World
import com.valaphee.tesseract.world.chunk.Chunk
import com.valaphee.tesseract.world.chunk.encodePosition
import com.valaphee.tesseract.world.chunk.position
import org.fusesource.leveldbjni.JniDBFactory
import org.iq80.leveldb.DB
import org.iq80.leveldb.Options
import java.io.File
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
class TesseractProvider @Inject constructor(
    @Named("world") private val objectMapper: ObjectMapper,
) : Provider {
    private val database: DB = JniDBFactory.factory.open(File("world"), Options().createIfMissing(true).blockSize(64 * 1024))

    override fun loadWorld() = synchronized(database) { database.get(worldKey) }?.let { objectMapper.readValue<World>(it) }

    override fun saveWorld(world: World) {
        val value = objectMapper.writeValueAsBytes(world)
        synchronized(database) { database.put(worldKey, value) }
    }

    override fun loadPlayer(userId: UUID) = synchronized(database) { database.get(playerKey(userId)) }?.let { objectMapper.readValue<Player>(it) }

    override fun savePlayer(userId: UUID, player: Player) {
        val value = objectMapper.writeValueAsBytes(player)
        synchronized(database) { database.put(playerKey(userId), value) }
    }

    override fun loadChunk(chunkPosition: Long) = synchronized(database) { database.get(chunkKey(chunkPosition)) }?.let { objectMapper.readValue<Chunk>(it) }

    override fun saveChunks(chunks: Iterable<Chunk>) {
        database.createWriteBatch().use { batch ->
            chunks.forEach {
                val (x, z) = it.position
                batch.put(chunkKey(encodePosition(x, z)), objectMapper.writeValueAsBytes(it))
            }
            synchronized(database) { database.write(batch) }
        }
    }

    override fun destroy() {
        synchronized(database) { database.close() }
    }

    companion object {
        private val worldKey = byteArrayOf()

        fun playerKey(userId: UUID): ByteArray {
            val mostSignificantBits = userId.mostSignificantBits
            val leastSignificantBits = userId.leastSignificantBits
            return byteArrayOf(
                ((mostSignificantBits shr 56) and 0xFF).toByte(),
                ((mostSignificantBits shr 48) and 0xFF).toByte(),
                ((mostSignificantBits shr 40) and 0xFF).toByte(),
                ((mostSignificantBits shr 32) and 0xFF).toByte(),
                ((mostSignificantBits shr 24) and 0xFF).toByte(),
                ((mostSignificantBits shr 16) and 0xFF).toByte(),
                ((mostSignificantBits shr 8) and 0xFF).toByte(),
                (mostSignificantBits and 0xFF).toByte(),
                ((leastSignificantBits shr 56) and 0xFF).toByte(),
                ((leastSignificantBits shr 48) and 0xFF).toByte(),
                ((leastSignificantBits shr 40) and 0xFF).toByte(),
                ((leastSignificantBits shr 32) and 0xFF).toByte(),
                ((leastSignificantBits shr 24) and 0xFF).toByte(),
                ((leastSignificantBits shr 16) and 0xFF).toByte(),
                ((leastSignificantBits shr 8) and 0xFF).toByte(),
                (leastSignificantBits and 0xFF).toByte()
            )
        }

        fun chunkKey(position: Long = 0L) = byteArrayOf(
            ((position shr 56) and 0xFF).toByte(),
            ((position shr 48) and 0xFF).toByte(),
            ((position shr 40) and 0xFF).toByte(),
            ((position shr 32) and 0xFF).toByte(),
            ((position shr 24) and 0xFF).toByte(),
            ((position shr 16) and 0xFF).toByte(),
            ((position shr 8) and 0xFF).toByte(),
            (position and 0xFF).toByte()
        )
    }
}
