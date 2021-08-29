/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain.block

import com.valaphee.tesseract.util.nbt.CompoundTag
import com.valaphee.tesseract.world.chunk.terrain.BlockUpdateList

/**
 * @author Kevin Ludwig
 */
class Block(
    val key: String,
    val component: CompoundTag? = null
) {
    var onUpdate: OnUpdate? = null

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
        private val byKey = mutableMapOf<String, Block>()
        private var finished = false

        fun finish() {
            check(!finished) { "Already finished" }

            finished = true
            BlockState.all.groupBy { it.key }.forEach { (key, states) ->
                val block = Block(key).also { byKey[key] = it }
                states.forEach { it.block = block }
            }
        }

        fun byKey(key: String) = byKey[key]

        val all get() = byKey.values
    }
}

typealias OnUpdate = (BlockUpdateList, Int, Int, Int, BlockState) -> Unit
