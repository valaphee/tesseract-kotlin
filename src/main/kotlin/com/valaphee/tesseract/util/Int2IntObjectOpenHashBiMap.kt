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

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap

/**
 * @author Kevin Ludwig
 */
class Int2ObjectOpenHashBiMap<V> : Int2ObjectOpenHashMap<V>, Cloneable {
    var reverse: Object2IntOpenHashMap<V>
        private set

    constructor() {
        reverse = Object2IntOpenHashMap<V>().apply { defaultReturnValue(-1) }
    }

    constructor(initialCapacity: Int) : super(initialCapacity) {
        reverse = Object2IntOpenHashMap<V>(initialCapacity).apply { defaultReturnValue(-1) }
    }

    constructor(noEntryValue: V) {
        defaultReturnValue(noEntryValue)
        reverse = Object2IntOpenHashMap<V>().apply { defaultReturnValue(-1) }
    }

    constructor(initialCapacity: Int, noEntryValue: V) : super(initialCapacity) {
        defaultReturnValue(noEntryValue)
        reverse = Object2IntOpenHashMap<V>(initialCapacity).apply { defaultReturnValue(-1) }
    }

    fun getKey(value: Any) = reverse.getInt(value)

    override fun put(key: Int, value: V): V? {
        reverse[value] = key
        return super.put(key, value)
    }

    override fun remove(key: Int) = super.remove(key)?.also { reverse.removeInt(it) }

    override fun putIfAbsent(key: Int, value: V): V? {
        reverse.putIfAbsent(value, key)
        return super.putIfAbsent(key, value)
    }

    override fun clear() {
        super.clear()
        reverse.clear()
    }

    override fun clone() = (super<Int2ObjectOpenHashMap>.clone() as Int2ObjectOpenHashBiMap).apply { reverse = this@Int2ObjectOpenHashBiMap.reverse.clone() }
}
