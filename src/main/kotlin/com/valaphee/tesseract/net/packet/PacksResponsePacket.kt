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
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Serverbound)
data class PacksResponsePacket(
    var status: Status,
    var packIds: Array<UUID>
) : Packet {
    enum class Status {
        None, Refused, TransferPacks, HaveAllPacks, Completed
    }

    override val id get() = 0x08

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeByte(status.ordinal)
        buffer.writeShortLE(packIds.size)
        packIds.forEach { buffer.writeString(it.toString()) }
    }

    override fun handle(handler: PacketHandler) = Unit

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PacksResponsePacket

        if (status != other.status) return false
        if (!packIds.contentEquals(other.packIds)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + packIds.contentHashCode()
        return result
    }
}

/**
 * @author Kevin Ludwig
 */
class PacksResponsePacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = PacksResponsePacket(
        PacksResponsePacket.Status.values()[buffer.readUnsignedByte().toInt()],
        Array(buffer.readUnsignedShortLE()) { UUID.fromString(buffer.readString().split("_".toRegex(), 2).toTypedArray()[0]) }
    )
}
