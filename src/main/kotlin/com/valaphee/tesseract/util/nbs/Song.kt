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

package com.valaphee.tesseract.util.nbs

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.DataInputStream

/**
 * @author Kevin Ludwig
 */
data class Song(
    val title: String,
    val author: String,
    val length: Int,
    val speed: Float,
    val layers: Int2ObjectMap<Layer>
) {
    val delay = 20 / speed
}

fun DataInputStream.readSong(): Song {
    val length = readShortLE()
    val layerCount = readShortLE()
    val name = readString()
    val author = readString()
    readString()
    readString()
    val speed = readShortLE() / 100.0f
    readBoolean()
    readByte()
    readByte()
    readIntLE()
    readIntLE()
    readIntLE()
    readIntLE()
    readIntLE()
    readString()
    val layers = Int2ObjectOpenHashMap<Layer>(layerCount)
    var tick = -1
    while (true) {
        val jumpTicks = readShortLE()
        if (jumpTicks == 0) break
        tick += jumpTicks
        var layer = -1
        while (true) {
            val jumpLayers = readShortLE()
            if (jumpLayers == 0) break
            layer += jumpLayers
            layers.computeIfAbsent(layer, Int2ObjectFunction { Layer("", 1.0f, Int2ObjectOpenHashMap()) }).notes[tick] = Note(readUnsignedByte(), readUnsignedByte())
        }
    }
    repeat(layerCount) {
        val name = readString()
        val volume = readByte() / 100.0f
        layers[it]?.let {
            it.name = name
            it.volume = volume
        }
    }
    return Song(name, author, length, speed, layers)
}

private fun DataInputStream.readShortLE() = readUnsignedByte() + (readUnsignedByte() shl 8)

private fun DataInputStream.readIntLE() = readUnsignedByte() + (readUnsignedByte() shl 8) + (readUnsignedByte() shl 16) + (readUnsignedByte() shl 24)

private fun DataInputStream.readString(): String {
    val length = readIntLE()
    val stringBuilder = StringBuilder(length)
    repeat(length) {
        var character = readByte().toChar()
        if (character == 0x0D.toChar()) character = ' '
        stringBuilder.append(character)
    }
    return stringBuilder.toString()
}
