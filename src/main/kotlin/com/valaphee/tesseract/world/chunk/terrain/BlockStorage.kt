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

import com.valaphee.tesseract.data.block.BlockState

/**
 * @author Kevin Ludwig
 */
class BlockStorage : ReadWriteCartesian {
    var sections: Array<Section> = Array(SectionCount) { SectionCompact(BitArray.Version.V1) }

    override operator fun get(x: Int, y: Int, z: Int) = if (x in 0 until XZSize && y in 0 until SectionCount * Section.YSize && z in 0 until XZSize) sections[y shr YShift].get(x, y and SectionMask, z) else airId

    operator fun get(x: Int, y: Int, z: Int, layer: Int) = if (x in 0 until XZSize && y in 0 until SectionCount * Section.YSize && z in 0 until XZSize) sections[y shr YShift].get(x, y and SectionMask, z, layer) else airId

    override operator fun set(x: Int, y: Int, z: Int, value: Int) {
        if (!(x in 0 until XZSize && y in 0 until SectionCount * Section.YSize && z in 0 until XZSize)) return

        sections[y shr YShift].set(x, y and SectionMask, z, value)
    }

    operator fun set(x: Int, y: Int, z: Int, value: Int, layer: Int) {
        if (!(x in 0 until XZSize && y in 0 until SectionCount * Section.YSize && z in 0 until XZSize)) return

        sections[y shr YShift].set(x, y and SectionMask, z, value, layer)
    }

    companion object {
        private val airId = BlockState.byKeyWithStates("minecraft:air").id
        const val XZSize = 16
        const val XZMask = XZSize - 1
        const val SectionCount = 16
        const val SectionMask = SectionCount - 1
        const val YShift = 4
    }
}
