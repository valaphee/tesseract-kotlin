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

package com.valaphee.tesseract.actor

import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.actor.attribute.Attributes
import com.valaphee.tesseract.actor.metadata.Metadata
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
data class ActorAddPacket(
    val uniqueEntityId: Long,
    val runtimeEntityId: Long,
    val type: String,
    val position: Float3,
    val velocity: Float3,
    val rotation: Float2,
    val headRotationYaw: Float,
    val attributes: Attributes,
    val metadata: Metadata,
    val links: Array<Link>
) : Packet {
    override val id get() = 0x0D

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarLong(uniqueEntityId)
        buffer.writeVarULong(runtimeEntityId)
        buffer.writeString(type)
        buffer.writeFloat3(position)
        buffer.writeFloat3(velocity)
        buffer.writeFloat2(rotation)
        buffer.writeFloatLE(headRotationYaw)
        attributes.writeToBuffer(buffer, false)
        metadata.writeToBuffer(buffer)
        buffer.writeVarUInt(links.size)
        if (version >= 407) links.forEach(buffer::writeLink) else links.forEach(buffer::writeLinkPre407)
    }

    override fun handle(handler: PacketHandler) = handler.actorAdd(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ActorAddPacket

        if (uniqueEntityId != other.uniqueEntityId) return false
        if (runtimeEntityId != other.runtimeEntityId) return false
        if (type != other.type) return false
        if (position != other.position) return false
        if (velocity != other.velocity) return false
        if (rotation != other.rotation) return false
        if (headRotationYaw != other.headRotationYaw) return false
        if (attributes != other.attributes) return false
        if (metadata != other.metadata) return false
        if (!links.contentEquals(other.links)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uniqueEntityId.hashCode()
        result = 31 * result + runtimeEntityId.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + position.hashCode()
        result = 31 * result + velocity.hashCode()
        result = 31 * result + rotation.hashCode()
        result = 31 * result + headRotationYaw.hashCode()
        result = 31 * result + attributes.hashCode()
        result = 31 * result + metadata.hashCode()
        result = 31 * result + links.contentHashCode()
        return result
    }
}

/**
 * @author Kevin Ludwig
 */
object ActorAddPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = ActorAddPacket(
        buffer.readVarLong(),
        buffer.readVarULong(),
        buffer.readString(),
        buffer.readFloat3(),
        buffer.readFloat3(),
        buffer.readFloat2(),
        buffer.readFloatLE(),
        Attributes().apply { readFromBuffer(buffer, false) },
        Metadata().apply { readFromBuffer(buffer) },
        Array(buffer.readVarUInt()) { if (version >= 407) buffer.readLink() else buffer.readLinkPre407() }
    )
}
