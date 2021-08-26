/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.util

import java.security.Key
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.KeyAgreement

private val keyFactory = KeyFactory.getInstance("EC")
private val base64Decoder = Base64.getDecoder()
val MojangRootKey = generatePublicKey("MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAE8ELkixyLcwlZryUQcu1TvPOmI2B7vX83ndnWRUaXm74wFfa5f/lwQNTfrLVHa2PmenpGI6JhIMUJaWZrjmMj90NoKNFSNBuKdm8rYiXsfaz3K36x/1U26HpG0ZxK/V1V")
private val keyPairGenerator = KeyPairGenerator.getInstance("EC")

fun generatePublicKey(base64: String?) = keyFactory.generatePublic(X509EncodedKeySpec(base64Decoder.decode(base64))) as ECPublicKey

fun generateKeyPair(): KeyPair = keyPairGenerator.apply { initialize(ECGenParameterSpec("secp384r1")) }.generateKeyPair()

fun generateSecret(serverPrivateKey: Key, clientPublicKey: Key): ByteArray = KeyAgreement.getInstance("ECDH").apply {
    init(serverPrivateKey)
    doPhase(clientPublicKey, true)
}.generateSecret()
