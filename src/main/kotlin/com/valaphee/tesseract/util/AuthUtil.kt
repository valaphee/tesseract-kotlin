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

package com.valaphee.tesseract.util

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.internal.Streams
import com.google.gson.stream.JsonReader
import com.valaphee.tesseract.entity.player.AuthExtra
import com.valaphee.tesseract.entity.player.User
import com.valaphee.tesseract.entity.player.asAuthExtra
import com.valaphee.tesseract.entity.player.asUser
import org.jose4j.jwa.AlgorithmConstraints
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.consumer.InvalidJwtException
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import java.io.StringReader
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

fun authJws(keyPair: KeyPair, authExtra: AuthExtra) = JsonObject().apply {
    add("chain", JsonArray().apply {
        add(JsonWebSignature().apply {
            setHeader("alg", "ES384")
            setHeader("x5u", base64Encoder.encodeToString(keyPair.public.encoded))
            payload = JsonObject().apply {
                val iat = System.currentTimeMillis()
                addProperty("nbf", iat - 60)
                addProperty("iat", iat)
                addProperty("exp", iat + 86_400)
                addProperty("identityPublicKey", base64Encoder.encodeToString(keyPair.public.encoded))
                add("extraData", JsonObject().apply(authExtra::toJson))
            }.toString()
            key = keyPair.private
        }.compactSerialization)
    })
}.toString()

fun authJws(keyPair: KeyPair, authJws: String) = JsonObject().apply {
    add("chain", JsonArray().apply {
        val authJsonJwsChain = Streams.parse(JsonReader(StringReader(authJws))).asJsonObject.getJsonArray("chain")
        val authJwtContext = JwtConsumerBuilder().setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT, "ES384").apply { setSkipSignatureVerification() }.build().process(authJsonJwsChain.first().asString)
        add(JsonWebSignature().apply {
            setHeader("alg", "ES384")
            setHeader("x5u", Base64.getEncoder().encodeToString(keyPair.public.encoded))
            payload = JsonObject().apply {
                addProperty("nbf", authJwtContext.joseObjects.first().headers.getStringHeaderValue("nbf"))
                addProperty("exp", authJwtContext.joseObjects.first().headers.getStringHeaderValue("exp"))
                addProperty("certificateAuthority", true)
                addProperty("identityPublicKey", authJwtContext.joseObjects.first().headers.getStringHeaderValue("x5u"))
            }.toString()
            key = keyPair.private
        }.compactSerialization)
        addAll(authJsonJwsChain)
    })
}.toString()

fun parseAuthJws(authJws: String): Triple<Boolean, PublicKey, AuthExtra> {
    val authJwsJsonChain = Streams.parse(JsonReader(StringReader(authJws))).asJsonObject.getJsonArray("chain")
    var verificationKey: PublicKey? = null
    var verified = false
    var authJwsPayloadJson: JsonObject? = null
    for (authJsonJws in authJwsJsonChain) {
        val authJwtConsumerBuilder = JwtConsumerBuilder().setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT, "ES384").apply {
            if (verified && verificationKey != null) setVerificationKey(verificationKey) else verificationKey?.let {
                setVerificationKey(verificationKey)
                if (verificationKey == mojangKey) verified = true
            } ?: setSkipSignatureVerification()
        }
        val authJwtClaims = try {
            authJwtConsumerBuilder.build().processToClaims(authJsonJws.asString)
        } catch (_: InvalidJwtException) {
            verified = false
            authJwtConsumerBuilder.setSkipSignatureVerification()
            authJwtConsumerBuilder.build().processToClaims(authJsonJws.asString)
        }
        authJwsPayloadJson = Streams.parse(JsonReader(StringReader(authJwtClaims.rawJson))).asJsonObject
        verificationKey = generatePublicKey(authJwsPayloadJson.getString("identityPublicKey"))
    }
    val authJwtConsumerBuilder = JwtConsumerBuilder().setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT, "ES384").apply { setVerificationKey(verificationKey) }
    try {
        authJwtConsumerBuilder.build().processToClaims(authJwsJsonChain.first().asString)
    } catch (_: InvalidJwtException) {
        verified = false
    }
    return Triple(verified, verificationKey!!, authJwsPayloadJson!!.getJsonObject("extraData").asAuthExtra)
}

fun userJws(keyPair: KeyPair, user: User) = JsonWebSignature().apply {
    setHeader("alg", "ES384")
    setHeader("x5u", base64Encoder.encodeToString(keyPair.public.encoded))
    payload = JsonObject().apply(user::toJson).toString()
    key = keyPair.private
}.compactSerialization

fun parseUserJws(userJws: String, verificationKey: PublicKey?): Pair<Boolean, User> {
    val userJwtConsumerBuilder = JwtConsumerBuilder().setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT, "ES384")
    verificationKey?.let { userJwtConsumerBuilder.setVerificationKey(verificationKey) } ?: userJwtConsumerBuilder.setSkipSignatureVerification()
    var verified = false
    val userJwtClaims = try {
        userJwtConsumerBuilder.build().processToClaims(userJws).also { verified = verificationKey != null }
    } catch (_: InvalidJwtException) {
        userJwtConsumerBuilder.setSkipSignatureVerification()
        userJwtConsumerBuilder.build().processToClaims(userJws)
    }
    return verified to Streams.parse(JsonReader(StringReader(userJwtClaims.rawJson))).asJsonObject.asUser
}

fun serverToClientHandshakeJws(keyPair: KeyPair, salt: ByteArray) = JsonWebSignature().apply {
    setHeader("alg", "ES384")
    setHeader("x5u", base64Encoder.encodeToString(keyPair.public.encoded))
    setHeader("typ", "JWT")
    payload = JsonObject().apply { addProperty("salt", base64Encoder.encodeToString(salt)) }.toString()
    key = keyPair.private
}.compactSerialization

fun parseServerToClientHandshakeJws(serverToClientHandshakeJws: String): Pair<PublicKey, ByteArray> {
    val jwtContext = JwtConsumerBuilder().setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT, "ES384").apply { setSkipSignatureVerification() }.build().process(serverToClientHandshakeJws)
    return generatePublicKey(jwtContext.joseObjects.first().headers.getStringHeaderValue("x5u")) to base64Decoder.decode(Streams.parse(JsonReader(StringReader(jwtContext.jwtClaims.rawJson))).asJsonObject.getString("salt"))
}

val base64Encoder = Base64.getEncoder()
val base64Decoder = Base64.getDecoder()
private val keyFactory = KeyFactory.getInstance("EC")
val mojangKey = generatePublicKey("MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAE8ELkixyLcwlZryUQcu1TvPOmI2B7vX83ndnWRUaXm74wFfa5f/lwQNTfrLVHa2PmenpGI6JhIMUJaWZrjmMj90NoKNFSNBuKdm8rYiXsfaz3K36x/1U26HpG0ZxK/V1V")
private val keyPairGenerator = KeyPairGenerator.getInstance("EC")

fun generatePublicKey(base64: String) = keyFactory.generatePublic(X509EncodedKeySpec(base64Decoder.decode(base64))) as ECPublicKey

fun generateKeyPair(): KeyPair = keyPairGenerator.apply { initialize(ECGenParameterSpec("secp384r1")) }.generateKeyPair()
