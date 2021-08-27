/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain.blocks

import com.valaphee.tesseract.nbt.NbtInputStream
import com.valaphee.tesseract.nbt.NbtOutputStream
import com.valaphee.tesseract.nbt.TagType
import com.valaphee.tesseract.nbt.compoundTag
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.util.LittleEndianByteBufInputStream
import com.valaphee.tesseract.util.LittleEndianByteBufOutputStream
import com.valaphee.tesseract.util.getCompoundTagOrNull
import com.valaphee.tesseract.util.getString
import com.valaphee.tesseract.world.chunk.terrain.block.BlockState
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList

/**
 * @author Kevin Ludwig
 */
class Layer(
    var blockPalette: IntList,
    var blocks: BitArray,
    var runtime: Boolean
) {
    constructor(version: BitArray.Version, runtime: Boolean) : this(IntArrayList(16).apply { add(airId) }, version.bitArray(BlockStorage.XZSize * Section.YSize * BlockStorage.XZSize), runtime)

    fun get(index: Int): Int = blockPalette.getInt(blocks[index])

    fun set(index: Int, id: Int) {
        var paletteId = blockPalette.indexOf(id)
        if (paletteId == -1) {
            paletteId = blockPalette.size
            blockPalette.add(id)
            val blocksVersion = blocks.version
            if (paletteId > blocksVersion.maximumEntryValue) {
                blocksVersion.next?.let {
                    val newBlockStorage = it.bitArray(BlockStorage.XZSize * Section.YSize * BlockStorage.XZSize)
                    repeat(BlockStorage.XZSize * Section.YSize * BlockStorage.XZSize) { newBlockStorage[it] = blocks[it] }
                    blocks = newBlockStorage
                }
            }
        }
        blocks[index] = paletteId
    }

    val empty get() = blocks.empty

    fun writeToBuffer(buffer: PacketBuffer) {
        buffer.writeByte((blocks.version.bitsPerEntry shl 1) or if (runtime) 1 else 0)
        blocks.data.forEach { buffer.writeIntLE(it) }
        if (runtime) {
            buffer.writeVarInt(blockPalette.size)
            blockPalette.forEach { buffer.writeVarInt(it) }
        } else {
            buffer.writeIntLE(blockPalette.size)
            NbtOutputStream(LittleEndianByteBufOutputStream(buffer)).use { stream ->
                blockPalette.forEach {
                    stream.writeTag(compoundTag().apply {
                        val block = BlockState.byId(it)!!
                        setString("name", block.key)
                    })
                }
            }
        }
    }

    companion object {
        private val airId = BlockState.byKeyWithStates("minecraft:air")?.runtimeId ?: error("Missing minecraft:air")
    }
}

fun PacketBuffer.readLayer(): Layer {
    val header = readByte().toInt()
    val version = BitArray.Version.byBitsPerEntry(header shr 1)
    val runtime = header and 1 == 1
    val blocks = version.bitArray(BlockStorage.XZSize * Section.YSize * BlockStorage.XZSize, IntArray(version.bitArrayDataSize(BlockStorage.XZSize * Section.YSize * BlockStorage.XZSize)) { readIntLE() })
    return Layer(IntArrayList().apply {
        if (runtime) repeat(readVarInt()) { add(readVarInt()) } else {
            NbtInputStream(LittleEndianByteBufInputStream(buffer)).use { stream ->
                repeat(readIntLE()) {
                    val tag = stream.readTag()!!.asCompoundTag()!!
                    val properties = mutableMapOf<String, Any>()
                    tag.getCompoundTagOrNull("states")?.toMap()?.forEach { (blockStatePropertyName, blockStatePropertyTag) ->
                        properties[blockStatePropertyName] = when (blockStatePropertyTag.type) {
                            TagType.Byte -> blockStatePropertyTag.asNumberTag()!!.toByte() != 0.toByte()
                            TagType.Int -> blockStatePropertyTag.asNumberTag()!!.toInt()
                            TagType.String -> blockStatePropertyTag.asArrayTag()!!.valueToString()
                            else -> TODO()
                        }
                    }
                    add(BlockState.byKey(tag.getString("name")).filter { it.properties == properties }.findAny().get().runtimeId)
                }
            }
        }
    }, blocks, runtime)
}
