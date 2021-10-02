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

package com.valaphee.tesseract.pack

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
class PacksPacket(
    val forcedToAccept: Boolean,
    val scriptingEnabled: Boolean,
    val forcingServerPacksEnabled: Boolean,
    val behaviorPacks: Array<Pack>,
    val resourcePacks: Array<Pack>
) : Packet {
    data class Pack(
        val id: UUID,
        val version: String,
        val size: Long,
        val encryptionKey: String,
        val subPackName: String,
        val contentId: String,
        val scripting: Boolean,
        val raytracingCapable: Boolean = false
    )

    override val id get() = 0x06

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeBoolean(forcedToAccept)
        buffer.writeBoolean(scriptingEnabled)
        if (version >= 448) buffer.writeBoolean(forcingServerPacksEnabled)
        buffer.writeShortLE(behaviorPacks.size)
        behaviorPacks.forEach {
            buffer.writeString(it.id.toString())
            buffer.writeString(it.version)
            buffer.writeLongLE(it.size)
            buffer.writeString(it.encryptionKey)
            buffer.writeString(it.subPackName)
            buffer.writeString(it.contentId)
            buffer.writeBoolean(it.scripting)
        }
        buffer.writeShortLE(resourcePacks.size)
        resourcePacks.forEach {
            buffer.writeString(it.id.toString())
            buffer.writeString(it.version)
            buffer.writeLongLE(it.size)
            buffer.writeString(it.encryptionKey)
            buffer.writeString(it.subPackName)
            buffer.writeString(it.contentId)
            buffer.writeBoolean(it.scripting)
            if (version >= 422) buffer.writeBoolean(it.raytracingCapable)
        }
    }

    override fun handle(handler: PacketHandler) = handler.packs(this)

    override fun toString() = "PacksPacket(forcedToAccept=$forcedToAccept, scriptingEnabled=$scriptingEnabled, forcingServerPacksEnabled=$forcingServerPacksEnabled, behaviorPacks=${behaviorPacks.contentToString()}, resourcePacks=${resourcePacks.contentToString()})"
}

/**
 * @author Kevin Ludwig
 */
object PacksPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = PacksPacket(
        buffer.readBoolean(),
        buffer.readBoolean(),
        if (version >= 448) buffer.readBoolean() else false,
        Array(buffer.readUnsignedShortLE()) { PacksPacket.Pack(UUID.fromString(buffer.readString()), buffer.readString(), buffer.readLongLE(), buffer.readString(), buffer.readString(), buffer.readString(), buffer.readBoolean()) },
        Array(buffer.readUnsignedShortLE()) { PacksPacket.Pack(UUID.fromString(buffer.readString()), buffer.readString(), buffer.readLongLE(), buffer.readString(), buffer.readString(), buffer.readString(), buffer.readBoolean(), if (version >= 422) buffer.readBoolean() else false) }
    )
}
