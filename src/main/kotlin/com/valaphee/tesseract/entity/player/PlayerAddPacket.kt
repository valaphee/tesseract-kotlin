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

package com.valaphee.tesseract.entity.player

import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.entity.Link
import com.valaphee.tesseract.entity.metadata.Metadata
import com.valaphee.tesseract.entity.readLink
import com.valaphee.tesseract.entity.readLinkPre407
import com.valaphee.tesseract.entity.writeLink
import com.valaphee.tesseract.entity.writeLinkPre407
import com.valaphee.tesseract.command.net.Permission
import com.valaphee.tesseract.inventory.item.stack.Stack
import com.valaphee.tesseract.inventory.item.stack.readStack
import com.valaphee.tesseract.inventory.item.stack.readStackPre431
import com.valaphee.tesseract.inventory.item.stack.writeStack
import com.valaphee.tesseract.inventory.item.stack.writeStackPre431
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import com.valaphee.tesseract.world.WorldFlag
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
data class PlayerAddPacket(
    val userId: UUID,
    val userName: String,
    val uniqueEntityId: Long,
    val runtimeEntityId: Long,
    val platformChatId: String,
    val position: Float3,
    val velocity: Float3,
    val rotation: Float2,
    val headRotationYaw: Float,
    val stackInHand: Stack?,
    val metadata: Metadata,
    val playerFlags: Collection<PlayerFlag>,
    val permission: Permission,
    val worldFlags: Collection<WorldFlag>,
    val rank: Rank,
    val customFlags: Int,
    val links: Array<Link>,
    val deviceId: String,
    val operatingSystem: User.OperatingSystem
) : Packet {
    override val id get() = 0x0C

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeUuid(userId)
        buffer.writeString(userName)
        buffer.writeVarLong(uniqueEntityId)
        buffer.writeVarULong(runtimeEntityId)
        buffer.writeString(platformChatId)
        buffer.writeFloat3(position)
        buffer.writeFloat3(velocity)
        buffer.writeFloat2(rotation)
        buffer.writeFloatLE(headRotationYaw)
        if (version >= 431) buffer.writeStack(stackInHand) else buffer.writeStackPre431(stackInHand)
        metadata.writeToBuffer(buffer)
        buffer.writeVarUIntFlags(playerFlags)
        buffer.writeVarUInt(permission.ordinal)
        buffer.writeVarUIntFlags(worldFlags)
        buffer.writeVarUInt(rank.ordinal)
        buffer.writeVarUInt(customFlags)
        buffer.writeLongLE(uniqueEntityId)
        buffer.writeVarUInt(links.size)
        if (version >= 407) links.forEach(buffer::writeLink) else links.forEach(buffer::writeLinkPre407)
        buffer.writeString(deviceId)
        buffer.writeIntLE(operatingSystem.ordinal)
    }

    override fun handle(handler: PacketHandler) = handler.playerAdd(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayerAddPacket

        if (userId != other.userId) return false
        if (userName != other.userName) return false
        if (uniqueEntityId != other.uniqueEntityId) return false
        if (runtimeEntityId != other.runtimeEntityId) return false
        if (platformChatId != other.platformChatId) return false
        if (position != other.position) return false
        if (velocity != other.velocity) return false
        if (rotation != other.rotation) return false
        if (headRotationYaw != other.headRotationYaw) return false
        if (stackInHand != other.stackInHand) return false
        if (metadata != other.metadata) return false
        if (customFlags != other.customFlags) return false
        if (!links.contentEquals(other.links)) return false
        if (deviceId != other.deviceId) return false
        if (operatingSystem != other.operatingSystem) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + userName.hashCode()
        result = 31 * result + uniqueEntityId.hashCode()
        result = 31 * result + runtimeEntityId.hashCode()
        result = 31 * result + platformChatId.hashCode()
        result = 31 * result + position.hashCode()
        result = 31 * result + velocity.hashCode()
        result = 31 * result + rotation.hashCode()
        result = 31 * result + headRotationYaw.hashCode()
        result = 31 * result + (stackInHand?.hashCode() ?: 0)
        result = 31 * result + metadata.hashCode()
        result = 31 * result + customFlags
        result = 31 * result + links.contentHashCode()
        result = 31 * result + deviceId.hashCode()
        result = 31 * result + operatingSystem.hashCode()
        return result
    }
}

/**
 * @author Kevin Ludwig
 */
object PlayerAddPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = PlayerAddPacket(
        buffer.readUuid(),
        buffer.readString(),
        buffer.readVarLong(),
        buffer.readVarULong(),
        buffer.readString(),
        buffer.readFloat3(),
        buffer.readFloat3(),
        buffer.readFloat2(),
        buffer.readFloatLE(),
        if (version >= 431) buffer.readStack() else buffer.readStackPre431(),
        Metadata().apply { readFromBuffer(buffer) },
        buffer.readVarUIntFlags(),
        Permission.values()[buffer.readVarUInt()],
        buffer.readVarUIntFlags(),
        Rank.values()[buffer.readVarUInt()],
        buffer.readVarUInt().also { buffer.readLongLE() },
        Array(buffer.readVarUInt()) { if (version >= 407) buffer.readLink() else buffer.readLinkPre407() },
        buffer.readString(),
        User.OperatingSystem.values()[buffer.readIntLE()],
    )
}