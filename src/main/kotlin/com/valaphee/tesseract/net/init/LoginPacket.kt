/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net.init

import com.google.gson.JsonObject
import com.google.gson.internal.Streams
import com.google.gson.stream.JsonReader
import com.valaphee.tesseract.actor.player.AuthExtra
import com.valaphee.tesseract.actor.player.User
import com.valaphee.tesseract.actor.player.asAuthExtra
import com.valaphee.tesseract.actor.player.asUser
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import com.valaphee.tesseract.util.MojangRootKey
import com.valaphee.tesseract.util.generatePublicKey
import com.valaphee.tesseract.util.getJsonArray
import org.jose4j.jwa.AlgorithmConstraints
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.InvalidJwtException
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import java.io.StringReader
import java.security.PublicKey

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Serverbound)
data class LoginPacket(
    val protocolVersion: Int,
    val publicKey: PublicKey,
    val authExtra: AuthExtra,
    val user: User,
    var verified: Boolean
) : Packet {
    override val id get() = 0x01

    override fun write(buffer: PacketBuffer, version: Int) = TODO()

    override fun handle(handler: PacketHandler) = handler.login(this)
}

/**
 * @author Kevin Ludwig
 */
class LoginPacketReader : PacketReader {
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
        val authJsonJwsChain = Streams.parse(JsonReader(StringReader(buffer.readAsciiStringLe().toString()))).asJsonObject.getJsonArray("chain")
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
            verificationKey = generatePublicKey(authJwsJsonPayload["identityPublicKey"].asString)
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
            verificationKey!!,
            authJwsJsonPayload!!.getAsJsonObject("extraData").asAuthExtra,
            Streams.parse(JsonReader(StringReader(userJwtClaims.rawJson))).asJsonObject.asUser,
            verified && mojangKeyVerified
        )
    }
}
