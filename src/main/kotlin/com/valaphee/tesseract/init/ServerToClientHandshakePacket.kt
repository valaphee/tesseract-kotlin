/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.init

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import java.security.Key
import java.util.Base64

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Clientbound)
data class ServerToClientHandshakePacket(
    var serverPublicKey: Key,
    val serverPrivateKey: Key,
    var clientSalt: ByteArray
) : Packet {
    override val id get() = 0x03

    override fun write(buffer: PacketBuffer, version: Int) {
        val jwtClaims = JwtClaims()
        jwtClaims.setClaim("salt", base64Encoder.encodeToString(clientSalt))
        val jws = JsonWebSignature()
        jws.setHeader("alg", "ES384")
        jws.setHeader("x5u", base64Encoder.encodeToString(serverPublicKey.encoded))
        jws.payload = jwtClaims.toJson()
        jws.key = serverPrivateKey
        buffer.writeString(jws.compactSerialization)
    }

    override fun handle(handler: PacketHandler) = handler.serverToClientHandshake(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServerToClientHandshakePacket

        if (serverPublicKey != other.serverPublicKey) return false
        if (serverPrivateKey != other.serverPrivateKey) return false
        if (!clientSalt.contentEquals(other.clientSalt)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = serverPublicKey.hashCode()
        result = 31 * result + serverPrivateKey.hashCode()
        result = 31 * result + clientSalt.contentHashCode()
        return result
    }

    companion object {
        private val base64Encoder: Base64.Encoder = Base64.getEncoder()
    }
}
