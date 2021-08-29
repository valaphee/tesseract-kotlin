/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
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
            val layer = BlockState.byKeyWithStates(it.substring(if (heightStart == -1) 0 else heightStart + 1)) ?: air
            repeat(if (heightStart == -1) 1 else it.substring(0, heightStart).toInt()) { column.add(layer) }
        }
        this.column = column.map { it.id }.toTypedArray()
    }

    override fun generate(position: Int2) = Terrain(BlockStorage().apply { repeat(BlockStorage.XZSize) { xr -> repeat(BlockStorage.XZSize) { zr -> column.forEachIndexed { i, it -> set(xr, i, zr, it) } } } })

    companion object {
        private val air = BlockState.byKeyWithStates("minecraft:air") ?: error("Missing minecraft:air")
    }
}
