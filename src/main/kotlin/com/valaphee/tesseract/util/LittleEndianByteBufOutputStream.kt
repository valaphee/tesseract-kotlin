/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
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
