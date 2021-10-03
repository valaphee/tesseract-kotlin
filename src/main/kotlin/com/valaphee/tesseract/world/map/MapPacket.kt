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

package com.valaphee.tesseract.world.map

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import com.valaphee.tesseract.world.Dimension

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
class MapPacket(
    val mapId: Long,
    val dimension: Dimension,
    val locked: Boolean,
    val trackedUniqueEntityIds: LongArray?,
    val scale: Int = 0,
    val trackedObjects: Array<TrackedObject>?,
    val decorations: Array<Decoration>?,
    val width: Int,
    val height: Int,
    val offsetX: Int,
    val offsetY: Int,
    val data: IntArray?
) : Packet {
    override val id get() = 0x43

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarLong(mapId)
        val flagsIndex = buffer.writerIndex()
        buffer.writeZero(PacketBuffer.MaximumVarUIntLength)
        buffer.writeByte(dimension.ordinal)
        buffer.writeBoolean(locked)
        var flagsValue = if (data?.isNotEmpty() == true) flagHasColor else 0 or if (decorations?.isNotEmpty() == true && trackedObjects?.isNotEmpty() == true) flagHasDecorationAndTrackedObjects else 0
        trackedUniqueEntityIds?.let {
            if (it.isNotEmpty()) {
                buffer.writeVarUInt(it.size)
                it.forEach { buffer.writeVarLong(it) }
                flagsValue = flagsValue or flagHasTrackedEntities
            }
        }
        if (flagsValue and flagHasAll != 0) buffer.writeByte(scale)
        if (flagsValue and flagHasDecorationAndTrackedObjects != 0) {
            trackedObjects?.let {
                buffer.writeVarUInt(it.size)
                it.forEach {
                    buffer.writeIntLE(it.type.ordinal)
                    when (it.type) {
                        TrackedObject.Type.Entity -> buffer.writeVarLong(it.uniqueEntityId!!)
                        TrackedObject.Type.Block -> buffer.writeInt3UnsignedY(it.blockPosition!!)
                    }
                }
            } ?: buffer.writeVarUInt(0)
            decorations?.let {
                buffer.writeVarUInt(it.size)
                it.forEach {
                    buffer.writeByte(it.type.ordinal)
                    buffer.writeByte(it.rotation.toInt())
                    buffer.writeByte(it.positionX.toInt())
                    buffer.writeByte(it.positionY.toInt())
                    buffer.writeString(it.label)
                    buffer.writeVarUInt(it.color)
                }
            } ?: buffer.writeVarUInt(0)
        }
        if (flagsValue and flagHasColor != 0) {
            data!!.let {
                buffer.writeVarInt(width)
                buffer.writeVarInt(height)
                buffer.writeVarInt(offsetX)
                buffer.writeVarInt(offsetY)
                buffer.writeVarUInt(it.size)
                it.forEach { buffer.writeVarUInt(it) }
            }
        }
        buffer.setMaximumLengthVarUInt(flagsIndex, flagsValue)
    }

    override fun handle(handler: PacketHandler) = handler.map(this)

    override fun toString() = "MapPacket(mapId=$mapId, dimension=$dimension, locked=$locked, trackedUniqueEntityIds=${trackedUniqueEntityIds?.contentToString()}, scale=$scale, trackedObjects=${trackedObjects?.contentToString()}, decorations=${decorations?.contentToString()}, width=$width, height=$height, offsetX=$offsetX, offsetY=$offsetY, data=${data?.contentToString()})"

    companion object {
        internal const val flagHasColor = 1 shl 1
        internal const val flagHasDecorationAndTrackedObjects = 1 shl 2
        internal const val flagHasTrackedEntities = 1 shl 3
        internal const val flagHasAll = flagHasColor or flagHasDecorationAndTrackedObjects or flagHasTrackedEntities
    }
}
