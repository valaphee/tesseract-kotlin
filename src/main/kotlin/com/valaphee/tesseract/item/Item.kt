/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.item

import com.valaphee.tesseract.item.meta.Meta
import com.valaphee.tesseract.util.nbt.CompoundTag
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap

/**
 * @author Kevin Ludwig
 */
class Item<T : Meta> constructor(
    val key: String,
    val component: CompoundTag? = null,
    val meta: () -> T
) {
    var id = 0

    init {
        register(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Item<*>

        if (id != other.id) return false

        return true
    }

    override fun hashCode() = id

    override fun toString() = key

    companion object {
        private val byId = Int2ObjectOpenHashMap<Item<*>>()
        private val byKey = mutableMapOf<String, Item<*>>()

        fun register(value: Item<*>) {
            byKey[value.key] = value
        }

        fun register(key: String, id: Int) {
            byId[id] = byKey.getOrPut(key) { Item(key, null, ::Meta) }.apply { this.id = id }
        }

        fun byId(id: Int): Item<*>? = byId[id]

        fun byKey(key: String) = byKey[key]

        val all get() = byId.values

        var default: Item<*>
            get() = byId.defaultReturnValue()
            set(value) = byId.defaultReturnValue(value)
    }
}
