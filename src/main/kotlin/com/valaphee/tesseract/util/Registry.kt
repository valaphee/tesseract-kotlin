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

package com.valaphee.tesseract.util

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap

/**
 * @author Kevin Ludwig
 */
class Registry<T> : Cloneable {
    var idToValue: Int2ObjectMap<T>
        private set
    var valueToId: Object2IntMap<T>
        private set

    var default: T
        get() = idToValue.defaultReturnValue()
        set(value) {
            idToValue.defaultReturnValue(value)
        }

    constructor() {
        idToValue = Int2ObjectOpenHashMap()
        valueToId = Object2IntOpenHashMap()
    }

    constructor(initialCapacity: Int) {
        idToValue = Int2ObjectOpenHashMap(initialCapacity)
        valueToId = Object2IntOpenHashMap(initialCapacity)
    }

    constructor(noEntryValue: T) {
        idToValue = Int2ObjectOpenHashMap<T>().apply { defaultReturnValue(noEntryValue) }
        valueToId = Object2IntOpenHashMap()
    }

    constructor(initialCapacity: Int, noEntryValue: T) {
        idToValue = Int2ObjectOpenHashMap<T>(initialCapacity).apply { defaultReturnValue(noEntryValue) }
        valueToId = Object2IntOpenHashMap(initialCapacity)
    }

    operator fun set(id: Int, value: T) {
        idToValue[id] = value
        valueToId[value] = id
    }

    operator fun get(id: Int): T? = idToValue.get(id)

    fun getId(value: T) = valueToId.getInt(value)

    @Suppress("UNCHECKED_CAST")
    public override fun clone() = (super.clone() as Registry<T>).apply {
        idToValue = this@Registry.idToValue
        valueToId = this@Registry.valueToId
    }
}
