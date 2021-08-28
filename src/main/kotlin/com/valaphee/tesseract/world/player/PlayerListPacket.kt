/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.player

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
@Restrict(Restriction.Clientbound)
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
        entries.let {
            buffer.writeVarUInt(it.size)
            it.forEach {
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
            if (version >= 390 && action == Action.Add) it.forEach { buffer.writeBoolean(it.appearance!!.trusted) }
        }
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
