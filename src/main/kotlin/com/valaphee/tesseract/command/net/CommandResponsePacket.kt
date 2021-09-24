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

package com.valaphee.tesseract.command.net

import com.valaphee.tesseract.command.Message
import com.valaphee.tesseract.command.readMessage
import com.valaphee.tesseract.command.writeMessage
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
data class CommandResponsePacket(
    var origin: Origin,
    var type: Type,
    var successCount: Int,
    var messages: Array<Message>,
    var data: String?
) : Packet {
    enum class Type {
        None, LastOutput, Silent, AllOutput, Data
    }

    override val id get() = 0x4F

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeOrigin(origin)
        buffer.writeByte(type.ordinal)
        buffer.writeVarUInt(successCount)
        messages.let {
            buffer.writeVarUInt(it.size)
            it.forEach { buffer.writeMessage(it) }
        }
        if (type == Type.Data) buffer.writeString(data!!)
    }

    override fun handle(handler: PacketHandler) = handler.commandResponse(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommandResponsePacket

        if (origin != other.origin) return false
        if (type != other.type) return false
        if (successCount != other.successCount) return false
        if (!messages.contentEquals(other.messages)) return false
        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        var result = origin.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + successCount
        result = 31 * result + messages.contentHashCode()
        result = 31 * result + data.hashCode()
        return result
    }
}

/**
 * @author Kevin Ludwig
 */
object CommandResponsePacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): CommandResponsePacket {
        val origin = buffer.readOrigin()
        val type = CommandResponsePacket.Type.values()[buffer.readByte().toInt()]
        val successCount = buffer.readVarUInt()
        val messages = Array(buffer.readVarUInt()) { buffer.readMessage() }
        val data = if (type == CommandResponsePacket.Type.Data) buffer.readString() else null
        return CommandResponsePacket(origin, type, successCount, messages, data)
    }
}
