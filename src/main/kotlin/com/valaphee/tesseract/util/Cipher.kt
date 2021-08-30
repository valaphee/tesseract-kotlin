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
import javax.crypto.ShortBufferException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * @author Kevin Ludwig
 */
interface Cipher : Closeable {
    fun cipher(`in`: ByteBuf, out: ByteBuf)
}

fun aesCipher(encrypt: Boolean, key: ByteArray, iv: ByteArray, gcm: Boolean): Cipher = JavaAesCipher(encrypt, key, iv, gcm)

/**
 * @author Kevin Ludwig
 */
private class JavaAesCipher(
    encrypt: Boolean,
    key: ByteArray,
    iv: ByteArray,
    gcm: Boolean
) : Cipher {
    private var cipher = javax.crypto.Cipher.getInstance(if (gcm) "AES/CTR/NoPadding" else "AES/CFB8/NoPadding").apply { init(if (encrypt) javax.crypto.Cipher.ENCRYPT_MODE else javax.crypto.Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(iv)) }

    @Throws(ShortBufferException::class)
    override fun cipher(`in`: ByteBuf, out: ByteBuf) {
        val readableBytes = `in`.readableBytes()
        var heapIn = heapInLocal.get()
        if (heapIn.size < readableBytes) heapInLocal.set(ByteArray(readableBytes).also { heapIn = it })
        `in`.readBytes(heapIn, 0, readableBytes)
        var heapOut = heapOutLocal.get()
        val outputSize = cipher.getOutputSize(readableBytes)
        if (heapOut.size < outputSize) heapOutLocal.set(ByteArray(outputSize).also { heapOut = it })
        out.writeBytes(heapOut, 0, cipher.update(heapIn, 0, readableBytes, heapOut))
    }

    override fun close() {}

    companion object {
        private val heapInLocal = ThreadLocal.withInitial { ByteArray(0) }
        private val heapOutLocal = ThreadLocal.withInitial { ByteArray(0) }
    }
}

/**
 * @author Kevin Ludwig
 */
internal class MbedTlsAesCipher(
    encrypt: Boolean,
    key: ByteArray,
    iv: ByteArray
) : Cipher {
    private var cipherContext = MbedTlsAesCipherImpl.init(encrypt, key, iv)

    override fun cipher(`in`: ByteBuf, out: ByteBuf) {
        val length = `in`.readableBytes()
        if (length <= 0) return
        out.ensureWritable(length)
        MbedTlsAesCipherImpl.cipher(cipherContext, `in`.memoryAddress() + `in`.readerIndex(), out.memoryAddress() + out.writerIndex(), length)
        `in`.readerIndex(`in`.writerIndex())
        out.writerIndex(out.writerIndex() + length)
    }

    fun cipher(`in`: Long, out: Long, length: Int) = MbedTlsAesCipherImpl.cipher(cipherContext, `in`, out, length)

    override fun close() {
        MbedTlsAesCipherImpl.free(cipherContext)
        cipherContext = 0
    }
}
