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

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.internal.Streams
import com.google.gson.stream.JsonReader
import com.valaphee.tesseract.entity.player.AuthExtra
import com.valaphee.tesseract.entity.player.User
import com.valaphee.tesseract.entity.player.asAuthExtra
import com.valaphee.tesseract.entity.player.asUser
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import com.valaphee.tesseract.util.MojangRootKey
import com.valaphee.tesseract.util.generatePublicKey
import com.valaphee.tesseract.util.getJsonArray
import com.valaphee.tesseract.util.getJsonObject
import com.valaphee.tesseract.util.getString
import io.netty.util.AsciiString
import org.jose4j.jwa.AlgorithmConstraints
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.InvalidJwtException
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import java.io.StringReader
import java.security.PrivateKey
import java.security.PublicKey
import java.util.Base64

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToServer)
class LoginPacket(
    val protocolVersion: Int,
    val authJws: String?,
    val publicKey: PublicKey,
    val authExtra: AuthExtra,
    val userJws: String?,
    val user: User,
    val privateKey: PrivateKey?,
    val verified: Boolean
) : Packet {
    override val id get() = 0x01

    constructor(protocolVersion: Int, publicKey: PublicKey, privateKey: PrivateKey?, authExtra: AuthExtra, user: User) : this(protocolVersion, null, publicKey, authExtra, null, user, privateKey, false)

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeInt(protocolVersion)
        val jwtLengthIndex = buffer.writerIndex()
        buffer.writeZero(PacketBuffer.MaximumVarUIntLength)
        buffer.writeAsciiStringLe(authJws?.let { AsciiString(it) } ?: AsciiString(JsonObject().apply {
            add("chain", JsonArray().apply {
                add(JsonWebSignature().apply {
                    setHeader("alg", "ES384")
                    setHeader("x5u", base64Encoder.encodeToString(publicKey.encoded))
                    payload = JsonObject().apply {
                        addProperty("exp", (System.currentTimeMillis() / 1_000L) + 31_536L)
                        add("extraData", JsonObject().apply(authExtra::toJson))
                        addProperty("identityPublicKey", base64Encoder.encodeToString(publicKey.encoded))
                        addProperty("nbf", (System.currentTimeMillis() / 1_000L) - 86_400L)
                    }.toString()
                    key = privateKey
                }.compactSerialization)
            })
        }.toString()))
        buffer.writeAsciiStringLe(userJws?.let { AsciiString(it) } ?: AsciiString(JsonWebSignature().apply {
            setHeader("alg", "ES384")
            setHeader("x5u", base64Encoder.encodeToString(publicKey.encoded))
            payload = JsonObject().apply(user::toJson).toString()
            key = privateKey
        }.compactSerialization))
        buffer.setMaximumLengthVarUInt(jwtLengthIndex, buffer.writerIndex() - (jwtLengthIndex + PacketBuffer.MaximumVarUIntLength))
    }

    override fun handle(handler: PacketHandler) = handler.login(this)

    override fun toString() = "LoginPacket(protocolVersion=$protocolVersion, authJws=$authJws, publicKey=$publicKey, authExtra=$authExtra, userJws=$userJws, user=$user, privateKey=$privateKey, verified=$verified)"

    companion object {
        private val base64Encoder: Base64.Encoder = Base64.getEncoder()
    }
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
        var verified = true
        var mojangKeyVerified = false
        val authExtraJws = buffer.readAsciiStringLe().toString()
        val authJsonJwsChain = Streams.parse(JsonReader(StringReader(authExtraJws))).asJsonObject.getJsonArray("chain")
        var verificationKey: PublicKey? = null
        var authJwsJsonPayload: JsonObject? = null
        for (authJsonJws in authJsonJwsChain) {
            val authJwtConsumerBuilder = JwtConsumerBuilder().setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT, "ES384")
            var authJwtClaims: JwtClaims
            try {
                if (!mojangKeyVerified) {
                    try {
                        authJwtConsumerBuilder.setVerificationKey(MojangRootKey)
                        authJwtConsumerBuilder.build().process(authJsonJws.asString)
                        mojangKeyVerified = true
                    } catch (_: InvalidJwtException) {
                    }
                }
                if (verified && verificationKey != null) authJwtConsumerBuilder.setVerificationKey(verificationKey) else authJwtConsumerBuilder.setSkipSignatureVerification()
                authJwtClaims = authJwtConsumerBuilder.build().processToClaims(authJsonJws.asString)
            } catch (_: InvalidJwtException) {
                verified = false
                authJwtConsumerBuilder.setSkipSignatureVerification()
                authJwtClaims = authJwtConsumerBuilder.build().processToClaims(authJsonJws.asString)
            }
            authJwsJsonPayload = Streams.parse(JsonReader(StringReader(authJwtClaims.rawJson))).asJsonObject
            verificationKey = generatePublicKey(authJwsJsonPayload.getString("identityPublicKey"))
        }
        val userJws = buffer.readAsciiStringLe().toString()
        val userJwtConsumerBuilder = JwtConsumerBuilder().setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT, "ES384")
        if (verified) userJwtConsumerBuilder.setVerificationKey(verificationKey) else userJwtConsumerBuilder.setSkipSignatureVerification()
        var userJwtClaims: JwtClaims
        try {
            userJwtClaims = userJwtConsumerBuilder.build().processToClaims(userJws)
        } catch (_: InvalidJwtException) {
            verified = false
            userJwtConsumerBuilder.setSkipSignatureVerification()
            userJwtClaims = userJwtConsumerBuilder.build().processToClaims(userJws)
        }
        return LoginPacket(
            protocolVersion,
            authExtraJws,
            verificationKey!!,
            authJwsJsonPayload!!.getJsonObject("extraData").asAuthExtra,
            userJws,
            Streams.parse(JsonReader(StringReader(userJwtClaims.rawJson))).asJsonObject.asUser,
            null,
            verified && mojangKeyVerified
        )
    }
}
