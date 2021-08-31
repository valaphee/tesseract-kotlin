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

import com.valaphee.tesseract.world.chunk.terrain.BlockStorage
import com.valaphee.tesseract.world.chunk.terrain.BlockUpdateList
import com.valaphee.tesseract.world.chunk.terrain.Section
import com.valaphee.tesseract.world.chunk.terrain.encodePosition
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import kotlin.math.min
import kotlin.random.Random

/**
 * @author Kevin Ludwig
 */
object Blocks {
    fun populate() {
        val flowingMoves = intArrayOf(
            -1,  0,  0,
             0,  0, -1,
             1,  0,  0,
             0,  0,  1,
            -1,  0, -1,
            -1,  0,  1,
             1,  0, -1,
             1,  0,  1,
        )
        fun flowingMovement(masses: Map<Int, Int>, viscosity: Int): OnUpdate {
            val maximumMass = masses.size
            return { updateList, x, y, z, state ->
                var mass = masses[state.id]!! + 1

                val fallingState = updateList[x, y - 1, z]
                (if (fallingState == airId) -1 else masses[fallingState])?.let {
                    val newMass = min((it + 1) + mass, maximumMass)
                    mass -= newMass - (it + 1)
                    updateList[x, y - 1, z, viscosity] = masses.entries.elementAt(maximumMass - newMass).key
                }

                val flowingMasses = Int2IntOpenHashMap(/*liquidMoves / 3*/8)
                repeat(/*liquidMoves / 3*/8) {
                    val offset = it * 3
                    val x = x + flowingMoves[offset]
                    val y = y + flowingMoves[offset + 1]
                    val z = z + flowingMoves[offset + 2]
                    if (x < BlockStorage.XZSize && y < BlockStorage.SectionCount * Section.YSize && z < BlockStorage.XZSize) {
                        val flowingState = updateList[x, y, z]
                        if (flowingState == airId) flowingMasses[offset] = 0
                        else masses[flowingState]?.let { if (it < mass) flowingMasses[offset] = it + 1 }
                    }
                }
                while (flowingMasses.isNotEmpty() && mass != 0) {
                    val flowingMass = flowingMasses.int2IntEntrySet().elementAt(Random.nextInt(flowingMasses.size))
                    val offset = flowingMass.intKey
                    val newMass = flowingMass.intValue + 1
                    if (mass <= newMass) {
                        flowingMasses.remove(offset)
                    } else {
                        mass--
                        flowingMass.setValue(newMass)

                        updateList[x + flowingMoves[offset], y + flowingMoves[offset + 1], z + flowingMoves[offset + 2], viscosity] = masses.entries.elementAt(maximumMass - newMass).key
                    }
                }

                if (mass == 0) updateList[x, y, z, 0] = airId
                else updateList[x, y, z, if (flowingMasses.isEmpty()) 0 else viscosity] = masses.entries.elementAt(maximumMass - mass).key
            }
        }
        Block.byKeyOrNull("minecraft:flowing_water")?.apply {
            onUpdate = flowingMovement(BlockState.byKey(key).associate { it.id to 7 - it.properties["liquid_depth"] as Int }.filterValues { it >= 0 }, 2)
        }
        Block.byKeyOrNull("minecraft:flowing_lava")?.apply {
            onUpdate = flowingMovement(BlockState.byKey(key).associate { it.id to 7 - it.properties["liquid_depth"] as Int }.filterValues { it >= 0 }, 4)
        }

        val fallingMoves = intArrayOf(
            -1, -1,  0,
             0, -1, -1,
             1, -1,  0,
             0, -1,  1,
            -1, -1, -1,
            -1, -1,  1,
             1, -1, -1,
             1, -1,  1,
        )
        val fallingMovement: OnUpdate = { updateList, x, y, z, state ->
            val id = state.id
            if (!updateList.setIfAir(x, y - 1, z, id, 0)) {
                val entropy = (0 until 4).shuffled()
                for (i in 0 until 8 step 4) {
                    if (entropy.find { j ->
                            val offset = ((i + j) * 3)
                            updateList.setIfAir(x + fallingMoves[offset], y + fallingMoves[offset + 1], z + fallingMoves[offset + 2], id, 0)
                        } != null) {
                        updateList[x, y, z] = airId
                        break
                    }
                }
            } else updateList[x, y, z] = airId
        }
        Block.byKeyOrNull("minecraft:sand")?.apply {
            onUpdate = fallingMovement
        }
        Block.byKeyOrNull("minecraft:gravel")?.apply {
            onUpdate = fallingMovement
        }
    }
}

private val airId = BlockState.byKeyWithStates("minecraft:air").id

private fun BlockUpdateList.setIfAir(x: Int, y: Int, z: Int, value: Int, updatesIn: Int = 0): Boolean {
    if (!(x in 0 until BlockStorage.XZSize && y in 0 until BlockStorage.SectionCount * Section.YSize && z in 0 until BlockStorage.XZSize)) return false

    if (get(x, y, z) == airId) {
        val position = encodePosition(x, y, z)
        changes[position] = value
        if (updatesIn != -1) pending[position] = updatesIn

        return true
    }

    return false
}
