/*
 * Copyright (c) 2021, GrieferGames, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.terrain.block

import com.valaphee.tesseract.nbt.CompoundTag

/**
 * @author Kevin Ludwig
 */
class Block(
    val key: String,
    val component: CompoundTag? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Block

        if (key != other.key) return false

        return true
    }

    override fun hashCode() = key.hashCode()

    override fun toString() = key

    companion object {
        private val byKey = HashMap<String, Block>()
        private var finished = false

        fun finish() {
            check(!finished) { "Already finished" }

            finished = true
            HashSet<String>().apply { BlockState.all.forEach { add(it.key) } }.forEach { byKey[it] = Block(it) }
        }

        fun byKey(key: String) = byKey[key]

        val all get() = byKey.values
    }
}
