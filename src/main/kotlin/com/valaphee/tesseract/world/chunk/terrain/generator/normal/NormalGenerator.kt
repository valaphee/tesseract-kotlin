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

package com.valaphee.tesseract.world.chunk.terrain.generator.normal

import com.valaphee.foundry.math.Int2
import com.valaphee.foundry.math.noise.FractalNoise
import com.valaphee.foundry.math.noise.PerlinNoise
import com.valaphee.tesseract.world.chunk.terrain.BlockStorage
import com.valaphee.tesseract.world.chunk.terrain.Terrain
import com.valaphee.tesseract.world.chunk.terrain.block.BlockState
import com.valaphee.tesseract.world.chunk.terrain.generator.Generator
import kotlin.math.pow

/**
 * @author Kevin Ludwig
 */
class NormalGenerator(
    private val seed: Int
) : Generator {
    private val heightMapNoise = FractalNoise(PerlinNoise(), octaves = 6)

    override fun generate(position: Int2) = Terrain(BlockStorage().apply {
        val x = position.x * BlockStorage.XZSize
        val z = position.y * BlockStorage.XZSize
        repeat(BlockStorage.XZSize) { xr ->
            repeat(BlockStorage.XZSize) { zr ->
                val height = (((heightMapNoise[seed, (xr + x) / 512.0f, (zr + z) / 512.0f] + 1) / 2).pow(3.0f) * 512.0f).toInt()
                for (y in 0 until height - 5) set(xr, y, zr, stoneId)
                for (y in height - 5 until height) set(xr, y, zr, dirtId)
                set(xr, height, zr, grassId)
            }
        }
    })

    companion object {
        private val stoneId = BlockState.byKeyWithStates("minecraft:stone")?.id ?: error("Missing minecraft:stone")
        private val dirtId = BlockState.byKeyWithStates("minecraft:dirt")?.id ?: error("Missing minecraft:dirt")
        private val grassId = BlockState.byKeyWithStates("minecraft:grass")?.id ?: error("Missing minecraft:grass")
    }
}
