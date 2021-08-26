/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world

import com.valaphee.tesseract.net.PacketBuffer

/**
 * @author Kevin Ludwig
 */
data class GameRule<T>(
    var name: String,
    var editable: Boolean,
    var value: T
)

fun PacketBuffer.readGameRulePre440(): GameRule<*> {
    val name = readString()
    return when (readVarUInt()) {
        1 -> GameRule(name, true, readBoolean())
        2 -> GameRule(name, true, readVarUInt())
        3 -> GameRule(name, true, readFloatLE())
        else -> TODO()
    }
}

fun PacketBuffer.readGameRule(): GameRule<*> {
    val name = readString()
    val editable = readBoolean()
    return when (readVarUInt()) {
        1 -> GameRule(name, editable, readBoolean())
        2 -> GameRule(name, editable, readVarUInt())
        3 -> GameRule(name, editable, readFloatLE())
        else -> TODO()
    }
}

fun PacketBuffer.writeGameRulePre440(value: GameRule<*>) {
    writeString(value.name)
    when (value.value) {
        is Boolean -> {
            writeVarUInt(1)
            writeBoolean(value.value as Boolean)
        }
        is Int -> {
            writeVarUInt(2)
            writeVarUInt(value.value as Int)
        }
        is Float -> {
            writeVarUInt(3)
            writeFloatLE(value.value as Float)
        }
        else -> TODO()
    }
}

fun PacketBuffer.writeGameRule(value: GameRule<*>) {
    writeString(value.name)
    writeBoolean(value.editable)
    when (value.value) {
        is Boolean -> {
            writeVarUInt(1)
            writeBoolean(value.value as Boolean)
        }
        is Int -> {
            writeVarUInt(2)
            writeVarUInt(value.value as Int)
        }
        is Float -> {
            writeVarUInt(3)
            writeFloatLE(value.value as Float)
        }
        else -> TODO()
    }
}
