/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
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
        private val stoneId = BlockState.byKeyWithStates("minecraft:stone")?.runtimeId ?: error("Missing minecraft:stone")
        private val dirtId = BlockState.byKeyWithStates("minecraft:dirt")?.runtimeId ?: error("Missing minecraft:dirt")
        private val grassId = BlockState.byKeyWithStates("minecraft:grass")?.runtimeId ?: error("Missing minecraft:grass")
    }
}
