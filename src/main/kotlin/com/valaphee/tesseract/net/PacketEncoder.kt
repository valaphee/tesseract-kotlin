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

package com.valaphee.tesseract.net

import com.fasterxml.jackson.databind.ObjectMapper
import com.valaphee.tesseract.latestProtocolVersion
import com.valaphee.tesseract.util.Registry
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

/**
 * @author Kevin Ludwig
 */
class PacketEncoder(
    private val client: Boolean,
    var version: Int = latestProtocolVersion,
) : MessageToByteEncoder<Packet>() {
    var objectMapper: ObjectMapper? = null
    var blockStates: Registry<String>? = null
    var items: Registry<String>? = null

    /*override fun acceptOutboundMessage(message: Any): Boolean {
        val restrictions = message::class.findAnnotation<Restrict>()?.value ?: return true
        return client && restrictions.contains(Restriction.ToServer) || !client && restrictions.contains(Restriction.ToClient)
    }*/

    override fun encode(context: ChannelHandlerContext, message: Packet, out: ByteBuf) {
        val packetBuffer = PacketBuffer(out, false, objectMapper, blockStates, items)
        packetBuffer.writeVarUInt(message.id and Packet.idMask or ((message.senderId and Packet.senderIdMask) shl Packet.senderIdShift) or ((message.clientId and Packet.clientIdMask) shl Packet.clientIdShift))
        message.write(packetBuffer, version)
    }

    companion object {
        const val NAME = "ta-packet-encoder"
    }
}
