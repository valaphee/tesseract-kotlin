/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader

/**
 * @author Kevin Ludwig
 */
data class TextPacket(
    var type: Type,
    var needsTranslation: Boolean,
    var sourceName: String?,
    var message: String,
    var arguments: Array<String>?,
    var xboxUserId: String,
    var platformChatId: String
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TextPacket

        if (type != other.type) return false
        if (needsTranslation != other.needsTranslation) return false
        if (sourceName != other.sourceName) return false
        if (message != other.message) return false
        if (arguments != null) {
            if (other.arguments == null) return false
            if (!arguments.contentEquals(other.arguments)) return false
        } else if (other.arguments != null) return false
        if (xboxUserId != other.xboxUserId) return false
        if (platformChatId != other.platformChatId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + needsTranslation.hashCode()
        result = 31 * result + (sourceName?.hashCode() ?: 0)
        result = 31 * result + message.hashCode()
        result = 31 * result + (arguments?.contentHashCode() ?: 0)
        result = 31 * result + xboxUserId.hashCode()
        result = 31 * result + platformChatId.hashCode()
        return result
    }
}

/**
 * @author Kevin Ludwig
 */
class TextPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): TextPacket {
        val type = TextPacket.Type.values()[buffer.readUnsignedByte().toInt()]
        val needsTranslation = buffer.readBoolean()
        var sourceName: String? = null
        val message: String
        var arguments: Array<String>? = null
        when (type) {
            TextPacket.Type.Chat, TextPacket.Type.Whisper, TextPacket.Type.Announcement, TextPacket.Type.ObjectWhisper -> {
                sourceName = buffer.readString()
                message = buffer.readString()
            }
            TextPacket.Type.Raw, TextPacket.Type.Tip, TextPacket.Type.System, TextPacket.Type.Object -> message = buffer.readString()
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
