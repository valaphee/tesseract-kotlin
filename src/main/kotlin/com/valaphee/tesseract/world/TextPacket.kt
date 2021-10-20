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

package com.valaphee.tesseract.world

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader

/**
 * @author Kevin Ludwig
 */
class TextPacket(
    val type: Type,
    val needsTranslation: Boolean,
    val sourceName: String?,
    val message: String,
    val arguments: Array<String>?,
    val xboxUserId: String,
    val platformChatId: String
) : Packet {
    enum class Type {
        Raw, Chat, Translation, PopUp, JukeboxPopUp, Tip, System, Whisper, Announcement, Object, ObjectWhisper
    }

    override val id get() = 0x09

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeByte(type.ordinal)
        buffer.writeBoolean(needsTranslation)
        when (type) {
            Type.Chat, Type.Whisper, Type.Announcement, Type.ObjectWhisper -> {
                buffer.writeString(sourceName!!)
                buffer.writeString(message)
            }
            Type.Raw, Type.Tip, Type.System, Type.Object -> buffer.writeString(message)
            Type.Translation, Type.PopUp, Type.JukeboxPopUp -> {
                buffer.writeString(message)
                arguments!!.let {
                    buffer.writeVarUInt(it.size)
                    it.forEach { buffer.writeString(it) }
                }
            }
        }
        buffer.writeString(xboxUserId)
        buffer.writeString(platformChatId)
    }

    override fun handle(handler: PacketHandler) = handler.text(this)

    override fun toString() = "TextPacket(type=$type, needsTranslation=$needsTranslation, sourceName=$sourceName, message='$message', arguments=${arguments?.contentToString()}, xboxUserId='$xboxUserId', platformChatId='$platformChatId')"
}

/**
 * @author Kevin Ludwig
 */
object TextPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): TextPacket {
        val type = TextPacket.Type.values()[buffer.readUnsignedByte().toInt()]
        val needsTranslation = buffer.readBoolean()
        var sourceName: String? = null
        val message: String
        var arguments: Array<String>? = null
        when (type) {
            TextPacket.Type.Chat, TextPacket.Type.Whisper, TextPacket.Type.Announcement -> {
                sourceName = buffer.readString()
                message = buffer.readString()
            }
            TextPacket.Type.Raw, TextPacket.Type.Tip, TextPacket.Type.System, TextPacket.Type.Object, TextPacket.Type.ObjectWhisper -> message = buffer.readString()
            TextPacket.Type.Translation, TextPacket.Type.PopUp, TextPacket.Type.JukeboxPopUp -> {
                message = buffer.readString()
                arguments = Array(buffer.readVarUInt()) { buffer.readString() }
            }
        }
        val xboxUserId = buffer.readString()
        val platformChatId = buffer.readString()
        return TextPacket(type, needsTranslation, sourceName, message, arguments, xboxUserId, platformChatId)
    }
}
