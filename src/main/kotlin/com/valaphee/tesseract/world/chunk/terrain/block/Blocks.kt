/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain.block

object Blocks {
    fun populate() {
        val airId = BlockState.byKeyWithStates("minecraft:air")?.id ?: error("Missing minecraft:air")

        Block.byKey("minecraft:water")?.apply {
            onUpdate = { cartesian, x, y, z, state ->
                val id = state.id
                if (
                    cartesian.setIfEmpty(x, y - 1, z, id, 5)
                    || cartesian.setIfEmpty(x - 1, y - 1, z, id, 5)
                    || cartesian.setIfEmpty(x, y - 1, z - 1, id, 5)
                    || cartesian.setIfEmpty(x + 1, y - 1, z, id, 5)
                    || cartesian.setIfEmpty(x, y - 1, z + 1, id, 5)
                    || cartesian.setIfEmpty(x - 1, y - 1, z - 1, id, 5)
                    || cartesian.setIfEmpty(x - 1, y - 1, z + 1, id, 5)
                    || cartesian.setIfEmpty(x + 1, y - 1, z - 1, id, 5)
                    || cartesian.setIfEmpty(x + 1, y - 1, z + 1, id, 5)
                    || cartesian.setIfEmpty(x - 1, y, z, id, 5)
                    || cartesian.setIfEmpty(x, y, z - 1, id, 5)
                    || cartesian.setIfEmpty(x + 1, y, z, id, 5)
                    || cartesian.setIfEmpty(x, y, z + 1, id, 5)
                    || cartesian.setIfEmpty(x - 1, y, z - 1, id, 5)
                    || cartesian.setIfEmpty(x - 1, y, z + 1, id, 5)
                    || cartesian.setIfEmpty(x + 1, y, z - 1, id, 5)
                    || cartesian.setIfEmpty(x + 1, y, z + 1, id, 5)
                ) cartesian[x, y, z] = airId
            }
        }
        Block.byKey("minecraft:lava")?.apply {
            onUpdate = { cartesian, x, y, z, state ->
                val id = state.id
                if (
                    cartesian.setIfEmpty(x, y - 1, z, id, 10)
                    || cartesian.setIfEmpty(x - 1, y - 1, z, id, 10)
                    || cartesian.setIfEmpty(x, y - 1, z - 1, id, 10)
                    || cartesian.setIfEmpty(x + 1, y - 1, z, id, 10)
                    || cartesian.setIfEmpty(x, y - 1, z + 1, id, 10)
                    || cartesian.setIfEmpty(x - 1, y - 1, z - 1, id, 10)
                    || cartesian.setIfEmpty(x - 1, y - 1, z + 1, id, 10)
                    || cartesian.setIfEmpty(x + 1, y - 1, z - 1, id, 10)
                    || cartesian.setIfEmpty(x + 1, y - 1, z + 1, id, 10)
                    || cartesian.setIfEmpty(x - 1, y, z, id, 10)
                    || cartesian.setIfEmpty(x, y, z - 1, id, 10)
                    || cartesian.setIfEmpty(x + 1, y, z, id, 10)
                    || cartesian.setIfEmpty(x, y, z + 1, id, 10)
                    || cartesian.setIfEmpty(x - 1, y, z - 1, id, 10)
                    || cartesian.setIfEmpty(x - 1, y, z + 1, id, 10)
                    || cartesian.setIfEmpty(x + 1, y, z - 1, id, 10)
                    || cartesian.setIfEmpty(x + 1, y, z + 1, id, 10)
                ) cartesian[x, y, z] = airId
            }
        }
        Block.byKey("minecraft:sand")?.apply {
            onUpdate = { cartesian, x, y, z, state ->
                val id = state.id
                if (
                    cartesian.setIfEmpty(x, y - 1, z, id, 1)
                    || cartesian.setIfEmpty(x - 1, y - 1, z, id, 1)
                    || cartesian.setIfEmpty(x, y - 1, z - 1, id, 1)
                    || cartesian.setIfEmpty(x + 1, y - 1, z, id, 1)
                    || cartesian.setIfEmpty(x, y - 1, z + 1, id, 1)
                    || cartesian.setIfEmpty(x - 1, y - 1, z - 1, id, 1)
                    || cartesian.setIfEmpty(x - 1, y - 1, z + 1, id, 1)
                    || cartesian.setIfEmpty(x + 1, y - 1, z - 1, id, 1)
                    || cartesian.setIfEmpty(x + 1, y - 1, z + 1, id, 1)
                ) cartesian[x, y, z] = airId
            }
        }
    }
}
