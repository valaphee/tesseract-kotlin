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
import java.io.Reader
import java.nio.CharBuffer
import java.nio.charset.StandardCharsets

/**
 * @author Kevin Ludwig
 */
class ByteBufStringReader constructor(
    private var buffer: ByteBuf,
    length: Int,
    private val releaseOnClose: Boolean = false
) : Reader() {
    private val startIndex = buffer.readerIndex()
    private val endIndex = startIndex + length
    private var open = true

    init {
        buffer.markReaderIndex()
    }

    fun readBytes() = buffer.readerIndex() - startIndex

    override fun read(value: CharArray, offset: Int, length: Int): Int {
        val available = available()
        if (available == 0) return -1
        return available.coerceAtMost(length).also {
            val byteBuffer = ByteArray(it)
            buffer.readBytes(byteBuffer)
            CharBuffer.wrap(value, offset, it).put(String(byteBuffer, StandardCharsets.UTF_8))
        }
    }

    override fun markSupported() = true

    override fun mark(readLimit: Int) {
        buffer.markReaderIndex()
    }

    override fun reset() {
        buffer.resetReaderIndex()
    }

    override fun close() {
        if (releaseOnClose && open) {
            open = false
            buffer.release()
        }
    }

    private fun available() = endIndex - buffer.readerIndex()
}
