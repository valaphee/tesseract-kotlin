/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain.block

/**
 * @author Kevin Ludwig
 */
class BlockState(
    val key: String,
    val properties: Map<String, Any>,
    val version: Int
) {
    var runtimeId = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BlockState

        if (runtimeId != other.runtimeId) return false

        return true
    }

    override fun hashCode() = runtimeId

    override fun toString() = StringBuilder().apply {
        append(key)
        if (properties.isNotEmpty()) {
            append('[')
            properties.forEach { (name, value) -> append(name).append('=').append(value).append(',') }
            setCharAt(length - 1, ']')
        }
    }.toString()

    companion object {
        private val values: MutableList<BlockState> = ArrayList()
        private var finished = false

        fun register(value: BlockState) {
            check(!finished) { "Already finished" }
            values += value
        }

        fun finish() {
            check(!finished) { "Already finished" }

            finished = true
            values.sortWith(compareBy { it.key.split(":", limit = 2)[1].lowercase() })
            values.forEachIndexed { i, it -> it.runtimeId = i }
        }

        fun byId(id: Int) = if (id < values.size) values[id] else null

        fun byKey(key: String) = values.stream().filter { key == it.key }

        fun byKeyWithStates(keyWithProperties: String): BlockState? {
            val propertiesBegin = keyWithProperties.indexOf('[')
            val propertiesEnd = keyWithProperties.indexOf(']')
            return if (propertiesBegin == -1 && propertiesEnd == -1) {
                byKey(keyWithProperties).findFirst().orElse(null)
            } else if (propertiesEnd == keyWithProperties.length - 1) {
                val properties = HashMap<String, Any>()
                keyWithProperties.substring(propertiesBegin + 1, propertiesEnd).split(',').forEach {
                    val property = it.split('=', limit = 2)
                    properties[property[0]] = when (val propertyValue = property[1]) {
                        "false" -> false
                        "true" -> true
                        else -> propertyValue.toIntOrNull() ?: propertyValue
                    }
                }
                byKey(keyWithProperties.substring(0, propertiesBegin)).filter { properties == it.properties }.findAny().orElse(null)
            } else null
        }

        val all get() = values
    }
}
