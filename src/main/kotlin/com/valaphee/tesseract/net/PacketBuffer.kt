/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net

import com.valaphee.foundry.math.Double3
import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.util.ByteBufWrapper
import io.netty.buffer.ByteBuf
import java.nio.charset.StandardCharsets

/**
 * @author Kevin Ludwig
 */
class PacketBuffer(
    buffer: ByteBuf
) : ByteBufWrapper(buffer) {
    fun readVarUInt(): Int {
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

    fun writeVarUInt(value: Int) {
        @Suppress("NAME_SHADOWING") var value = value
        while (true) {
            if (value and 0x7F.inv() == 0) {
                writeByte(value)
                return
            } else {
                writeByte((value and 0x7F) or 0x80)
                value = value ushr 7
            }
        }
    }

    fun readVarInt(): Int {
        val value = readVarUInt()
        return (value ushr 1) xor -(value and 1)
    }

    fun writeVarInt(value: Int) {
        writeVarUInt((value shl 1) xor (value shr 31))
    }

    fun readVarULong(): Long {
        var value: Long = 0
        var shift = 0
        while (shift <= 70) {
            val head = readByte().toInt()
            value = value or ((head and 0x7F).toLong() shl shift)
            if (head and 0x80 == 0) return value
            shift += 7
        }
        throw ArithmeticException("VarLong wider than 70-bit")
    }

    fun writeVarULong(value: Long) {
        @Suppress("NAME_SHADOWING") var value = value
        while (true) {
            if (value and 0x7FL.inv() == 0L) {
                writeByte(value.toInt())
                return
            } else {
                writeByte((value.toInt() and 0x7F) or 0x80)
                value = value ushr 7
            }
        }
    }

    fun readVarLong(): Long {
        val value = readVarULong()
        return (value ushr 1) xor -(value and 1)
    }

    fun writeVarLong(value: Long) {
        writeVarULong((value shl 1) xor (value shr 63))
    }

    fun readByteArray(maximumLength: Int = Short.MAX_VALUE.toInt()): ByteArray {
        val length = readVarUInt()
        check(length <= maximumLength) { "Maximum length of $maximumLength exceeded" }
        val bytes = ByteArray(length)
        readBytes(bytes)
        return bytes
    }

    fun readByteArrayOfExpectedLength(expectedLength: Int): ByteArray {
        val length = readVarUInt()
        check(length == expectedLength) { "Expected length of $expectedLength, was $length" }
        val bytes = ByteArray(length)
        readBytes(bytes)
        return bytes
    }

    fun writeByteArray(value: ByteArray) {
        writeVarUInt(value.size)
        writeBytes(value)
    }

    fun readString(maximumLength: Int = Int.MAX_VALUE) = String(readByteArray(maximumLength), StandardCharsets.UTF_8)

    fun writeString(value: String) {
        writeByteArray(value.toByteArray(StandardCharsets.UTF_8))
    }

    fun readAngle() = readByte().toFloat() * 360 / 256

    fun writeAngle(value: Float) {
        writeByte((value * 256 / 360).toInt())
    }

    fun readAngle2() = Float2(readAngle(), readAngle())

    fun writeAngle2(value: Float2) {
        writeAngle(value.x)
        writeAngle(value.y)
    }

    fun readFloat3() = Float3(readFloat(), readFloat(), readFloat())

    fun writeFloat3(value: Float3) {
        writeFloat(value.x)
        writeFloat(value.y)
        writeFloat(value.z)
    }

    fun readDouble3() = Double3(readDouble(), readDouble(), readDouble())

    fun writeDouble3(value: Double3) {
        writeDouble(value.x)
        writeDouble(value.y)
        writeDouble(value.z)
    }
}
