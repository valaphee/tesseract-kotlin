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

package com.valaphee.tesseract.entity.player

import com.valaphee.tesseract.command.net.Permission
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.world.WorldFlag

/**
 * @author Kevin Ludwig
 */
data class AdventureSettingsPacket(
    var uniqueEntityId: Long,
    var playerFlags: Collection<PlayerFlag>,
    var permission: Permission,
    var worldFlags: Collection<WorldFlag>,
    var rank: Rank,
    var customFlags: Int
) : Packet {
    override val id get() = 0x37

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarUIntFlags(playerFlags)
        buffer.writeVarUInt(permission.ordinal)
        buffer.writeVarUIntFlags(worldFlags)
        buffer.writeVarUInt(rank.ordinal)
        buffer.writeVarUInt(customFlags)
        buffer.writeLongLE(uniqueEntityId)
    }

    override fun handle(handler: PacketHandler) = handler.adventureSettings(this)
}

/**
 * @author Kevin Ludwig
 */
object AdventureSettingsPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): AdventureSettingsPacket {
        val playerFlags = buffer.readVarUIntFlags<PlayerFlag>()
        val permission = Permission.values()[buffer.readVarUInt()]
        val worldFlags = buffer.readVarUIntFlags<WorldFlag>()
        val rank = Rank.values()[buffer.readVarUInt()]
        val customFlags = buffer.readVarUInt()
        val uniqueEntityId = buffer.readLongLE()
        return AdventureSettingsPacket(uniqueEntityId, playerFlags, permission, worldFlags, rank, customFlags)
    }
}
