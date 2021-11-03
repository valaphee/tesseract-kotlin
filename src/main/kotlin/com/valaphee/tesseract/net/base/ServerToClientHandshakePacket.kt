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

import com.google.gson.JsonObject
import com.google.gson.internal.Streams
import com.google.gson.stream.JsonReader
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import com.valaphee.tesseract.util.generatePublicKey
import com.valaphee.tesseract.util.getString
import org.jose4j.jwa.AlgorithmConstraints
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import java.io.StringReader
import java.security.PrivateKey
import java.security.PublicKey
import java.util.Base64

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
class ServerToClientHandshakePacket(
    val serverPublicKey: PublicKey,
    val serverPrivateKey: PrivateKey?,
    val salt: ByteArray
) : Packet() {
    override val id get() = 0x03

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeString(JsonWebSignature().apply {
            setHeader("alg", "ES384")
            setHeader("x5u", base64Encoder.encodeToString(serverPublicKey.encoded))
            payload = JsonObject().apply { addProperty("salt", base64Encoder.encodeToString(salt)) }.toString()
            key = serverPrivateKey
        }.compactSerialization)
    }

    override fun handle(handler: PacketHandler) = handler.serverToClientHandshake(this)

    override fun toString() = "ServerToClientHandshakePacket(serverPublicKey=$serverPublicKey, serverPrivateKey=$serverPrivateKey, salt=${salt.contentToString()})"

    companion object {
        private val base64Encoder: Base64.Encoder = Base64.getEncoder()
    }
}

/**
 * @author Kevin Ludwig
 */
object ServerToClientHandshakePacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): ServerToClientHandshakePacket {
        val jws = buffer.readString()
        val jwtConsumerBuilder = JwtConsumerBuilder().setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT, "ES384")
        jwtConsumerBuilder.setSkipSignatureVerification()
        val jwtContext = jwtConsumerBuilder.build().process(jws)
        return ServerToClientHandshakePacket(generatePublicKey(jwtContext.joseObjects.first().headers.getStringHeaderValue("x5u")), null, base64Decoder.decode(Streams.parse(JsonReader(StringReader(jwtContext.jwtClaims.rawJson))).asJsonObject.getString("salt")))
    }

    private val base64Decoder: Base64.Decoder = Base64.getDecoder()
}
