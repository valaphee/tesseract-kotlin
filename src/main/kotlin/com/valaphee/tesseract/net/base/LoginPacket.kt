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

package com.valaphee.tesseract.net.base

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import io.netty.util.AsciiString

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToServer)
class LoginPacket(
    val protocolVersion: Int,
    val authJws: String,
    val userJws: String
) : Packet() {
    override val id get() = 0x01

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeInt(protocolVersion)
        val jwsIndex = buffer.writerIndex()
        buffer.writeZero(PacketBuffer.MaximumVarUIntLength)
        buffer.writeAsciiStringLe(AsciiString(authJws))
        buffer.writeAsciiStringLe(AsciiString(userJws))
        buffer.setMaximumLengthVarUInt(jwsIndex, buffer.writerIndex() - (jwsIndex + PacketBuffer.MaximumVarUIntLength))
    }

    override fun handle(handler: PacketHandler) = handler.login(this)

    override fun toString() = "LoginPacket(protocolVersion=$protocolVersion, authJws=$authJws, userJws=$userJws)"
}

/**
 * @author Kevin Ludwig
 */
object LoginPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): LoginPacket {
        val readerIndex = buffer.readerIndex()
        var protocolVersion = buffer.readInt()
        if (protocolVersion == 0) {
            buffer.readerIndex(readerIndex + 2)
            protocolVersion = buffer.readInt()
        }
        buffer.readVarUInt()
        val authJws = buffer.readAsciiStringLe().toString()
        val userJws = buffer.readAsciiStringLe().toString()
        return LoginPacket(protocolVersion, authJws, userJws)
    }
}
