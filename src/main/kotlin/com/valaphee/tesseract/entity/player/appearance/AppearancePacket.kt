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

package com.valaphee.tesseract.entity.player.appearance

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
class AppearancePacket(
    val userId: UUID,
    val appearance: Appearance,
    val newName: String,
    val oldName: String
) : Packet {
    override val id get() = 0x5D

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeUuid(userId)
        if (version >= 465) buffer.writeAppearance(appearance) else if (version >= 428) buffer.writeAppearancePre465(appearance) else if (version >= 419) buffer.writeAppearancePre428(appearance) else if (version >= 390) buffer.writeAppearancePre419(appearance) else buffer.writeAppearancePre390(appearance)
        buffer.writeString(newName)
        buffer.writeString(oldName)
        if (version >= 390) buffer.writeBoolean(appearance.trusted)
    }

    override fun handle(handler: PacketHandler) = handler.appearance(this)

    override fun toString() = "AppearancePacket(userId=$userId, appearance=$appearance, newName='$newName', oldName='$oldName')"
}

/**
 * @author Kevin Ludwig
 */
object AppearancePacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = AppearancePacket(
        buffer.readUuid(),
        if (version >= 465) buffer.readAppearance() else if (version >= 428) buffer.readAppearancePre465() else if (version >= 419) buffer.readAppearancePre428() else if (version >= 390) buffer.readAppearancePre419() else buffer.readAppearancePre390(),
        buffer.readString(),
        buffer.readString()
    ).also { if (version >= 390) it.appearance.trusted = buffer.readBoolean() }
}
