/*
 * Copyright (c) 2021, GrieferGames, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk

import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.util.NibbleArray
import com.valaphee.tesseract.util.nibbleArray

/**
 * @author Kevin Ludwig
 */
interface SubChunk {
    val empty: Boolean

    fun getBlock(x: Int, y: Int, z: Int): Int

    fun getBlock(x: Int, y: Int, z: Int, layer: Int = 0) = getBlock(x, y, z)

    fun setBlock(x: Int, y: Int, z: Int, id: Int)

    fun setBlock(x: Int, y: Int, z: Int, id: Int, layer: Int = 0) = setBlock(x, y, z, id)

    fun writeToBuffer(buffer: PacketBuffer)

    companion object {
        const val YSize = 16
        const val XShift = 8
        const val ZShift = 4
    }
}

/**
 * @author Kevin Ludwig
 */
class SubChunkV0(
    var blockIds: ByteArray = ByteArray(Chunk.XZSize * SubChunk.YSize * Chunk.XZSize),
    var blockSubIds: NibbleArray = nibbleArray(Chunk.XZSize * SubChunk.YSize * Chunk.XZSize)
) : SubChunk {
    override fun getBlock(x: Int, y: Int, z: Int): Int {
        val index = (x shl SubChunk.XShift) or (z shl SubChunk.ZShift) or y
        return (blockIds[index].toInt() and (blockIdMask shl blockIdShift)) or blockSubIds[index]
    }

    override fun setBlock(x: Int, y: Int, z: Int, id: Int) {
        val index = (x shl SubChunk.XShift) or (z shl SubChunk.ZShift) or y
        blockIds[index] = ((id shr blockIdShift) and blockIdMask).toByte()
        blockSubIds[index] = (id and blockSubIdMask)
    }

    override val empty get() = blockIds.all { it.toInt() == 0 }

    override fun writeToBuffer(buffer: PacketBuffer) {
        buffer.writeByte(0)
        buffer.writeBytes(blockIds)
        buffer.writeBytes(blockSubIds.data)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SubChunkV0

        if (!blockIds.contentEquals(other.blockIds)) return false
        if (blockSubIds != other.blockSubIds) return false

        return true
    }

    override fun hashCode(): Int {
        var result = blockIds.contentHashCode()
        result = 31 * result + blockSubIds.hashCode()
        return result
    }

    companion object {
        private const val blockIdMask = 255
        private const val blockIdShift = 4
        private const val blockSubIdMask = 15
    }
}

/**
 * @author Kevin Ludwig
 */
class SubChunkV8(
    var layers: Array<Layer>
) : SubChunk {
    constructor(version: BitArray.Version, runtime: Boolean = true) : this(arrayOf(Layer(version, runtime), Layer(version, runtime)))

    override fun getBlock(x: Int, y: Int, z: Int) = getBlock(0, x, y, z)

    override fun getBlock(x: Int, y: Int, z: Int, layer: Int) = layers[layer].getBlock((x shl SubChunk.XShift) or (z shl SubChunk.ZShift) or y)

    override fun setBlock(x: Int, y: Int, z: Int, id: Int) = setBlock(0, x, y, z, id)

    override fun setBlock(x: Int, y: Int, z: Int, id: Int, layer: Int) = layers[layer].setBlock((x shl SubChunk.XShift) or (z shl SubChunk.ZShift) or y, id)

    override val empty get() = layers.all { it.empty }

    override fun writeToBuffer(buffer: PacketBuffer) {
        buffer.writeByte(8)
        buffer.writeByte(layers.size)
        layers.forEach { it.writeToBuffer(buffer) }
    }
}

fun PacketBuffer.readSubChunk(): SubChunk = when (readUnsignedByte().toInt()) {
    0 -> {
        val blockIds = ByteArray(Chunk.XZSize * SubChunk.YSize * Chunk.XZSize)
        readBytes(blockIds)
        val blockSubIdsData = ByteArray((Chunk.XZSize * SubChunk.YSize * Chunk.XZSize) / 2)
        readBytes(blockSubIdsData)
        SubChunkV0(blockIds, nibbleArray(blockSubIdsData))
    }
    8 -> SubChunkV8(Array(readUnsignedByte().toInt()) { readLayer() })
    else -> throw IndexOutOfBoundsException()
}
