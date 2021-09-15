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

import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.actor.player.Player
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.connection
import com.valaphee.tesseract.world.World
import com.valaphee.tesseract.world.WorldContext

/**
 * @author Kevin Ludwig
 */
class Broadcast(
    override val context: WorldContext,
    override val source: Player?,
    val packets: Array<out Packet>
) : ChunkUsage {
    override val entity get() = source

    override lateinit var chunks: Array<Chunk>
}

fun Chunk.broadcast(vararg packets: Packet) = viewers.forEach { packets.forEach(it.connection::write) }

fun Chunk.broadcast(source: Player, vararg packets: Packet) = viewers.forEach { if (it != source) packets.forEach(it.connection::write) }

fun World.chunkBroadcast(context: WorldContext, position: Float3, vararg packets: Packet) {
    val (x, _, z) = position.toInt3()
    sendMessage(ChunkAcquire(context, context.world, longArrayOf(encodePosition(x shr 4, z shr 4)), Broadcast(context, null, packets)))
}

fun World.chunkBroadcast(context: WorldContext, source: Player, position: Float3, vararg packets: Packet) {
    val (x, _, z) = position.toInt3()
    sendMessage(ChunkAcquire(context, context.world, longArrayOf(encodePosition(x shr 4, z shr 4)), Broadcast(context, source, packets)))
}
