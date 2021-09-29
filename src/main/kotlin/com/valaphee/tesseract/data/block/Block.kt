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

package com.valaphee.tesseract.data.block

import com.valaphee.tesseract.data.Data
import com.valaphee.tesseract.data.KeyedData
import com.valaphee.tesseract.util.nbt.CompoundTag

/**
 * @author Kevin Ludwig
 */
abstract class Block(
    val properties: Map<String, Set<*>> = mapOf(),
    val component: CompoundTag? = null
) : Data, KeyedData {
    val states: List<BlockState>

    init {
        val states = mutableListOf<BlockState>()
        properties.values.fold(listOf(listOf<Any?>())) { acc, set -> acc.flatMap { list -> set.map { list + it } } }.forEach { states.add(BlockState(this, properties.keys.zip(it).toMap())) }
        this.states = states
    }
}

fun Map<String, @JvmSuppressWildcards Block>.byKeyWithStates(keyWithProperties: String) = checkNotNull(byKeyWithStatesOrNull(keyWithProperties))

fun Map<String, @JvmSuppressWildcards Block>.byKeyWithStatesOrNull(keyWithProperties: String): BlockState? {
    val propertiesBegin = keyWithProperties.indexOf('[')
    val propertiesEnd = keyWithProperties.indexOf(']')
    return if (propertiesBegin == -1 && propertiesEnd == -1) {
        this[keyWithProperties]?.states?.firstOrNull()
    } else if (propertiesEnd == keyWithProperties.length - 1) {
        val properties = mutableMapOf<String, Any>()
        keyWithProperties.substring(propertiesBegin + 1, propertiesEnd).split(',').forEach {
            val property = it.split('=', limit = 2)
            properties[property[0]] = when (val propertyValue = property[1]) {
                "false" -> false
                "true" -> true
                else -> propertyValue.toIntOrNull() ?: propertyValue
            }
        }
        this[keyWithProperties]?.states?.find { properties == it.properties }
    } else null
}
