/*
 * Copyright (c) 2021, GrieferGames, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.util

import io.netty.buffer.ByteBuf
import java.nio.charset.StandardCharsets

/**
 * @author Kevin Ludwig
 */
class LittleEndianVarIntByteBufInputStream : LittleEndianByteBufInputStream {
    constructor(buffer: ByteBuf) : super(buffer)

    constructor(buffer: ByteBuf, length: Int) : super(buffer, length)

    constructor(buffer: ByteBuf, releaseOnClose: Boolean) : super(buffer, releaseOnClose)

    constructor(buffer: ByteBuf, length: Int, releaseOnClose: Boolean) : super(buffer, length, releaseOnClose)

    override fun readInt(): Int {
        val value = readVarUInt()
        return (value ushr 1) xor -(value and 1)
    }

    override fun readLong(): Long {
        var value: Long = 0
        var shift = 0
        while (shift <= 70) {
            val head = readByte().toInt()
            value = value or ((head and 0x7F).toLong() shl shift)
            if (head and 0x80 == 0) return (value ushr 1) xor -(value and 1)
            shift += 7
        }
        throw ArithmeticException("VarLong wider than 70-bit")
    }

    override fun readUTF(): String {
        val length = readVarUInt()
        check(Short.MAX_VALUE >= length) { "Maximum length of ${Short.MAX_VALUE} exceeded" }
        val bytes = ByteArray(length)
        readFully(bytes)
        return String(bytes, StandardCharsets.UTF_8)
    }

    private fun readVarUInt(): Int {
        var value = 0
        var shift = 0
        while (shift <= 35) {
            val head = readByte().toInt()
            value = value or ((head and 0x7F) shl shift)
            if (head and 0x80 == 0) return value
            shift += 7
        }
        throw ArithmeticException("VarInt wider than 35-bit")
    }
}
