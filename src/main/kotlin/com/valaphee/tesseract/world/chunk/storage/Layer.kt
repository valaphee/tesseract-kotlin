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
 *
 */

package com.valaphee.tesseract.world.chunk.storage

import com.valaphee.tesseract.net.PacketBuffer
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList

/**
 * @author Kevin Ludwig
 */
class Layer(
    var palette: IntList,
    var bitArray: BitArray,
) {
    constructor(default: Int, version: BitArray.Version) : this(IntArrayList(16).apply { add(default) }, version.bitArray(BlockStorage.XZSize * Section.YSize * BlockStorage.XZSize))

    fun get(index: Int): Int = palette.getInt(bitArray[index])

    fun set(index: Int, id: Int) {
        var value = palette.indexOf(id)
        if (value == -1) {
            value = palette.size
            palette.add(id)
            val blocksVersion = bitArray.version
            if (value > blocksVersion.maximumEntryValue) {
                blocksVersion.next?.let {
                    val newBlockStorage = it.bitArray(BlockStorage.XZSize * Section.YSize * BlockStorage.XZSize)
                    repeat(BlockStorage.XZSize * Section.YSize * BlockStorage.XZSize) { newBlockStorage[it] = bitArray[it] }
                    bitArray = newBlockStorage
                }
            }
        }
        bitArray[index] = value
    }

    val empty get() = bitArray.empty

    fun writeToBuffer(buffer: PacketBuffer, runtime: Boolean) {
        buffer.writeByte((bitArray.version.bitsPerEntry shl 1) or if (runtime) 1 else 0)
        bitArray.data.forEach { buffer.writeIntLE(it) }
        buffer.writeVarInt(palette.size)
        if (runtime) palette.forEach { buffer.writeVarInt(it) }/* else buffer.toNbtOutputStream().use { stream ->
            palette.forEach {
                stream.writeTag(compoundTag().apply {
                    val block = BlockState.byId(it)
                    setString("name", block.key)
                    set("states", block.propertiesNbt)
                })
            }
        } TODO*/
    }
}

fun PacketBuffer.readLayer(default: Int): Layer {
    val header = readByte().toInt()
    val version = BitArray.Version.byBitsPerEntry(header shr 1)
    val runtime = header and 1 == 1
    val blocks = version.bitArray(BlockStorage.XZSize * Section.YSize * BlockStorage.XZSize, IntArray(version.bitArrayDataSize(BlockStorage.XZSize * Section.YSize * BlockStorage.XZSize)) { readIntLE() })
    val paletteSize = readVarInt()
    return Layer(IntArrayList().apply {
        if (runtime) repeat(paletteSize) { add(readVarInt()) }/* else {
            toNbtInputStream().use { stream ->
                repeat(paletteSize) {
                    add(stream.readTag()?.asCompoundTag()?.let {
                        val propertiesNbt = it.getCompoundTag("states")
                        BlockState.byKey(it.getString("name")).find { it.propertiesNbt == propertiesNbt }?.id
                    } ?: default)
                }
            }
        } TODO*/
    }, blocks)
}
