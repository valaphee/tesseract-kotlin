/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain.blocks

import com.valaphee.tesseract.world.chunk.terrain.ReadWriteCartesian

/**
 * @author Kevin Ludwig
 */
class Blocks : ReadWriteCartesian {
    var sections: Array<Section> = Array(SectionCount) { SectionV8(BitArray.Version.V1) }

    override fun get(x: Int, y: Int, z: Int) = sections[y shr YShift].get(x, y and SectionMask, z)

    fun get(x: Int, y: Int, z: Int, layer: Int) = sections[y shr YShift].get(layer, x, y and SectionMask, z)

    override fun set(x: Int, y: Int, z: Int, value: Int) = sections[y shr YShift].set(x, y and SectionMask, z, value)

    fun set(x: Int, y: Int, z: Int, value: Int, layer: Int) = sections[y shr YShift].set(layer, x, y and SectionMask, z, value)

    override fun setIfEmpty(x: Int, y: Int, z: Int, value: Int): Int {
        if (!(x in 0 until XZSize && y in 0 until SectionCount * Section.YSize && z in 0 until XZSize)) return 0

        val oldValue = get(x, y, z)
        if (oldValue == 0) {
            set(x, y, z, value)

            return oldValue
        }

        return oldValue
    }

    companion object {
        const val XZSize = 16
        const val XZMask = XZSize - 1
        const val SectionCount = 16
        const val SectionMask = SectionCount - 1
        const val YShift = 4
    }
}
