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

package com.valaphee.tesseract.world.chunk.terrain.block

import com.valaphee.tesseract.util.nbt.CompoundTag
import com.valaphee.tesseract.world.chunk.terrain.BlockUpdateList

/**
 * @author Kevin Ludwig
 */
class Block(
    val key: String,
    val component: CompoundTag? = null
) {
    var onUpdate: OnUpdate? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Block

        if (key != other.key) return false

        return true
    }

    override fun hashCode() = key.hashCode()

    override fun toString() = key

    companion object {
        private val byKey = mutableMapOf<String, Block>()
        private var finished = false

        fun finish() {
            check(!finished) { "Already finished" }

            finished = true
            BlockState.all.groupBy { it.key }.forEach { (key, states) ->
                val block = Block(key).also { byKey[key] = it }
                states.forEach { it.block = block }
            }
        }

        fun byKeyOrNull(key: String) = byKey[key]

        val all get() = byKey.values
    }
}

typealias OnUpdate = (BlockUpdateList, Int, Int, Int, BlockState) -> Unit
