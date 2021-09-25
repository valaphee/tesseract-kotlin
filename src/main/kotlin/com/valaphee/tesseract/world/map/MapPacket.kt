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
data class MapPacket(
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
                it.forEach { (trackedObjectType, uniqueEntityId, blockPosition) ->
                    buffer.writeIntLE(trackedObjectType.ordinal)
                    when (trackedObjectType) {
                        TrackedObject.Type.Entity -> buffer.writeVarLong(uniqueEntityId!!)
                        TrackedObject.Type.Block -> buffer.writeInt3UnsignedY(blockPosition!!)
                    }
                }
            } ?: buffer.writeVarUInt(0)
            decorations?.let {
                buffer.writeVarUInt(it.size)
                it.forEach { (type, rotation, positionX, positionY, label, color) ->
                    buffer.writeByte(type.ordinal)
                    buffer.writeByte(rotation.toInt())
                    buffer.writeByte(positionX.toInt())
                    buffer.writeByte(positionY.toInt())
                    buffer.writeString(label)
                    buffer.writeVarUInt(color)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MapPacket

        if (mapId != other.mapId) return false
        if (dimension != other.dimension) return false
        if (locked != other.locked) return false
        if (trackedUniqueEntityIds != null) {
            if (other.trackedUniqueEntityIds == null) return false
            if (!trackedUniqueEntityIds.contentEquals(other.trackedUniqueEntityIds)) return false
        } else if (other.trackedUniqueEntityIds != null) return false
        if (scale != other.scale) return false
        if (trackedObjects != null) {
            if (other.trackedObjects == null) return false
            if (!trackedObjects.contentEquals(other.trackedObjects)) return false
        } else if (other.trackedObjects != null) return false
        if (decorations != null) {
            if (other.decorations == null) return false
            if (!decorations.contentEquals(other.decorations)) return false
        } else if (other.decorations != null) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (offsetX != other.offsetX) return false
        if (offsetY != other.offsetY) return false
        if (data != null) {
            if (other.data == null) return false
            if (!data.contentEquals(other.data)) return false
        } else if (other.data != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mapId.hashCode()
        result = 31 * result + dimension.hashCode()
        result = 31 * result + locked.hashCode()
        result = 31 * result + (trackedUniqueEntityIds?.contentHashCode() ?: 0)
        result = 31 * result + scale
        result = 31 * result + (trackedObjects?.contentHashCode() ?: 0)
        result = 31 * result + (decorations?.contentHashCode() ?: 0)
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + offsetX
        result = 31 * result + offsetY
        result = 31 * result + (data?.contentHashCode() ?: 0)
        return result
    }

    companion object {
        internal const val flagHasColor = 1 shl 1
        internal const val flagHasDecorationAndTrackedObjects = 1 shl 2
        internal const val flagHasTrackedEntities = 1 shl 3
        internal const val flagHasAll = flagHasColor or flagHasDecorationAndTrackedObjects or flagHasTrackedEntities
    }
}
