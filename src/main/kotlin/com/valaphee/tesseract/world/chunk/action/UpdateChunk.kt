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
 *
 */

package com.valaphee.tesseract.world.chunk.action

import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.world.chunk.ReadWriteBlockAccess
import it.unimi.dsi.fastutil.shorts.Short2IntOpenHashMap

/**
 * @author Kevin Ludwig
 */
class UpdateChunk(
    private val base: ReadWriteBlockAccess
) : ReadWriteBlockAccess {
    private val changes = Short2IntOpenHashMap()
    private val changesAttachment = Short2IntOpenHashMap()

    override fun get(x: Int, y: Int, z: Int): Int {
        val key = encodePosition(x, y, z)
        return if (changes.containsKey(key)) changes.get(key) else base[x, y, z]
    }

    override fun set(x: Int, y: Int, z: Int, value: Int) = set(x, y, z, 0b1, value)

    operator fun set(x: Int, y: Int, z: Int, attachment: Int, value: Int) {
        if (base[x, y, z] != value) {
            val position = encodePosition(x, y, z)
            changes[position] = value
            if (attachment != 0) this.changesAttachment[position] = attachment
        }
    }
}

fun encodePosition(x: Int, y: Int, z: Int) = ((x shl 12) or (z shl 8) or y).toShort()

fun decodePosition(position: Short): Int3 {
    val positionInt = position.toInt()
    return Int3((positionInt shr 12) and 0xF, positionInt and 0xFF, (positionInt shr 8) and 0xF)
}
