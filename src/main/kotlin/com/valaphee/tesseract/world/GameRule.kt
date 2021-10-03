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

package com.valaphee.tesseract.world

import com.valaphee.tesseract.net.PacketBuffer

/**
 * @author Kevin Ludwig
 */
class GameRule<T>(
    val name: String,
    val editable: Boolean,
    val value: T
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
            writeBoolean(value.value)
        }
        is Int -> {
            writeVarUInt(2)
            writeVarUInt(value.value)
        }
        is Float -> {
            writeVarUInt(3)
            writeFloatLE(value.value)
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
            writeBoolean(value.value)
        }
        is Int -> {
            writeVarUInt(2)
            writeVarUInt(value.value)
        }
        is Float -> {
            writeVarUInt(3)
            writeFloatLE(value.value)
        }
        else -> TODO()
    }
}
