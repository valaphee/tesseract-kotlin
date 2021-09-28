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
import com.valaphee.tesseract.util.NibbleArray
import com.valaphee.tesseract.util.nibbleArray

/**
 * @author Kevin Ludwig
 */
interface Section {
    val empty: Boolean

    operator fun get(x: Int, y: Int, z: Int): Int

    operator fun get(x: Int, y: Int, z: Int, layer: Int) = get(x, y, z)

    operator fun set(x: Int, y: Int, z: Int, value: Int)

    operator fun set(x: Int, y: Int, z: Int, value: Int, layer: Int) = set(x, y, z, value)

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
class SectionLegacy(
    var blockIds: ByteArray = ByteArray(BlockStorage.XZSize * Section.YSize * BlockStorage.XZSize),
    var blockSubIds: NibbleArray = nibbleArray(BlockStorage.XZSize * Section.YSize * BlockStorage.XZSize)
) : Section {
    override fun get(x: Int, y: Int, z: Int): Int {
        val index = (x shl Section.XShift) or (z shl Section.ZShift) or y
        return (blockIds[index].toInt() and (blockIdMask shl blockIdShift)) or blockSubIds[index]
    }

    override fun set(x: Int, y: Int, z: Int, value: Int) {
        val index = (x shl Section.XShift) or (z shl Section.ZShift) or y
        blockIds[index] = ((value shr blockIdShift) and blockIdMask).toByte()
        blockSubIds[index] = (value and blockSubIdMask)
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

        other as SectionLegacy

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
class SectionCompact(
    var layers: Array<Layer>
) : Section {
    constructor(default: Int, version: BitArray.Version) : this(arrayOf(Layer(default, version), Layer(default, version)))

    override fun get(x: Int, y: Int, z: Int) = get(x, y, z, 0)

    override fun get(x: Int, y: Int, z: Int, layer: Int) = layers[layer].get((x shl Section.XShift) or (z shl Section.ZShift) or y)

    override fun set(x: Int, y: Int, z: Int, value: Int) = set(x, y, z, value, 0)

    override fun set(x: Int, y: Int, z: Int, value: Int, layer: Int) = layers[layer].set((x shl Section.XShift) or (z shl Section.ZShift) or y, value)

    override val empty get() = layers.all { it.empty }

    override fun writeToBuffer(buffer: PacketBuffer) = writeToBuffer(buffer, true)

    fun writeToBuffer(buffer: PacketBuffer, runtime: Boolean) {
        if (layers.size == 1) buffer.writeByte(1) else {
            buffer.writeByte(8)
            buffer.writeByte(layers.size)
        }
        layers.forEach { it.writeToBuffer(buffer, runtime) }
    }
}

fun PacketBuffer.readSection(default: Int) = when (readUnsignedByte().toInt()) {
    0, 2, 3, 4, 5, 6 -> SectionLegacy(ByteArray(BlockStorage.XZSize * Section.YSize * BlockStorage.XZSize).apply { readBytes(this) }, nibbleArray(ByteArray((BlockStorage.XZSize * Section.YSize * BlockStorage.XZSize) / 2).apply { readBytes(this) })).also { if (isReadable(4096)) skipBytes(4096) }
    1 -> SectionCompact(arrayOf(readLayer(default)))
    8 -> SectionCompact(Array(readUnsignedByte().toInt()) { readLayer(default) })
    else -> TODO()
}
