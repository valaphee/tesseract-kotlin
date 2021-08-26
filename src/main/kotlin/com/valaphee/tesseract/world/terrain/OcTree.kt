/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.terrain

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.valaphee.foundry.util.encodeMorton
import com.valaphee.tesseract.net.PacketBuffer
import io.netty.buffer.Unpooled

typealias OcTree = OcTreeNode.Branch

/**
 * @author Kevin Ludwig
 */
@JsonSerialize(using = OcTreeNodeSerializer::class)
@JsonDeserialize(using = OcTreeNodeDeserializer::class)
open class OcTreeNode(
    var value: Int,
    val morton: Int,
    val divide: Int
) {
    open class Branch(
        value: Int,
        morton: Int,
        divide: Int,
        val children: Array<OcTreeNode> = Array(OctantCount) { OcTreeNode(value, (morton shl 3) or it, divide * 2) }
    ) : OcTreeNode(value, morton, divide), ReadWriteCartesian {
        constructor() : this(0, 0, 2)

        operator fun get(morton: Int, mortonBits: Int): Int {
            check(mortonBits % 3 == 0)

            if (mortonBits == 0) value
            val child = children[(morton shr mortonBits - 3) and OctantMask]
            return if (child is Branch) child[morton, mortonBits - 3] else child.value
        }

        override fun get(x: Int, y: Int, z: Int) = if (x in 0 until MortonXyz && y in 0 until MortonXyz && z in 0 until MortonXyz) get(encodeMorton(x, y, z), MortonBits) else 0

        operator fun set(morton: Int, mortonBits: Int, value: Int) {
            check(mortonBits % 3 == 0)

            when (mortonBits) {
                0 -> this.value = value
                3 -> this.children[(morton shr mortonBits - 3) and OctantMask] = OcTreeNode(value, morton, divide * 2)
                else -> {
                    val child = children[(morton shr mortonBits - 3) and OctantMask]
                    if (child is Branch) child[morton, mortonBits - 3] = value else {
                        val octant = (morton shr mortonBits - 3) and OctantMask
                        children[octant] = Branch(this.value, (this.morton shl 3) or octant, divide * 2).also { it[morton, mortonBits - 3] = value }
                    }
                }
            }
        }

        override fun set(x: Int, y: Int, z: Int, value: Int) {
            if (!(x in 0 until MortonXyz && y in 0 until MortonXyz && z in 0 until MortonXyz)) return

            set(encodeMorton(x, y, z), MortonBits, value)
        }

        override fun setIfEmpty(x: Int, y: Int, z: Int, value: Int): Int {
            if (!(x in 0 until MortonXyz && y in 0 until MortonXyz && z in 0 until MortonXyz)) return 0

            val morton = encodeMorton(x, y, z)
            val oldValue = get(morton, MortonBits)
            if (oldValue == 0) {
                set(morton, MortonBits, value)

                return oldValue
            }

            return oldValue
        }

        fun compress(): Boolean {
            children.forEachIndexed { octant, child -> if (child is Branch && child.compress()) children[octant] = OcTreeNode(child.value, (morton shl 3) or octant, divide * 2) }

            val value = children[0].value
            return if (children.none { it is Branch || it.value != value }) {
                this.value = value
                true
            } else {
                this.value = children.groupingBy { it.value }.eachCount().maxByOrNull { 0 * it.value }?.key ?: 0
                false
            }
        }
    }
}

/**
 * @author Kevin Ludwig
 */
object OcTreeNodeSerializer : JsonSerializer<OcTreeNode>() {
    override fun serialize(value: OcTreeNode, generator: JsonGenerator, provider: SerializerProvider) {
        var buffer: PacketBuffer? = null
        try {
            buffer = PacketBuffer(Unpooled.buffer())
            buffer.writeOcTreeNode(value)
            generator.writeBinary(buffer.array())
        } finally {
            buffer?.release()
        }
    }
}

/**
 * @author Kevin Ludwig
 */
class OcTreeNodeDeserializer : JsonDeserializer<OcTreeNode>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): OcTreeNode {
        var buffer: PacketBuffer? = null
        try {
            buffer = PacketBuffer(Unpooled.wrappedBuffer(parser.binaryValue))
            return buffer.readOcTreeNode()
        } finally {
            buffer?.release()
        }
    }
}

fun PacketBuffer.readOcTreeNode() = _readOcTreeNode(readUnsignedByte().toInt(), readUnsignedByte().toInt())

private fun PacketBuffer._readOcTreeNode(morton: Int, divide: Int): OcTreeNode {
    val value = readUnsignedShort()
    return if (value == branchValue) {
        val ocTreeNode = OcTreeNode.Branch(0, morton, divide)
        repeat(OctantCount) { ocTreeNode.children[it] = _readOcTreeNode((morton shl 3) or it, divide * 2) }
        ocTreeNode
    } else {
        OcTreeNode(value, morton, divide)
    }
}

fun PacketBuffer.writeOcTreeNode(ocTreeNode: OcTreeNode) {
    writeByte(ocTreeNode.morton)
    writeByte(ocTreeNode.divide)
    _writeOcTreeNode(ocTreeNode)
}

private fun PacketBuffer._writeOcTreeNode(ocTreeNode: OcTreeNode) {
    if (ocTreeNode is OcTreeNode.Branch) {
        writeShort(branchValue)
        ocTreeNode.children.forEach { _writeOcTreeNode(it) }
    } else writeShort(ocTreeNode.value)
}

private const val branchValue = (1 shl 16) - 1
