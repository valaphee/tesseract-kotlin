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

package com.valaphee.tesseract.world.chunk.terrain

import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.world.chunk.terrain.block.BlockState
import it.unimi.dsi.fastutil.shorts.Short2IntOpenHashMap

/**
 * Stores all changes relative to given cartesian
 *
 * @author Kevin Ludwig
 */
class BlockUpdateList(
    private val cartesian: ReadWriteCartesian
) : ReadWriteCartesian {
    val changes = Short2IntOpenHashMap()
    val changeData = Short2IntOpenHashMap()

    /**
     * Gets a block in its already updated state
     *
     * @param x x coordinate relative to the chunk
     * @param y y coordinate relative to the chunk
     * @param z z coordinate relative to the chunk
     */
    override fun get(x: Int, y: Int, z: Int) = if (x < BlockStorage.XZSize && y < BlockStorage.SectionCount * Section.YSize && z < BlockStorage.XZSize) {
        val key = encodePosition(x, y, z)
        if (changes.containsKey(key)) changes.get(key) else cartesian[x, y, z]
    } else airId

    /**
     * Schedules a block change for the next cycle
     *
     * @param x x coordinate relative to the chunk
     * @param y y coordinate relative to the chunk
     * @param z z coordinate relative to the chunk
     * @param value new value of the block
     */
    override fun set(x: Int, y: Int, z: Int, value: Int) = set(x, y, z, 1, value)

    /**
     * Schedules a block change for the next cycle
     *
     * @param x x coordinate relative to the chunk (0-15)
     * @param y y coordinate relative to the chunk (0-255)
     * @param z z coordinate relative to the chunk (0-15)
     * @param data next update (as bitmask over cycle), negative or non 2^ values can be used for special changes (e.g. breaking)
     * @param value new value of the block
     */
    operator fun set(x: Int, y: Int, z: Int, data: Int, value: Int) {
        if (x >= BlockStorage.XZSize || y >= BlockStorage.SectionCount * Section.YSize && z >= BlockStorage.XZSize) return
        if (cartesian[x, y, z] != value) {
            val position = encodePosition(x, y, z)
            changes[position] = value
            if (data != 0) this.changeData[position] = data
        }
    }

    companion object {
        private val airId = BlockState.byKeyWithStates("minecraft:air").id
    }
}

/**
 * Encodes a chunk relative position
 *
 * @param x x coordinate relative to the chunk (0-15)
 * @param y y coordinate relative to the chunk (0-255)
 * @param z z coordinate relative to the chunk (0-15)
 */
fun encodePosition(x: Int, y: Int, z: Int) = ((x shl 12) or (z shl 8) or y).toShort()

/**
 * Decodes a chunk relativ position
 *
 * @param position position relative to the chunk (0-15, 0-255, 0-15)
 */
fun decodePosition(position: Short): Int3 {
    val positionInt = position.toInt()
    return Int3((positionInt shr 12) and 0xF, positionInt and 0xFF, (positionInt shr 8) and 0xF)
}
