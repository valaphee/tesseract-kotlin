/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain.blocks

import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.util.NibbleArray
import com.valaphee.tesseract.util.nibbleArray

/**
 * @author Kevin Ludwig
 */
interface Section {
    val empty: Boolean

    fun get(x: Int, y: Int, z: Int): Int

    fun get(x: Int, y: Int, z: Int, layer: Int = 0) = get(x, y, z)

    fun set(x: Int, y: Int, z: Int, id: Int)

    fun set(x: Int, y: Int, z: Int, id: Int, layer: Int = 0) = set(x, y, z, id)

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
class SectionV0(
    var blockIds: ByteArray = ByteArray(Blocks.XZSize * Section.YSize * Blocks.XZSize),
    var blockSubIds: NibbleArray = nibbleArray(Blocks.XZSize * Section.YSize * Blocks.XZSize)
) : Section {
    override fun get(x: Int, y: Int, z: Int): Int {
        val index = (x shl Section.XShift) or (z shl Section.ZShift) or y
        return (blockIds[index].toInt() and (blockIdMask shl blockIdShift)) or blockSubIds[index]
    }

    override fun set(x: Int, y: Int, z: Int, id: Int) {
        val index = (x shl Section.XShift) or (z shl Section.ZShift) or y
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

        other as SectionV0

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
class SectionV8(
    var layers: Array<Layer>
) : Section {
    constructor(version: BitArray.Version, runtime: Boolean = true) : this(arrayOf(Layer(version, runtime), Layer(version, runtime)))

    override fun get(x: Int, y: Int, z: Int) = get(0, x, y, z)

    override fun get(x: Int, y: Int, z: Int, layer: Int) = layers[layer].get((x shl Section.XShift) or (z shl Section.ZShift) or y)

    override fun set(x: Int, y: Int, z: Int, id: Int) = set(0, x, y, z, id)

    override fun set(x: Int, y: Int, z: Int, id: Int, layer: Int) = layers[layer].set((x shl Section.XShift) or (z shl Section.ZShift) or y, id)

    override val empty get() = layers.all { it.empty }

    override fun writeToBuffer(buffer: PacketBuffer) {
        buffer.writeByte(8)
        buffer.writeByte(layers.size)
        layers.forEach { it.writeToBuffer(buffer) }
    }
}

fun PacketBuffer.readSection() = when (readUnsignedByte().toInt()) {
    0 -> {
        val blockIds = ByteArray(Blocks.XZSize * Section.YSize * Blocks.XZSize)
        readBytes(blockIds)
        val blockSubIdsData = ByteArray((Blocks.XZSize * Section.YSize * Blocks.XZSize) / 2)
        readBytes(blockSubIdsData)
        SectionV0(blockIds, nibbleArray(blockSubIdsData))
    }
    8 -> SectionV8(Array(readUnsignedByte().toInt()) { readLayer() })
    else -> TODO()
}
