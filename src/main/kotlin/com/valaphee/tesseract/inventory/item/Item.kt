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

package com.valaphee.tesseract.inventory.item

import com.valaphee.tesseract.inventory.item.stack.meta.Meta
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
    var item: com.valaphee.tesseract.data.item.Item? = null

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

        fun byId(id: Int) = checkNotNull(byId[id])

        fun byIdOrNull(id: Int): Item<*>? = byId[id]

        fun byKey(key: String) = checkNotNull(byKey[key])

        fun byKeyOrNull(key: String) = byKey[key]

        val all get() = byId.values

        var default: Item<*>
            get() = byId.defaultReturnValue()
            set(value) = byId.defaultReturnValue(value)
    }
}
