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

package com.valaphee.tesseract.world.chunk

import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToServer)
class StructureBlockUpdatePacket(
    val position: Int3,
    val name: String,
    val metadata: String,
    val includingPlayers: Boolean,
    val showBoundingBox: Boolean,
    val mode: Mode,
    val settings: StructureSettings,
    val redstoneSaveMode: RedstoneSaveMode,
    val powered: Boolean
) : Packet() {
    enum class Mode {
        Data, Save, Load, Corner, None, Export
    }

    enum class RedstoneSaveMode {
        SavesToMemory, SavesToDisk
    }

    override val id get() = 0x5A

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeInt3UnsignedY(position)
        buffer.writeString(name)
        buffer.writeString(metadata)
        buffer.writeBoolean(includingPlayers)
        buffer.writeBoolean(showBoundingBox)
        buffer.writeVarInt(mode.ordinal)
        if (version >= 440) buffer.writeStructureSettings(settings) else buffer.writeStructureSettingsPre440(settings)
        buffer.writeVarInt(redstoneSaveMode.ordinal)
        buffer.writeBoolean(powered)
    }

    override fun handle(handler: PacketHandler) = handler.structureBlockUpdate(this)

    override fun toString() = "StructureBlockUpdatePacket(position=$position, name='$name', metadata='$metadata', includingPlayers=$includingPlayers, showBoundingBox=$showBoundingBox, mode=$mode, settings=$settings, redstoneSaveMode=$redstoneSaveMode, powered=$powered)"
}

/**
 * @author Kevin Ludwig
 */
object StructureBlockUpdatePacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = StructureBlockUpdatePacket(
        buffer.readInt3UnsignedY(),
        buffer.readString(),
        buffer.readString(),
        buffer.readBoolean(),
        buffer.readBoolean(),
        StructureBlockUpdatePacket.Mode.values()[buffer.readVarInt()],
        if (version >= 440) buffer.readStructureSettings() else buffer.readStructureSettingsPre440(),
        StructureBlockUpdatePacket.RedstoneSaveMode.values()[buffer.readVarInt()],
        buffer.readBoolean()
    )
}
