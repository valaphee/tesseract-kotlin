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
 */

package com.valaphee.tesseract.entity

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader

/**
 * @author Kevin Ludwig
 */
class EntityAnimatePacket(
    val animation: String,
    val nextState: String,
    val stopExpression: String,
    val controller: String,
    val blendOutTime: Float,
    val runtimeEntityIds: LongArray
) : Packet() {
    override val id get() = 0x9E

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeString(animation)
        buffer.writeString(nextState)
        buffer.writeString(stopExpression)
        if (version >= 465) buffer.writeInt(0)
        buffer.writeString(controller)
        buffer.writeFloatLE(blendOutTime)
        buffer.writeVarUInt(runtimeEntityIds.size)
        runtimeEntityIds.forEach { buffer.writeVarULong(it) }
    }

    override fun handle(handler: PacketHandler) = handler.entityAnimate(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EntityAnimatePacket

        if (animation != other.animation) return false
        if (nextState != other.nextState) return false
        if (stopExpression != other.stopExpression) return false
        if (controller != other.controller) return false
        if (blendOutTime != other.blendOutTime) return false
        if (!runtimeEntityIds.contentEquals(other.runtimeEntityIds)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = animation.hashCode()
        result = 31 * result + nextState.hashCode()
        result = 31 * result + stopExpression.hashCode()
        result = 31 * result + controller.hashCode()
        result = 31 * result + blendOutTime.hashCode()
        result = 31 * result + runtimeEntityIds.contentHashCode()
        return result
    }

    override fun toString() = "EntityAnimatePacket(animation='$animation', nextState='$nextState', stopExpression='$stopExpression', controller='$controller', blendOutTime=$blendOutTime, runtimeEntityIds=${runtimeEntityIds.contentToString()})"
}

/**
 * @author Kevin Ludwig
 */
object EntityAnimatePacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = EntityAnimatePacket(
        buffer.readString(),
        buffer.readString(),
        buffer.readString(),
        buffer.readString(),
        buffer.readFloatLE(),
        LongArray(buffer.readVarUInt()) { buffer.readVarULong() },
    )
}
