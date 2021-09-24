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

package com.valaphee.tesseract.net.init.pack

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import com.valaphee.tesseract.world.Experiment
import com.valaphee.tesseract.world.writeExperiment
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
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
object PacksStackPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = PacksStackPacket(
        buffer.readBoolean(),
        Array(buffer.readVarUInt()) { PacksStackPacket.Pack(UUID.fromString(buffer.readString()), buffer.readString(), buffer.readString()) },
        Array(buffer.readVarUInt()) { PacksStackPacket.Pack(UUID.fromString(buffer.readString()), buffer.readString(), buffer.readString()) },
        if (version < 419) buffer.readBoolean() else false,
        "", emptyArray(), false/*buffer.readString(),
        if (version >= 419) Array(buffer.readIntLE()) { buffer.readExperiment() } else emptyArray(),
        if (version >= 419) buffer.readBoolean() else false TODO*/
    )
}
