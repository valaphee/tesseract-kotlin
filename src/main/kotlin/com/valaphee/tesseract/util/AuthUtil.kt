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
import org.jose4j.jwt.JwtClaims
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
                addProperty("exp", (System.currentTimeMillis() / 1_000L) + 31_536L)
                add("extraData", JsonObject().apply(authExtra::toJson))
                addProperty("identityPublicKey", base64Encoder.encodeToString(keyPair.public.encoded))
                addProperty("nbf", (System.currentTimeMillis() / 1_000L) - 86_400L)
            }.toString()
            key = keyPair.private
        }.compactSerialization)
    })
}.toString()

fun parseAuthJws(authJws: String): Triple<Boolean, PublicKey, AuthExtra> {
    var verified = true
    var verifiedMojang = false
    val authJwsJsonChain = Streams.parse(JsonReader(StringReader(authJws))).asJsonObject.getJsonArray("chain")
    var verificationKey: PublicKey? = null
    var authJwsPayloadJson: JsonObject? = null
    for (authJsonJws in authJwsJsonChain) {
        val authJwtConsumerBuilder = JwtConsumerBuilder().setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT, "ES384")
        var authJwtClaims: JwtClaims
        try {
            if (!verifiedMojang) {
                try {
                    authJwtConsumerBuilder.setVerificationKey(mojangKey)
                    authJwtConsumerBuilder.build().process(authJsonJws.asString)
                    verifiedMojang = true
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
        authJwsPayloadJson = Streams.parse(JsonReader(StringReader(authJwtClaims.rawJson))).asJsonObject
        verificationKey = generatePublicKey(authJwsPayloadJson.getString("identityPublicKey"))
    }
    return Triple((verified && verifiedMojang), verificationKey!!, authJwsPayloadJson!!.getJsonObject("extraData").asAuthExtra)
}

fun userJws(keyPair: KeyPair, user: User) = JsonWebSignature().apply {
    setHeader("alg", "ES384")
    setHeader("x5u", base64Encoder.encodeToString(keyPair.public.encoded))
    payload = JsonObject().apply(user::toJson).toString()
    key = keyPair.private
}.compactSerialization

fun parseUserJws(userJws: String, verificationKey: PublicKey?): Pair<Boolean, User> {
    var verified = verificationKey != null
    val userJwtConsumerBuilder = JwtConsumerBuilder().setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT, "ES384")
    verificationKey?.let { userJwtConsumerBuilder.setVerificationKey(verificationKey) } ?: userJwtConsumerBuilder.setSkipSignatureVerification()
    var userJwtClaims: JwtClaims
    try {
        userJwtClaims = userJwtConsumerBuilder.build().processToClaims(userJws)
    } catch (_: InvalidJwtException) {
        verified = false
        userJwtConsumerBuilder.setSkipSignatureVerification()
        userJwtClaims = userJwtConsumerBuilder.build().processToClaims(userJws)
    }
    return verified to Streams.parse(JsonReader(StringReader(userJwtClaims.rawJson))).asJsonObject.asUser
}

private val base64Encoder = Base64.getEncoder()
private val base64Decoder = Base64.getDecoder()
private val keyFactory = KeyFactory.getInstance("EC")
private val mojangKey = generatePublicKey("MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAE8ELkixyLcwlZryUQcu1TvPOmI2B7vX83ndnWRUaXm74wFfa5f/lwQNTfrLVHa2PmenpGI6JhIMUJaWZrjmMj90NoKNFSNBuKdm8rYiXsfaz3K36x/1U26HpG0ZxK/V1V")
private val keyPairGenerator = KeyPairGenerator.getInstance("EC")

fun generatePublicKey(base64: String?) = keyFactory.generatePublic(X509EncodedKeySpec(base64Decoder.decode(base64))) as ECPublicKey

fun generateKeyPair(): KeyPair = keyPairGenerator.apply { initialize(ECGenParameterSpec("secp384r1")) }.generateKeyPair()
