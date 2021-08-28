/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.util.NibbleArray
import com.valaphee.tesseract.util.nibbleArray
import io.netty.buffer.Unpooled

/**
 * @author Kevin Ludwig
 */
@JsonSerialize(using = SectionSerializer::class)
@JsonDeserialize(using = SectionDeserializer::class)
interface Section {
    val empty: Boolean

    fun get(x: Int, y: Int, z: Int): Int

    fun get(x: Int, y: Int, z: Int, layer: Int) = get(x, y, z)

    fun set(x: Int, y: Int, z: Int, value: Int)

    fun set(x: Int, y: Int, z: Int, value: Int, layer: Int) = set(x, y, z, value)

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
    constructor(version: BitArray.Version, runtime: Boolean) : this(arrayOf(Layer(version, runtime), Layer(version, runtime)))

    override fun get(x: Int, y: Int, z: Int) = get(x, y, z, 0)

    override fun get(x: Int, y: Int, z: Int, layer: Int) = layers[layer].get((x shl Section.XShift) or (z shl Section.ZShift) or y)

    override fun set(x: Int, y: Int, z: Int, value: Int) = set(x, y, z, value, 0)

    override fun set(x: Int, y: Int, z: Int, value: Int, layer: Int) = layers[layer].set((x shl Section.XShift) or (z shl Section.ZShift) or y, value)

    override val empty get() = layers.all { it.empty }

    override fun writeToBuffer(buffer: PacketBuffer) {
        if (layers.size == 1) buffer.writeByte(1) else {
            buffer.writeByte(8)
            buffer.writeByte(layers.size)
        }
        layers.forEach { it.writeToBuffer(buffer) }
    }
}

fun PacketBuffer.readSection() = when (readUnsignedByte().toInt()) {
    0, 2, 3, 4, 5, 6 -> SectionLegacy(ByteArray(BlockStorage.XZSize * Section.YSize * BlockStorage.XZSize).apply { readBytes(this) }, nibbleArray(ByteArray((BlockStorage.XZSize * Section.YSize * BlockStorage.XZSize) / 2).apply { readBytes(this) }))
    1 -> SectionCompact(arrayOf(readLayer()))
    8 -> SectionCompact(Array(readUnsignedByte().toInt()) { readLayer() })
    else -> TODO()
}

/**
 * @author Kevin Ludwig
 */
object SectionSerializer : JsonSerializer<Section>() {
    override fun serialize(value: Section, generator: JsonGenerator, provider: SerializerProvider) {
        var buffer: PacketBuffer? = null
        try {
            buffer = PacketBuffer(Unpooled.buffer())
            value.writeToBuffer(buffer)
            generator.writeBinary(buffer.array())
        } finally {
            buffer?.release()
        }
    }
}

/**
 * @author Kevin Ludwig
 */
class SectionDeserializer : JsonDeserializer<Section>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Section {
        var buffer: PacketBuffer? = null
        try {
            buffer = PacketBuffer(Unpooled.wrappedBuffer(parser.binaryValue))
            return buffer.readSection()
        } finally {
            buffer?.release()
        }
    }
}
