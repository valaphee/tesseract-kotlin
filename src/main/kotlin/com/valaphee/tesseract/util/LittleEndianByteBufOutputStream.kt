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
import java.io.DataOutput
import java.io.OutputStream
import java.nio.charset.StandardCharsets

/**
 * @author Kevin Ludwig
 */
open class LittleEndianByteBufOutputStream(
    private val buffer: ByteBuf
) : OutputStream(), DataOutput {
    private val startIndex = buffer.writerIndex()

    fun writtenBytes() = buffer.writerIndex() - startIndex

    override fun write(value: Int) {
        buffer.writeByte(value)
    }

    override fun write(value: ByteArray) {
        buffer.writeBytes(value)
    }

    override fun write(value: ByteArray, offset: Int, length: Int) {
        if (length != 0) buffer.writeBytes(value, offset, length)
    }

    override fun writeBoolean(value: Boolean) {
        buffer.writeBoolean(value)
    }

    override fun writeByte(value: Int) {
        buffer.writeByte(value)
    }

    override fun writeShort(value: Int) {
        buffer.writeShortLE(value)
    }

    override fun writeChar(value: Int) {
        buffer.writeShortLE(value)
    }

    override fun writeInt(value: Int) {
        buffer.writeIntLE(value)
    }

    override fun writeLong(value: Long) {
        buffer.writeLongLE(value)
    }

    override fun writeFloat(value: Float) {
        buffer.writeFloatLE(value)
    }

    override fun writeDouble(value: Double) {
        buffer.writeDoubleLE(value)
    }

    override fun writeBytes(value: String) {
        buffer.writeCharSequence(value, StandardCharsets.US_ASCII)
    }

    override fun writeChars(value: String) {
        value.indices.forEach { buffer.writeChar(value[it].toInt()) }
    }

    override fun writeUTF(value: String) {
        value.toByteArray(StandardCharsets.UTF_8).run {
            writeShort(size)
            write(this)
        }
    }

    fun buffer() = buffer
}
