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

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import com.valaphee.tesseract.util.nbt.Tag

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
data class StructureTemplateDataExportResponsePacket(
    val name: String,
    val save: Boolean,
    val tag: Tag?,
    val type: Type
) : Packet {
    enum class Type {
        None, Export, Query
    }

    override val id get() = 0x85

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeString(name)
        buffer.writeBoolean(save)
        if (save) buffer.toNbtOutputStream().use { it.writeTag(tag) }
        buffer.writeByte(type.ordinal)
    }

    override fun handle(handler: PacketHandler) = handler.structureTemplateDataExportResponse(this)
}

/**
 * @author Kevin Ludwig
 */
object StructureTemplateDataExportResponsePacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): StructureTemplateDataExportResponsePacket {
        val name = buffer.readString()
        val save = buffer.readBoolean()
        val tag = if (save) buffer.toNbtInputStream().use { it.readTag() } else null
        val type = StructureTemplateDataExportResponsePacket.Type.values()[buffer.readUnsignedByte().toInt()]
        return StructureTemplateDataExportResponsePacket(name, save, tag, type)
    }
}
