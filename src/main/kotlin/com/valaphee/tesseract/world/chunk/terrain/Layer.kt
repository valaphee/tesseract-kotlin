/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain

import com.valaphee.tesseract.nbt.compoundTag
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.util.getCompoundTag
import com.valaphee.tesseract.util.getString
import com.valaphee.tesseract.world.chunk.terrain.block.BlockState
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList

/**
 * @author Kevin Ludwig
 */
class Layer(
    var palette: IntList,
    var bitArray: BitArray,
    var runtime: Boolean
) {
    constructor(default: Int, version: BitArray.Version, runtime: Boolean) : this(IntArrayList(16).apply { add(default) }, version.bitArray(BlockStorage.XZSize * Section.YSize * BlockStorage.XZSize), runtime)

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

    fun writeToBuffer(buffer: PacketBuffer) {
        buffer.writeByte((bitArray.version.bitsPerEntry shl 1) or if (runtime) 1 else 0)
        bitArray.data.forEach { buffer.writeIntLE(it) }
        buffer.writeVarInt(palette.size)
        if (runtime) palette.forEach { buffer.writeVarInt(it) } else buffer.toNbtOutputStream().use { stream ->
            palette.forEach {
                stream.writeTag(compoundTag().apply {
                    val block = BlockState.byId(it)!!
                    setString("name", block.key)
                    set("states", block.propertiesNbt)
                })
            }
        }
    }
}

fun PacketBuffer.readLayer(default: Int): Layer {
    val header = readByte().toInt()
    val version = BitArray.Version.byBitsPerEntry(header shr 1)
    val runtime = header and 1 == 1
    val blocks = version.bitArray(BlockStorage.XZSize * Section.YSize * BlockStorage.XZSize, IntArray(version.bitArrayDataSize(BlockStorage.XZSize * Section.YSize * BlockStorage.XZSize)) { readIntLE() })
    val paletteSize = readVarInt()
    return Layer(IntArrayList().apply {
        if (runtime) repeat(paletteSize) { add(readVarInt()) } else {
            toNbtInputStream().use { stream ->
                repeat(paletteSize) {
                    add(stream.readTag()?.asCompoundTag()?.let { it ->
                        val propertiesNbt = it.getCompoundTag("states")
                        BlockState.byKey(it.getString("name")).filter { it.propertiesNbt == propertiesNbt }.findAny().orElseGet { null }?.id
                    } ?: default)
                }
            }
        }
    }, blocks, runtime)
}
