/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.terrain

import com.valaphee.foundry.util.encodeMorton
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.sector.Sector
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap

/**
 * @author Kevin Ludwig
 */
class CartesianDelta(
    private val cartesian: ReadWriteCartesian
) : ReadWriteCartesian {
    private val changes = Int2ObjectOpenHashMap<Int>()

    override fun get(x: Int, y: Int, z: Int) = if (x in 0 until MortonXyz && y in 0 until MortonXyz && z in 0 until MortonXyz) changes[encodeMorton(x, y, z)] ?: cartesian[x, y, z] else 0

    override fun set(x: Int, y: Int, z: Int, value: Int) {
        if (!(x in 0 until MortonXyz && y in 0 until MortonXyz && z in 0 until MortonXyz)) return

        changes[encodeMorton(x, y, z)] = value
    }

    override fun setIfEmpty(x: Int, y: Int, z: Int, value: Int): Int {
        if (!(x in 0 until MortonXyz && y in 0 until MortonXyz && z in 0 until MortonXyz)) return 0

        val oldValue = get(x, y, z)
        if (oldValue == 0) {
            set(x, y, z, value)

            return oldValue
        }

        return oldValue
    }

    fun merge(context: WorldContext, sector: Sector) {
        if (changes.isNotEmpty()) {
            sector.sendMessage(CartesianDeltaMerge(context, changes.clone()))
            changes.clear()
        }
    }
}
