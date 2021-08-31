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

package com.valaphee.tesseract.world.chunk

import com.google.inject.Inject
import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.tesseract.actor.player.PlayerType
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.terrain.generator.Generator
import com.valaphee.tesseract.world.entity.addEntities
import com.valaphee.tesseract.world.entity.removeEntities
import com.valaphee.tesseract.world.whenTypeIs
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap

/**
 * @author Kevin Ludwig
 */
class ChunkManager @Inject constructor(
    private val generator: Generator
) : BaseFacet<WorldContext, ChunkManagerMessage>(ChunkManagerMessage::class) {
    private val chunks = Long2ObjectOpenHashMap<Chunk>()

    override suspend fun receive(message: ChunkManagerMessage): Response {
        val context = message.context
        when (message) {
            is ChunkAcquire -> {
                context.world.addEntities(context, message.source, *message.positions.filterNot(chunks::containsKey).map { position ->
                    (context.provider.loadChunk(position) ?: run {
                        val position = decodePosition(position)
                        context.entityFactory(ChunkType, setOf(Ticket(), Location(position), generator.generate(position)))
                    }).also { chunks[position] = it }
                }.toTypedArray())

                message.usage.chunks = message.positions.map(chunks::get).filterNotNull().onEach { chunk -> message.source?.whenTypeIs<PlayerType> { chunk.players += it } }.toTypedArray()
                message.source?.sendMessage(message.usage)
            }
            is ChunkRelease -> {
                val chunksRemoved = message.positions.filter { chunkPosition ->
                    val chunk = chunks.get(chunkPosition)
                    message.source?.whenTypeIs<PlayerType> { chunk.players -= it }
                    chunk.players.isEmpty()
                }.map(chunks::remove)
                context.provider.saveChunks(chunksRemoved)
                context.world.removeEntities(context, message.source, *chunksRemoved.map { it.id }.toLongArray())
            }
        }

        return Pass
    }
}
