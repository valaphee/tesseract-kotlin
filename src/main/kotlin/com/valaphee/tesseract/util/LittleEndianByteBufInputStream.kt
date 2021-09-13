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
import io.netty.util.internal.StringUtil
import java.io.DataInput
import java.io.EOFException
import java.io.InputStream
import java.nio.charset.StandardCharsets

/**
 * @author Kevin Ludwig
 */
open class LittleEndianByteBufInputStream constructor(
    private val buffer: ByteBuf,
    length: Int = buffer.readableBytes(),
    private val releaseOnClose: Boolean = false
) : InputStream(), DataInput {
    private val startIndex = buffer.readerIndex()
    private val endIndex = startIndex + length
    private var open = true
    private var lineBuffer: StringBuilder? = null

    init {
        buffer.markReaderIndex()
    }

    constructor(buffer: ByteBuf, releaseOnClose: Boolean) : this(buffer, buffer.readableBytes(), releaseOnClose)

    fun readBytes() = buffer.readerIndex() - startIndex

    override fun readFully(value: ByteArray) = readFully(value, 0, value.size)

    override fun readFully(value: ByteArray, offset: Int, length: Int) {
        checkAvailable(length)
        buffer.readBytes(value, offset, length)
    }

    override fun skipBytes(length: Int) = available().coerceAtMost(length).also { buffer.skipBytes(it) }

    override fun readBoolean(): Boolean {
        checkAvailable(1)
        return read() != 0
    }

    override fun readByte(): Byte {
        if (available() == 0) throw EOFException()
        return buffer.readByte()
    }

    override fun readUnsignedByte(): Int {
        if (available() == 0) throw EOFException()
        return buffer.readUnsignedByte().toInt()
    }

    override fun readShort(): Short {
        checkAvailable(2)
        return buffer.readShortLE()
    }

    override fun readUnsignedShort(): Int {
        checkAvailable(2)
        return buffer.readUnsignedShortLE()
    }

    override fun readChar(): Char {
        checkAvailable(2)
        return buffer.readShortLE().toChar()
    }

    override fun readInt(): Int {
        checkAvailable(4)
        return buffer.readIntLE()
    }

    override fun readLong(): Long {
        checkAvailable(8)
        return buffer.readLongLE()
    }

    override fun readFloat(): Float {
        checkAvailable(4)
        return buffer.readFloatLE()
    }

    override fun readDouble(): Double {
        checkAvailable(8)
        return buffer.readDoubleLE()
    }

    override fun readLine(): String? {
        var available = available()
        if (available == 0) return null
        if (lineBuffer != null) lineBuffer!!.setLength(0)
        loop@ do {
            val character = buffer.readUnsignedByte().toInt().toChar()
            available--
            when (character) {
                '\n' -> break@loop
                '\r' -> {
                    if (available > 0 && '\n' == buffer.getUnsignedByte(buffer.readerIndex()).toInt().toChar()) buffer.skipBytes(1)
                    break@loop
                }
                else -> {
                    if (lineBuffer == null) lineBuffer = StringBuilder()
                    lineBuffer!!.append(character)
                }
            }
        } while (0 < available)
        return if (null != lineBuffer && lineBuffer!!.isNotEmpty()) lineBuffer.toString() else StringUtil.EMPTY_STRING
    }

    override fun readUTF(): String {
        val length = readUnsignedShort()
        check(length <= Short.MAX_VALUE) { "Maximum length of ${Short.MAX_VALUE} exceeded" }
        val bytes = ByteArray(length)
        readFully(bytes)
        return String(bytes, StandardCharsets.UTF_8)
    }

    override fun read() = if (0 == available()) -1 else buffer.readByte().toInt() and 0xFF

    override fun read(value: ByteArray, offset: Int, length: Int): Int {
        val available = available()
        if (0 == available) return -1
        return available.coerceAtMost(length).also { buffer.readBytes(value, offset, it) }
    }

    override fun skip(length: Long): Long = if (Int.MAX_VALUE < length) skipBytes(Int.MAX_VALUE).toLong() else skipBytes(length.toInt()).toLong()

    override fun available(): Int = endIndex - buffer.readerIndex()

    override fun close() {
        try {
            super.close()
        } finally {
            if (releaseOnClose && open) {
                open = false
                buffer.release()
            }
        }
    }

    @Synchronized
    override fun mark(readLimit: Int) {
        buffer.markReaderIndex()
    }

    @Synchronized
    override fun reset() {
        buffer.resetReaderIndex()
    }

    override fun markSupported() = true

    @Throws(EOFException::class)
    private fun checkAvailable(length: Int) {
        if (length < 0) throw IndexOutOfBoundsException("Tried to read $length bytes extra")
        val available = available()
        if (length > available) throw EOFException("Tried to read $length bytes extra, maximum is $available")
    }
}
