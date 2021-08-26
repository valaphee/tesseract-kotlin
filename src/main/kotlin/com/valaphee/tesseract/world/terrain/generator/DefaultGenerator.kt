/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.terrain.generator

import com.valaphee.foundry.math.Int2
import com.valaphee.foundry.math.noise.FractalNoise
import com.valaphee.foundry.math.noise.PerlinNoise
import com.valaphee.tesseract.world.terrain.OcTree
import com.valaphee.tesseract.world.terrain.Terrain
import com.valaphee.tesseract.world.terrain.block.BlockState
import kotlin.math.pow

/**
 * @author Kevin Ludwig
 */
class DefaultGenerator : Generator {
    private val heightMapNoise = FractalNoise(PerlinNoise(), octaves = 6)
    private val stoneId = BlockState.byKey("minecraft:stone").findFirst().get().runtimeId
    private val dirtId = BlockState.byKey("minecraft:dirt").findFirst().get().runtimeId
    private val grassId = BlockState.byKey("minecraft:grass").findFirst().get().runtimeId

    override fun generate(position: Int2) = Terrain(OcTree().apply {
        val worldX = position.x shl 8
        val worldZ = position.y shl 8

        repeat(256) { x ->
            repeat(256) { z ->
                val heightMap = ((heightMapNoise[0, (x + worldX) / 512.0f, (z + worldZ) / 512.0f] + 1) / 2).pow(3)
                val height = 32 + (heightMap * 64).toInt()
                for(y in 0 until height - 5) set(x, y, z, stoneId)
                for(y in height - 5 until height) set(x, y, z, dirtId)
                set(x, height, z, grassId)
            }
        }
        compress()
    })
}
