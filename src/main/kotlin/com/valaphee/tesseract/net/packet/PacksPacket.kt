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
@Restrict(Restriction.Clientbound)
data class PacksPacket(
    var forcedToAccept: Boolean,
    var scriptingEnabled: Boolean,
    var forcingServerPacksEnabled: Boolean,
    var behaviorPacks: Array<Pack>,
    var resourcePacks: Array<Pack>
) : Packet {
    data class Pack(
        var packId: UUID,
        var version: String,
        var size: Long,
        var encryptionKey: String,
        var subPackName: String,
        var contentId: String,
        var scripting: Boolean,
        var raytracingCapable: Boolean = false
    )

    override val id get() = 0x06

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeBoolean(forcedToAccept)
        buffer.writeBoolean(scriptingEnabled)
        if (version >= 448) buffer.writeBoolean(forcingServerPacksEnabled)
        buffer.writeShortLE(behaviorPacks.size)
        behaviorPacks.forEach {
            buffer.writeString(it.packId.toString())
            buffer.writeString(it.version)
            buffer.writeLongLE(it.size)
            buffer.writeString(it.encryptionKey)
            buffer.writeString(it.subPackName)
            buffer.writeString(it.contentId)
            buffer.writeBoolean(it.scripting)
        }
        buffer.writeShortLE(resourcePacks.size)
        resourcePacks.forEach {
            buffer.writeString(it.packId.toString())
            buffer.writeString(it.version)
            buffer.writeLongLE(it.size)
            buffer.writeString(it.encryptionKey)
            buffer.writeString(it.subPackName)
            buffer.writeString(it.contentId)
            buffer.writeBoolean(it.scripting)
            if (version >= 422) buffer.writeBoolean(it.raytracingCapable)
        }
    }

    override fun handle(handler: PacketHandler) = Unit

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PacksPacket

        if (forcedToAccept != other.forcedToAccept) return false
        if (scriptingEnabled != other.scriptingEnabled) return false
        if (forcingServerPacksEnabled != other.forcingServerPacksEnabled) return false
        if (!behaviorPacks.contentEquals(other.behaviorPacks)) return false
        if (!resourcePacks.contentEquals(other.resourcePacks)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = forcedToAccept.hashCode()
        result = 31 * result + scriptingEnabled.hashCode()
        result = 31 * result + forcingServerPacksEnabled.hashCode()
        result = 31 * result + behaviorPacks.contentHashCode()
        result = 31 * result + resourcePacks.contentHashCode()
        return result
    }
}

/**
 * @author Kevin Ludwig
 */
class PacksPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = PacksPacket(
        buffer.readBoolean(),
        buffer.readBoolean(),
        if (version >= 448) buffer.readBoolean() else false,
        Array(buffer.readUnsignedShortLE()) { PacksPacket.Pack(UUID.fromString(buffer.readString()), buffer.readString(), buffer.readLongLE(), buffer.readString(), buffer.readString(), buffer.readString(), buffer.readBoolean()) },
        Array(buffer.readUnsignedShortLE()) { PacksPacket.Pack(UUID.fromString(buffer.readString()), buffer.readString(), buffer.readLongLE(), buffer.readString(), buffer.readString(), buffer.readString(), buffer.readBoolean(), if (version >= 422) buffer.readBoolean() else false) }
    )
}
