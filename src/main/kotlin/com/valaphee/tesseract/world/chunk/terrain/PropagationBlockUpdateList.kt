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
import com.valaphee.tesseract.data.block.BlockState

/**
 * @author Kevin Ludwig
 */
class PropagationBlockUpdateList(
    center: BlockUpdateList
) : ReadWriteCartesian {
    internal val chunks = arrayOfNulls<BlockUpdateList>(9)

    init {
        val (_, _, index) = indexAndOffset(0, 0)
        chunks[index] = center
    }

    override fun get(x: Int, y: Int, z: Int): Int {
        val (offsetX, offsetZ, index) = indexAndOffset(x, z)
        return chunks[index]?.get(x + offsetX, y, z + offsetZ) ?: TODO()
    }

    override fun set(x: Int, y: Int, z: Int, value: Int) = set(x, y, z, 0b1, value)

    operator fun set(x: Int, y: Int, z: Int, data: Int, value: Int) {
        val (offsetX, offsetZ, index) = indexAndOffset(x, z)
        chunks[index]?.let {
            it[x + offsetX, y, z + offsetZ, data] = value
            /*it.setData(x + offsetX, y - 1, z + offsetZ, 0b1)
            it.setData(x + offsetX, y + 1, z + offsetZ, 0b1)*/
        } ?: TODO()
        /*val x0 = x - 1
        val (offsetX0, offsetZ0, index0) = indexAndOffset(x0, z)
        chunks[index0]?.setData(x0 + offsetX0, y, z + offsetZ0, 0b1) ?: TODO()
        val z1 = z - 1
        val (offsetX1, offsetZ1, index1) = indexAndOffset(x, z1)
        chunks[index1]?.setData(x + offsetX1, y, z1 + offsetZ1, 0b1) ?: TODO()
        val x2 = x + 1
        val (offsetX2, offsetZ2, index2) = indexAndOffset(x2, z)
        chunks[index2]?.setData(x2 + offsetX2, y, z + offsetZ2, 0b1) ?: TODO()
        val z3 = z + 1
        val (offsetX3, offsetZ3, index3) = indexAndOffset(x, z3)
        chunks[index3]?.setData(x + offsetX3, y, z3 + offsetZ3, 0b1) ?: TODO()*/
    }

    private fun indexAndOffset(x: Int, z: Int) = when {
        x < 0 -> when {
            z < 0 -> Int3(BlockStorage.XZSize, BlockStorage.XZSize, 4)
            z >= BlockStorage.XZSize -> Int3(BlockStorage.XZSize, -BlockStorage.XZSize, 5)
            else -> Int3(BlockStorage.XZSize, 0, 0)
        }
        x >= BlockStorage.XZSize -> when {
            z < 0 -> Int3(-BlockStorage.XZSize, BlockStorage.XZSize, 6)
            z >= BlockStorage.XZSize -> Int3(-BlockStorage.XZSize, -BlockStorage.XZSize, 7)
            else -> Int3(-BlockStorage.XZSize, 0, 2)
        }
        else -> when {
            z < 0 -> Int3(0, BlockStorage.XZSize, 1)
            z >= BlockStorage.XZSize -> Int3(0, -BlockStorage.XZSize, 3)
            else -> Int3(0, 0, 8)
        }
    }

    companion object {
        private val borderId = BlockState.byKeyWithStates("minecraft:stone").id
    }
}
