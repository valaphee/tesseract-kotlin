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

package com.valaphee.tesseract.actor.player

import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.actor.Link
import com.valaphee.tesseract.actor.metadata.Metadata
import com.valaphee.tesseract.actor.writeLink
import com.valaphee.tesseract.actor.writeLinkPre407
import com.valaphee.tesseract.inventory.item.stack.Stack
import com.valaphee.tesseract.inventory.item.stack.writeStack
import com.valaphee.tesseract.inventory.item.stack.writeStackPre431
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Clientbound)
data class PlayerAddPacket(
    var userId: UUID,
    var userName: String,
    var uniqueEntityId: Long,
    var runtimeEntityId: Long,
    var platformChatId: String,
    var position: Float3,
    var motion: Float3,
    var rotation: Float2,
    var headRotationYaw: Float,
    var stackInHand: Stack<*>?,
    var metadata: Metadata,
    /*var playerFlags: Collection<PlayerFlag>? = null,
    var levelFlags: Collection<LevelFlag>? = null,*/
    var customFlags: Int,
    /*var commandPermission: Permission? = null,
    var playerPermission: PlayerPermission? = null,*/
    var links: Array<Link>,
    var deviceId: String,
    var operatingSystem: User.OperatingSystem
) : Packet {
    override val id get() = 0x0C

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeUuid(userId)
        buffer.writeString(userName)
        buffer.writeVarLong(uniqueEntityId)
        buffer.writeVarULong(runtimeEntityId)
        buffer.writeString(platformChatId)
        buffer.writeFloat3(position)
        buffer.writeFloat3(motion)
        buffer.writeFloat2(rotation)
        buffer.writeFloatLE(headRotationYaw)
        if (version >= 431) buffer.writeStack(stackInHand) else buffer.writeStackPre431(stackInHand)
        metadata.writeToBuffer(buffer)
        /*buffer.writeVarUIntFlags(playerFlags!!)
        buffer.writeVarUInt(commandPermission!!.ordinal)
        buffer.writeVarUIntFlags(levelFlags!!)
        buffer.writeVarUInt(playerPermission!!.ordinal)*/
        buffer.writeVarUInt(0)
        buffer.writeVarUInt(0)
        buffer.writeVarUInt(0)
        buffer.writeVarUInt(0)
        buffer.writeVarUInt(customFlags)
        buffer.writeLongLE(uniqueEntityId)
        buffer.writeVarUInt(links.size)
        if (version >= 407) links.forEach(buffer::writeLink) else links.forEach(buffer::writeLinkPre407)
        buffer.writeString(deviceId)
        buffer.writeIntLE(operatingSystem.ordinal)
    }

    override fun handle(handler: PacketHandler) = handler.playerAdd(this)
}
