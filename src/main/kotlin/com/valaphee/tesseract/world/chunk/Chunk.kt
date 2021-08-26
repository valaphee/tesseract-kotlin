/*
 * Copyright (c) 2021, GrieferGames, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk

import com.valaphee.tesseract.nbt.CompoundTag
import it.unimi.dsi.fastutil.ints.Int2ShortMap
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap

/**
 * @author Kevin Ludwig
 */
class Chunk(
    val x: Int,
    val z: Int
) {
    lateinit var biomes: ByteArray
    lateinit var subChunks: Array<SubChunk>
    lateinit var blockExtraData: Int2ShortMap
    lateinit var blockEntities: List<CompoundTag>

    fun initialize() {
        biomes = ByteArray(XZSize * XZSize)
        subChunks = Array(SubChunkCount) { SubChunkV8(BitArray.Version.V1) }
        blockExtraData = Int2ShortOpenHashMap()
        blockEntities = ArrayList()
    }

    fun getBiome(x: Int, z: Int) = biomes[(x and XZMask shl 4) or (z and XZMask)]

    fun setBiome(x: Int, z: Int, biome: Byte) {
        biomes[((x and XZMask) shl 4) or (z and XZMask)] = biome
    }

    fun getBlock(x: Int, y: Int, z: Int) = subChunks[y shr YShift].getBlock(x, y and SubChunkMask, z)

    fun getBlock(x: Int, y: Int, z: Int, layer: Int) = subChunks[y shr YShift].getBlock(layer, x, y and SubChunkMask, z)

    fun setBlock(x: Int, y: Int, z: Int, id: Int) = subChunks[y shr YShift].setBlock(x, y and SubChunkMask, z, id)

    fun setBlock(x: Int, y: Int, z: Int, id: Int, layer: Int) = subChunks[y shr YShift].setBlock(layer, x, y and SubChunkMask, z, id)

    companion object {
        const val XZSize = 16
        const val XZMask = XZSize - 1
        const val SubChunkCount = 16
        const val SubChunkMask = SubChunkCount - 1
        const val YShift = 4
    }
}
