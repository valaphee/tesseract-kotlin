/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain.blocks

import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.world.chunk.terrain.block.BlockState
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList

/**
 * @author Kevin Ludwig
 */
class Layer(
    var blockPalette: IntList,
    var blocks: BitArray,
    var runtime: Boolean = true
) {
    constructor(version: BitArray.Version, runtime: Boolean = true) : this(IntArrayList(16).apply { add(airId) }, version.bitArray(Blocks.XZSize * Section.YSize * Blocks.XZSize), runtime)

    fun get(index: Int): Int = blockPalette.getInt(blocks[index])

    fun set(index: Int, id: Int) {
        var paletteId = blockPalette.indexOf(id)
        if (paletteId == -1) {
            paletteId = blockPalette.size
            blockPalette.add(id)
            val blocksVersion = blocks.version
            if (paletteId > blocksVersion.maximumEntryValue) {
                blocksVersion.next?.let {
                    val newBlocks = it.bitArray(Blocks.XZSize * Section.YSize * Blocks.XZSize)
                    repeat(Blocks.XZSize * Section.YSize * Blocks.XZSize) { newBlocks[it] = blocks[it] }
                    blocks = newBlocks
                }
            }
        }
        blocks[index] = paletteId
    }

    val empty get() = blocks.empty

    fun writeToBuffer(buffer: PacketBuffer) {
        buffer.writeByte((blocks.version.bitsPerEntry shl 1) or if (runtime) 1 else 0)
        blocks.data.forEach { buffer.writeIntLE(it) }
        buffer.writeVarInt(blockPalette.size)
        blockPalette.forEach { buffer.writeVarInt(it) }
    }

    companion object {
        private val airId = BlockState.byKeyWithStates("minecraft:air")?.runtimeId ?: error("Missing minecraft:air")
    }
}

fun PacketBuffer.readLayer(): Layer {
    val header = readByte().toInt()
    val version = BitArray.Version.byBitsPerEntry(header shr 1)
    val blocks = version.bitArray(Blocks.XZSize * Section.YSize * Blocks.XZSize, IntArray(version.bitArrayDataSize(Blocks.XZSize * Section.YSize * Blocks.XZSize)) { readIntLE() })
    return Layer(IntArrayList().apply { repeat(readVarInt()) { add(readVarInt()) } }, blocks, header and 1 == 1)
}