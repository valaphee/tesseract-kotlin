/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain.block

object Blocks {
    fun populate() {
        val airId = BlockState.byKeyWithStates("minecraft:air")?.id ?: error("Missing minecraft:air")
        val flowingMoves = intArrayOf(
             0, -1,  0,
            -1, -1,  0,
             0, -1, -1,
             1, -1,  0,
             0, -1,  1,
            -1, -1, -1,
            -1, -1,  1,
             1, -1, -1,
             1, -1,  1,
            -1,  0,  0,
             0,  0, -1,
             1,  0,  0,
             0,  0,  1,
            -1,  0, -1,
            -1,  0,  1,
             1,  0, -1,
             1,  0,  1
        )
        Block.byKey("minecraft:water")?.apply {
            onUpdate = { updateList, x, y, z, state ->
                val id = state.id
                if (!updateList.setIfEmpty(x + flowingMoves[0], y + flowingMoves[1], z + flowingMoves[2], id, 5)) {
                    val entropy = (0 until 4).shuffled()
                    for (i in 1 until 17 step 4) {
                        if (entropy.find { j ->
                            val offset = ((i + j) * 3)
                            updateList.setIfEmpty(x + flowingMoves[offset], y + flowingMoves[offset + 1], z + flowingMoves[offset + 2], id, 5)
                        } != null) {
                            updateList[x, y, z] = airId
                            break
                        }
                    }
                } else updateList[x, y, z] = airId
            }
        }
        Block.byKey("minecraft:lava")?.apply {
            onUpdate = { updateList, x, y, z, state ->
                val id = state.id
                if (!updateList.setIfEmpty(x + flowingMoves[0], y + flowingMoves[1], z + flowingMoves[2], id, 5)) {
                    val entropy = (0 until 4).shuffled()
                    for (i in 1 until 17 step 4) {
                        if (entropy.find { j ->
                                val offset = ((i + j) * 3)
                                updateList.setIfEmpty(x + flowingMoves[offset], y + flowingMoves[offset + 1], z + flowingMoves[offset + 2], id, 10)
                            } != null) {
                            updateList[x, y, z] = airId
                            break
                        }
                    }
                } else updateList[x, y, z] = airId
            }
        }
        val fallingMoves = intArrayOf(
            0, -1,  0,
            -1, -1,  0,
            0, -1, -1,
            1, -1,  0,
            0, -1,  1,
            -1, -1, -1,
            -1, -1,  1,
            1, -1, -1,
            1, -1,  1,
        )
        Block.byKey("minecraft:sand")?.apply {
            onUpdate = { updateList, x, y, z, state ->
                val id = state.id
                if (!updateList.setIfEmpty(x + fallingMoves[0], y + fallingMoves[1], z + fallingMoves[2], id, 5)) {
                    val entropy = (0 until 4).shuffled()
                    for (i in 1 until 9 step 4) {
                        if (entropy.find { j ->
                                val offset = ((i + j) * 3)
                                updateList.setIfEmpty(x + fallingMoves[offset], y + fallingMoves[offset + 1], z + fallingMoves[offset + 2], id, 1)
                            } != null) {
                            updateList[x, y, z] = airId
                            break
                        }
                    }
                } else updateList[x, y, z] = airId
            }
        }
        Block.byKey("minecraft:gravel")?.apply {
            onUpdate = { updateList, x, y, z, state ->
                val id = state.id
                if (!updateList.setIfEmpty(x + fallingMoves[0], y + fallingMoves[1], z + fallingMoves[2], id, 5)) {
                    val entropy = (0 until 4).shuffled()
                    for (i in 1 until 9 step 4) {
                        if (entropy.find { j ->
                                val offset = ((i + j) * 3)
                                updateList.setIfEmpty(x + fallingMoves[offset], y + fallingMoves[offset], z + fallingMoves[offset], id, 1)
                            } != null) {
                            updateList[x, y, z] = airId
                            break
                        }
                    }
                } else updateList[x, y, z] = airId
            }
        }
    }
}
