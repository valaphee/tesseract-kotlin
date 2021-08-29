/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain

import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.world.chunk.terrain.block.BlockState
import it.unimi.dsi.fastutil.shorts.Short2IntOpenHashMap

/**
 * @author Kevin Ludwig
 */
class BlockUpdateList(
    private val cartesian: ReadWriteCartesian
) : ReadWriteCartesian {
    val changes = Short2IntOpenHashMap()
    val pending = Short2IntOpenHashMap()

    override fun get(x: Int, y: Int, z: Int) = if (x < BlockStorage.XZSize && y < BlockStorage.SectionCount * Section.YSize && z < BlockStorage.XZSize) {
        val key = encodePosition(x, y, z)
        if (changes.containsKey(key)) changes.get(key) else cartesian[x, y, z]
    } else airId

    override fun set(x: Int, y: Int, z: Int, value: Int) = set(x, y, z, 1, value)

    operator fun set(x: Int, y: Int, z: Int, updatesIn: Int, value: Int) {
        if (x >= BlockStorage.XZSize || y >= BlockStorage.SectionCount * Section.YSize && z >= BlockStorage.XZSize) return
        if (cartesian[x, y, z] != value) {
            val position = encodePosition(x, y, z)
            changes[position] = value
            if (updatesIn != 0) pending[position] = updatesIn
        }
    }

    companion object {
        private val airId = BlockState.byKeyWithStates("minecraft:air")?.id ?: error("Missing minecraft:air")
    }
}

fun encodePosition(x: Int, y: Int, z: Int) = ((x shl 12) or (z shl 8) or y).toShort()

fun decodePosition(position: Short): Int3 {
    val positionInt = position.toInt()
    return Int3((positionInt shr 12) and 0xF, positionInt and 0xFF, (positionInt shr 8) and 0xF)
}
