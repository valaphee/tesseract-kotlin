/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net.packet

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Clientbound)
data class StatusPacket(
    var status: Status
) : Packet {
    enum class Status {
        LoginSuccess,
        FailedClient,
        FailedServer,
        PlayerSpawn,
        FailedInvalidTenant,
        FailedVanillaEducation,
        FailedEducationVanilla,
        FailedServerFull
    }

    override val id get() = 0x02

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeInt(status.ordinal)
    }

    override fun handle(handler: PacketHandler) = handler.status(this)
}

/**
 * @author Kevin Ludwig
 */
class StatusPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = StatusPacket(StatusPacket.Status.values()[buffer.readInt()])
}
