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
