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

import com.valaphee.tesseract.actor.player.Appearance
import com.valaphee.tesseract.actor.player.User
import com.valaphee.tesseract.actor.player.writeAppearance
import com.valaphee.tesseract.actor.player.writeAppearancePre390
import com.valaphee.tesseract.actor.player.writeAppearancePre419
import com.valaphee.tesseract.actor.player.writeAppearancePre428
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import java.util.UUID

/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
data class PlayerListPacket(
    var action: Action,
    var entries: Array<Entry>
) : Packet {
    enum class Action {
        Add, Remove
    }

    data class Entry(
        val userId: UUID,
        val uniqueEntityId: Long = 0,
        val userName: String? = null,
        val xboxUserId: String? = null,
        val platformChatId: String? = null,
        val operatingSystem: User.OperatingSystem? = null,
        val appearance: Appearance? = null,
        val teacher: Boolean = false,
        val host: Boolean = false
    )

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
                if (version >= 428) buffer.writeAppearance(it.appearance!!) else if (version >= 419) buffer.writeAppearancePre428(it.appearance!!) else if (version >= 390) buffer.writeAppearancePre419(it.appearance!!) else buffer.writeAppearancePre390(it.appearance!!)
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
