/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain

import com.valaphee.tesseract.world.chunk.terrain.block.BlockState

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
        private val airId = BlockState.byKeyWithStates("minecraft:air")?.id ?: error("Missing minecraft:air")

        const val XZSize = 16
        const val XZMask = XZSize - 1
        const val SectionCount = 16
        const val SectionMask = SectionCount - 1
        const val YShift = 4
    }
}
