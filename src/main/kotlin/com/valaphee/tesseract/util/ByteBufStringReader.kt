/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
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
