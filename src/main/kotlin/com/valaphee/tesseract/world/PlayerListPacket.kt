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

package com.valaphee.tesseract.world

import com.valaphee.tesseract.entity.player.User
import com.valaphee.tesseract.entity.player.appearance.Appearance
import com.valaphee.tesseract.entity.player.appearance.readAppearance
import com.valaphee.tesseract.entity.player.appearance.readAppearancePre390
import com.valaphee.tesseract.entity.player.appearance.readAppearancePre419
import com.valaphee.tesseract.entity.player.appearance.readAppearancePre428
import com.valaphee.tesseract.entity.player.appearance.readAppearancePre465
import com.valaphee.tesseract.entity.player.appearance.writeAppearance
import com.valaphee.tesseract.entity.player.appearance.writeAppearancePre390
import com.valaphee.tesseract.entity.player.appearance.writeAppearancePre419
import com.valaphee.tesseract.entity.player.appearance.writeAppearancePre428
import com.valaphee.tesseract.entity.player.appearance.writeAppearancePre465
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
data class PlayerListPacket(
    val action: Action,
    val entries: Array<Entry>
) : Packet {
    enum class Action {
        Add, Remove
    }

    data class Entry(
        val userId: UUID,
        val uniqueEntityId: Long,
        val userName: String?,
        val xboxUserId: String?,
        val platformChatId: String?,
        val operatingSystem: User.OperatingSystem?,
        val appearance: Appearance?,
        val teacher: Boolean,
        val host: Boolean
    ) {
        constructor(userId: UUID) : this(userId, 0, null, null, null, null, null, false, false)
    }

    override val id get() = 0x3F

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeByte(action.ordinal)
        buffer.writeVarUInt(entries.size)
        entries.forEach {
            buffer.writeUuid(it.userId)
            if (action == Action.Add) {
                buffer.writeVarLong(it.uniqueEntityId)
                buffer.writeString(it.userName!!)
                buffer.writeString(it.xboxUserId!!)
                buffer.writeString(it.platformChatId!!)
                buffer.writeIntLE(it.operatingSystem!!.ordinal)
                if (version >= 465) buffer.writeAppearance(it.appearance!!) else if (version >= 428) buffer.writeAppearancePre465(it.appearance!!) else if (version >= 419) buffer.writeAppearancePre428(it.appearance!!) else if (version >= 390) buffer.writeAppearancePre419(it.appearance!!) else buffer.writeAppearancePre390(it.appearance!!)
                buffer.writeBoolean(it.teacher)
                buffer.writeBoolean(it.host)
            }
        }
        if (version >= 390 && action == Action.Add) entries.forEach { buffer.writeBoolean(it.appearance!!.trusted) }
    }

    override fun handle(handler: PacketHandler) = handler.playerList(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayerListPacket

        if (action != other.action) return false
        if (!entries.contentEquals(other.entries)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = action.hashCode()
        result = 31 * result + entries.contentHashCode()
        return result
    }
}

/**
 * @author Kevin Ludwig
 */
object PlayerListPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): PlayerListPacket {
        val action = PlayerListPacket.Action.values()[buffer.readUnsignedByte().toInt()]
        val entries = Array(buffer.readVarUInt()) {
            when (action) {
                PlayerListPacket.Action.Add -> PlayerListPacket.Entry(
                    buffer.readUuid(),
                    buffer.readVarLong(),
                    buffer.readString(),
                    buffer.readString(),
                    buffer.readString(),
                    User.OperatingSystem.values()[buffer.readIntLE()],
                    if (version >= 465) buffer.readAppearance() else if (version >= 428) buffer.readAppearancePre465() else if (version >= 419) buffer.readAppearancePre428() else if (version >= 390) buffer.readAppearancePre419() else buffer.readAppearancePre390(),
                    buffer.readBoolean(),
                    buffer.readBoolean()
                )
                else -> PlayerListPacket.Entry(buffer.readUuid())
            }
        }.apply {
            if (version > 389 && action == PlayerListPacket.Action.Add) forEach { (_, _, _, _, _, _, appearance) -> appearance!!.trusted = buffer.readBoolean() }
        }
        return PlayerListPacket(action, entries)
    }
}
