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

package com.valaphee.tesseract.world.chunk.terrain.generator.flat

import com.valaphee.foundry.math.Int2
import com.valaphee.tesseract.world.chunk.terrain.BlockStorage
import com.valaphee.tesseract.world.chunk.terrain.Terrain
import com.valaphee.tesseract.world.chunk.terrain.block.BlockState
import com.valaphee.tesseract.world.chunk.terrain.generator.Generator

/**
 * @author Kevin Ludwig
 */
class FlatGenerator(
    string: String
) : Generator {
    private val column: Array<Int>

    init {
        val column = mutableListOf<BlockState>()
        val settings = string.split(';')
        settings[0].split(',').forEach {
            val heightStart = it.indexOf('*')
            val layer = BlockState.byKeyWithStatesOrNull(it.substring(if (heightStart == -1) 0 else heightStart + 1)) ?: air
            repeat(if (heightStart == -1) 1 else it.substring(0, heightStart).toInt()) { column.add(layer) }
        }
        this.column = column.map { it.id }.toTypedArray()
    }

    override fun generate(position: Int2) = Terrain(BlockStorage().apply { repeat(BlockStorage.XZSize) { xr -> repeat(BlockStorage.XZSize) { zr -> column.forEachIndexed { i, it -> set(xr, i, zr, it) } } } })

    companion object {
        private val air = BlockState.byKeyWithStates("minecraft:air")
    }
}
