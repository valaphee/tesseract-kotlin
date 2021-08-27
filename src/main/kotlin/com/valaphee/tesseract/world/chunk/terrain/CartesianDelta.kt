/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain

import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.Chunk
import com.valaphee.tesseract.world.chunk.terrain.blocks.Blocks
import com.valaphee.tesseract.world.chunk.terrain.blocks.Section
import it.unimi.dsi.fastutil.shorts.Short2IntOpenHashMap

/**
 * @author Kevin Ludwig
 */
class CartesianDelta(
    private val cartesian: ReadWriteCartesian
) : ReadWriteCartesian {
    private val changes = Short2IntOpenHashMap()

    override fun get(x: Int, y: Int, z: Int) = if (x in 0 until Blocks.XZSize && y in 0 until Blocks.SectionCount * Section.YSize && z in 0 until Blocks.XZSize) {
        val key = encodePosition(x, y, z)
        if (changes.containsKey(key)) changes.get(key) else cartesian[x, y, z]
    } else 0

    override fun set(x: Int, y: Int, z: Int, value: Int) {
        if (!(x in 0 until Blocks.XZSize && y in 0 until Blocks.SectionCount * Section.YSize && z in 0 until Blocks.XZSize)) return

        changes[encodePosition(x, y, z)] = value
    }

    override fun setIfEmpty(x: Int, y: Int, z: Int, value: Int): Int {
        if (!(x in 0 until Blocks.XZSize && y in 0 until Blocks.SectionCount * Section.YSize && z in 0 until Blocks.XZSize)) return 0

        val oldValue = get(x, y, z)
        if (oldValue == 0) {
            set(x, y, z, value)

            return oldValue
        }

        return oldValue
    }

    fun merge(context: WorldContext, sector: Chunk) {
        if (changes.isNotEmpty()) {
            sector.sendMessage(CartesianDeltaMerge(context, sector, changes.clone()))
            changes.clear()
        }
    }
}

fun encodePosition(x: Int, y: Int, z: Int) = ((x shl 12) or (z shl 8) or y).toShort()

fun decodePosition(position: Short): Int3 {
    val positionInt = position.toInt()
    return Int3((positionInt shr 12) and 0xF, positionInt and 0xFF, (positionInt shr 8) and 0xF)
}
