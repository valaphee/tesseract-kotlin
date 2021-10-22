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

package com.valaphee.tesseract.world.chunk.storage

import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.util.getCompoundTag
import com.valaphee.tesseract.util.getString
import com.valaphee.tesseract.util.nbt.TagType
import com.valaphee.tesseract.util.nbt.compoundTag
import com.valaphee.tesseract.util.nbt.ofBool
import com.valaphee.tesseract.util.nbt.ofInt
import com.valaphee.tesseract.util.nbt.ofString
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList

/**
 * @author Kevin Ludwig
 */
class Layer(
    var palette: IntList,
    var bitArray: BitArray,
) {
    constructor(default: Int, version: BitArray.Version) : this(IntArrayList(16).apply { add(default) }, version.bitArray(BlockStorage.XZSize * SubChunk.YSize * BlockStorage.XZSize))

    operator fun get(index: Int) = palette.getInt(bitArray[index])

    operator fun set(index: Int, value: Int) {
        var paletteIndex = palette.indexOf(value)
        if (paletteIndex == -1) {
            paletteIndex = palette.size
            palette.add(value)
            val blocksVersion = bitArray.version
            if (paletteIndex > blocksVersion.maximumEntryValue) {
                blocksVersion.next?.let {
                    val newBlockStorage = it.bitArray(BlockStorage.XZSize * SubChunk.YSize * BlockStorage.XZSize)
                    repeat(BlockStorage.XZSize * SubChunk.YSize * BlockStorage.XZSize) { newBlockStorage[it] = bitArray[it] }
                    bitArray = newBlockStorage
                }
            }
        }
        bitArray[index] = paletteIndex
    }

    val empty get() = bitArray.empty

    fun writeToBuffer(buffer: PacketBuffer, runtime: Boolean) {
        buffer.writeByte((bitArray.version.bitsPerEntry shl 1) or if (runtime) 1 else 0)
        bitArray.data.forEach { buffer.writeIntLE(it) }
        buffer.writeVarInt(palette.size)
        if (runtime) palette.forEach { buffer.writeVarInt(it) } else buffer.toNbtOutputStream().use { stream ->
            palette.forEach {
                stream.writeTag(compoundTag().apply {
                    val keyWithProperties = checkNotNull(buffer.blockStates[it])
                    val propertiesBegin = keyWithProperties.indexOf('[')
                    val propertiesEnd = keyWithProperties.indexOf(']')
                    if (propertiesBegin == -1 && propertiesEnd == -1) {
                        setString("name", keyWithProperties)
                        set("states", compoundTag())
                    } else if (propertiesEnd == keyWithProperties.length - 1) {
                        val propertiesTag = compoundTag()
                        keyWithProperties.substring(propertiesBegin + 1, propertiesEnd).split(',').forEach {
                            val property = it.split('=', limit = 2)
                            propertiesTag[property[0]] = when (val propertyValue = property[1]) {
                                "false" -> ofBool(false)
                                "true" -> ofBool(true)
                                else -> propertyValue.toIntOrNull()?.let { ofInt(it) } ?: ofString(propertyValue)
                            }
                        }
                        setString("name", keyWithProperties.substring(0, propertiesBegin))
                        set("states", propertiesTag)
                    }
                })
            }
        }
    }
}

fun PacketBuffer.readLayer(default: Int): Layer {
    val header = readByte().toInt()
    val version = BitArray.Version.byBitsPerEntry(header shr 1)
    val runtime = header and 1 == 1
    val blocks = version.bitArray(BlockStorage.XZSize * SubChunk.YSize * BlockStorage.XZSize, IntArray(version.bitArrayDataSize(BlockStorage.XZSize * SubChunk.YSize * BlockStorage.XZSize)) { readIntLE() })
    val paletteSize = readVarInt()
    return Layer(IntArrayList().apply {
        if (runtime) repeat(paletteSize) { add(readVarInt()) } else {
            toNbtInputStream().use { stream ->
                repeat(paletteSize) {
                    add(stream.readTag()?.asCompoundTag()?.let {
                        blockStates.getId(StringBuilder().apply {
                            append(it.getString("name"))
                            val properties = it.getCompoundTag("states").toMap().mapValues {
                                when (it.value.type) {
                                    TagType.Byte -> it.value.asNumberTag()!!.toByte() != 0.toByte()
                                    TagType.Int -> it.value.asNumberTag()!!.toInt()
                                    TagType.String -> it.value.asArrayTag()!!.valueToString()
                                    else -> TODO()
                                }
                            }
                            if (properties.isNotEmpty()) {
                                append('[')
                                properties.forEach { (name, value) -> append(name).append('=').append(value).append(',') }
                                setCharAt(length - 1, ']')
                            }
                        }.toString())
                    } ?: default)
                }
            }
        }
    }, blocks)
}
