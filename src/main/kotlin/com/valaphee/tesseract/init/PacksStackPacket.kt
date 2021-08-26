/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.init

import com.valaphee.tesseract.Experiment
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import com.valaphee.tesseract.readExperiment
import com.valaphee.tesseract.writeExperiment
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Clientbound)
data class PacksStackPacket(
    var forcedToAccept: Boolean,
    var behaviorPacks: Array<Pack>,
    var resourcePacks: Array<Pack>,
    var experimental: Boolean,
    var version: String,
    var experiments: Array<Experiment>,
    var experimentsPreviouslyToggled: Boolean
) : Packet {
    data class Pack(
        var packId: UUID,
        var version: String,
        var subPackName: String
    )

    override val id get() = 0x07

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeBoolean(forcedToAccept)
        buffer.writeVarUInt(behaviorPacks.size)
        behaviorPacks.forEach {
            buffer.writeString(it.packId.toString())
            buffer.writeString(it.version)
            buffer.writeString(it.subPackName)
        }
        buffer.writeVarUInt(resourcePacks.size)
        resourcePacks.forEach {
            buffer.writeString(it.packId.toString())
            buffer.writeString(it.version)
            buffer.writeString(it.subPackName)
        }
        if (version < 419) buffer.writeBoolean(experimental)
        buffer.writeString(this.version)
        if (version >= 419) {
            buffer.writeIntLE(experiments.size)
            experiments.forEach { buffer.writeExperiment(it) }
            buffer.writeBoolean(experimentsPreviouslyToggled)
        }
    }

    override fun handle(handler: PacketHandler) = handler.packsStack(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PacksStackPacket

        if (forcedToAccept != other.forcedToAccept) return false
        if (!behaviorPacks.contentEquals(other.behaviorPacks)) return false
        if (!resourcePacks.contentEquals(other.resourcePacks)) return false
        if (experimental != other.experimental) return false
        if (version != other.version) return false
        if (!experiments.contentEquals(other.experiments)) return false
        if (experimentsPreviouslyToggled != other.experimentsPreviouslyToggled) return false

        return true
    }

    override fun hashCode(): Int {
        var result = forcedToAccept.hashCode()
        result = 31 * result + behaviorPacks.contentHashCode()
        result = 31 * result + resourcePacks.contentHashCode()
        result = 31 * result + experimental.hashCode()
        result = 31 * result + version.hashCode()
        result = 31 * result + experiments.contentHashCode()
        result = 31 * result + experimentsPreviouslyToggled.hashCode()
        return result
    }
}


/**
 * @author Kevin Ludwig
 */
class PacksStackPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = PacksStackPacket(
        buffer.readBoolean(),
        Array(buffer.readVarUInt()) { PacksStackPacket.Pack(UUID.fromString(buffer.readString()), buffer.readString(), buffer.readString()) },
        Array(buffer.readVarUInt()) { PacksStackPacket.Pack(UUID.fromString(buffer.readString()), buffer.readString(), buffer.readString()) },
        if (version < 419) buffer.readBoolean() else false,
        buffer.readString(),
        if (version >= 419) Array(buffer.readIntLE()) { buffer.readExperiment() } else emptyArray(),
        if (version >= 419) buffer.readBoolean() else false
    )
}
