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

package com.valaphee.tesseract.data.block

import com.valaphee.tesseract.world.chunk.terrain.PropagationBlockUpdateList
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import kotlin.math.min
import kotlin.random.Random

/**
 * @author Kevin Ludwig
 */
abstract class FlowingBlock(
    private val viscosity: Int
) : Block {
    private val masses = BlockState.byKey(key).associate { it.id to 7 - it.properties["liquid_depth"] as Int }.filterValues { it >= 0 }

    override val transparent get() = true

    override fun onUpdate(blockUpdates: PropagationBlockUpdateList, x: Int, y: Int, z: Int, blockState: BlockState) {
        var mass = masses[blockState.id]!! + 1

        val fallingState = blockUpdates[x, y - 1, z]
        (if (fallingState == Blocks.airId) -1 else masses[fallingState])?.let {
            val newMass = min((it + 1) + mass, masses.size)
            mass -= newMass - (it + 1)
            blockUpdates[x, y - 1, z, viscosity] = masses.entries.elementAt(masses.size - newMass).key
        }

        val flowingMasses = Int2IntOpenHashMap(/*liquidMoves / 3*/8)
        repeat(/*liquidMoves / 3*/8) {
            val offset = it * 3
            val flowingState = blockUpdates[x + flowingMoves[offset], y + flowingMoves[offset + 1], z + flowingMoves[offset + 2]]
            if (flowingState == Blocks.airId) flowingMasses[offset] = 0
            else masses[flowingState]?.let { if (it < mass) flowingMasses[offset] = it + 1 }
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

                blockUpdates[x + flowingMoves[offset], y + flowingMoves[offset + 1], z + flowingMoves[offset + 2], viscosity] = masses.entries.elementAt(masses.size - newMass).key
            }
        }

        if (mass == 0) blockUpdates[x, y, z, 0] = Blocks.airId
        else blockUpdates[x, y, z, if (flowingMasses.isEmpty()) 0 else viscosity] = masses.entries.elementAt(masses.size - mass).key
    }

    companion object {
        private val flowingMoves = intArrayOf(
            -1,  0,  0,
             0,  0, -1,
             1,  0,  0,
             0,  0,  1,
            -1,  0, -1,
            -1,  0,  1,
             1,  0, -1,
             1,  0,  1,
        )
    }
}
