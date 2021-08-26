/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.util

import io.netty.buffer.ByteBuf
import java.io.Closeable
import java.security.MessageDigest

/**
 * @author Kevin Ludwig
 */
interface Hasher : Closeable {
    fun update(buffer: ByteBuf)

    fun digest(): ByteArray
}

fun sha256Hasher(): Hasher = JavaSha256Hasher()

/**
 * @author Kevin Ludwig
 */
private class JavaSha256Hasher : Hasher {
    private var digest = MessageDigest.getInstance("SHA-256")

    override fun update(buffer: ByteBuf) {
        val data: ByteArray
        if (buffer.isDirect) {
            data = ByteArray(buffer.readableBytes())
            buffer.getBytes(0, data, 0, buffer.readableBytes())
        } else data = buffer.array()
        digest.update(data)
    }

    override fun digest() = digest.digest()

    override fun close() {}
}

/**
 * @author Kevin Ludwig
 */
private class MbedTlsSha256Hasher : Hasher {
    private var mbedTlsSha256Context = MbedTlsSha256HasherImpl.init()

    override fun update(buffer: ByteBuf) {
        MbedTlsSha256HasherImpl.update(mbedTlsSha256Context, buffer.memoryAddress() + buffer.readerIndex(), buffer.readableBytes())
        buffer.readerIndex(buffer.writerIndex())
    }

    override fun digest() = MbedTlsSha256HasherImpl.digest(mbedTlsSha256Context)

    override fun close() {
        MbedTlsSha256HasherImpl.free(mbedTlsSha256Context)
        mbedTlsSha256Context = 0
    }
}
