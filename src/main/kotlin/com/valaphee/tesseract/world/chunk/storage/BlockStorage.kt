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
 *
 */

package com.valaphee.tesseract.world.chunk.storage

import com.valaphee.tesseract.world.chunk.ReadWriteBlockAccess

/**
 * @author Kevin Ludwig
 */
class BlockStorage(
    val default: Int,
    val sections: Array<Section>
) : ReadWriteBlockAccess {
    private val sectionCount get() = sections.size


    constructor(default: Int, sectionCount: Int = 16) : this(default, Array(sectionCount) { SectionCompact(default, BitArray.Version.V1) })

    override operator fun get(x: Int, y: Int, z: Int) = if (x in 0 until XZSize && y in 0 until sectionCount * Section.YSize && z in 0 until XZSize) sections[y shr YShift].get(x, y and YMask, z) else default

    operator fun get(x: Int, y: Int, z: Int, layer: Int) = if (x in 0 until XZSize && y in 0 until sectionCount * Section.YSize && z in 0 until XZSize) sections[y shr YShift].get(x, y and YMask, z, layer) else default

    override operator fun set(x: Int, y: Int, z: Int, value: Int) {
        if (!(x in 0 until XZSize && y in 0 until sectionCount * Section.YSize && z in 0 until XZSize)) return

        sections[y shr YShift].set(x, y and YMask, z, value)
    }

    operator fun set(x: Int, y: Int, z: Int, value: Int, layer: Int) {
        if (!(x in 0 until XZSize && y in 0 until sectionCount * Section.YSize && z in 0 until XZSize)) return

        sections[y shr YShift].set(x, y and YMask, z, value, layer)
    }

    companion object {
        const val XZSize = 16
        const val YShift = 4
        const val YMask = 15
    }
}
