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

import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.actor.player.Player
import com.valaphee.tesseract.world.World
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.Chunk
import com.valaphee.tesseract.world.chunk.ChunkAcquire
import com.valaphee.tesseract.world.chunk.ChunkUsage
import com.valaphee.tesseract.world.chunk.encodePosition

/**
 * @author Kevin Ludwig
 */
// just for experimenting, currently
class BlockBreak(
    override val context: WorldContext,
    override val source: Player,
    val position: Int3,
    val place: Boolean = false,
) : ChunkUsage {
    override val entity get() = source

    override lateinit var chunks: Array<Chunk>
}

fun World.breakBlock(context: WorldContext, source: Player, position: Int3) {
    val (x, y, z) = position
    sendMessage(ChunkAcquire(context, source, longArrayOf(encodePosition(x shr 4, z shr 4)), BlockBreak(context, source, Int3(x and 0xF, y and 0xFF, z and 0xF))))
}

fun World.placeBlock(context: WorldContext, source: Player, position: Int3) {
    val (x, y, z) = position
    sendMessage(ChunkAcquire(context, source, longArrayOf(encodePosition(x shr 4, z shr 4)), BlockBreak(context, source, Int3(x and 0xF, y and 0xFF, z and 0xF), true)))
}
