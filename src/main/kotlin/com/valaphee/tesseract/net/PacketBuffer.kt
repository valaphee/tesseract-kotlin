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

package com.valaphee.tesseract.net

import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.data.block.BlockState
import com.valaphee.tesseract.data.item.Item
import com.valaphee.tesseract.util.ByteBufWrapper
import com.valaphee.tesseract.util.Int2ObjectOpenHashBiMap
import com.valaphee.tesseract.util.LittleEndianByteBufInputStream
import com.valaphee.tesseract.util.LittleEndianByteBufOutputStream
import com.valaphee.tesseract.util.LittleEndianVarIntByteBufInputStream
import com.valaphee.tesseract.util.LittleEndianVarIntByteBufOutputStream
import com.valaphee.tesseract.util.nbt.NbtInputStream
import com.valaphee.tesseract.util.nbt.NbtOutputStream
import io.netty.buffer.ByteBuf
import io.netty.util.AsciiString
import java.nio.charset.StandardCharsets
import java.util.EnumSet
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
class PacketBuffer(
    buffer: ByteBuf,
    private val local: Boolean = false,
    blockStates: Int2ObjectOpenHashBiMap<BlockState>? = null,
    items: Int2ObjectOpenHashBiMap<Item>? = null
) : ByteBufWrapper(buffer) {
    lateinit var blockStates: Int2ObjectOpenHashBiMap<BlockState>
    lateinit var items: Int2ObjectOpenHashBiMap<Item>

    init {
        blockStates?.let { this.blockStates = it }
        items?.let { this.items = it }
    }

    inline fun <reified T : Enum<T>> readByteFlags(): Collection<T> {
        val flagsValue = readByte().toInt()
        return EnumSet.noneOf(T::class.java).apply { enumValues<T>().filter { (flagsValue and (1 shl it.ordinal)) != 0 }.forEach { add(it) } }
    }

    fun <T : Enum<T>> writeByteFlags(flags: Collection<T>) = writeByte(flags.map { 1 shl it.ordinal }.fold(0) { flagsValue, flagValue -> flagsValue or flagValue })

    inline fun <reified T : Enum<T>> readShortLEFlags(): Collection<T> {
        val flagsValue = readUnsignedShortLE()
        return EnumSet.noneOf(T::class.java).apply { enumValues<T>().filter { (flagsValue and (1 shl it.ordinal)) != 0 }.forEach { add(it) } }
    }

    fun <T : Enum<T>> writeShortLEFlags(flags: Collection<T>) = writeShortLE(flags.map { 1 shl it.ordinal }.fold(0) { flagsValue, flagValue -> flagsValue or flagValue })

    fun readUuid() = UUID(readLongLE(), readLongLE())

    fun writeUuid(value: UUID) {
        writeLongLE(value.mostSignificantBits)
        writeLongLE(value.leastSignificantBits)
    }

    fun readString16(): String {
        val length = readUnsignedShortLE()
        check(length <= Short.MAX_VALUE) { "Maximum length of ${Short.MAX_VALUE} exceeded" }
        val bytes = ByteArray(length)
        readBytes(bytes)
        return String(bytes, StandardCharsets.UTF_8)
    }

    fun writeString16(value: String) {
        val bytes = value.toByteArray(StandardCharsets.UTF_8)
        writeShortLE(bytes.size)
        writeBytes(bytes)
    }

    fun readAsciiStringLe(maximumLength: Int = Int.MAX_VALUE): AsciiString {
        val length = readIntLE()
        check(length <= maximumLength) { "Maximum length of $maximumLength exceeded" }
        val bytes = ByteArray(length)
        readBytes(bytes)
        return AsciiString(bytes)
    }

    fun writeAsciiStringLe(value: AsciiString) {
        writeIntLE(value.length)
        writeBytes(value.toByteArray())
    }

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

    fun setMaximumLengthVarUInt(index: Int, value: Int) {
        setBytes(
            index, byteArrayOf(
                (value and 0x7F or 0x80).toByte(),
                (value ushr 7 and 0x7F or 0x80).toByte(),
                (value ushr 14 and 0x7F or 0x80).toByte(),
                (value ushr 21 and 0x7F or 0x80).toByte(),
                (value ushr 28 and 0x7F).toByte()
            )
        )
    }

    inline fun <reified T : Enum<T>> readVarUIntFlags(): Collection<T> {
        val flagsValue = readVarUInt()
        return EnumSet.noneOf(T::class.java).apply { enumValues<T>().filter { (flagsValue and (1 shl it.ordinal)) != 0 }.forEach { add(it) } }
    }

    fun <T : Enum<T>> writeVarUIntFlags(flags: Collection<T>) = writeVarUInt(flags.map { 1 shl it.ordinal }.fold(0) { flagsValue, flagValue -> flagsValue or flagValue })

    fun readVarInt() = if (local) readIntLE() else {
        val value = readVarUInt()
        (value ushr 1) xor -(value and 1)
    }

    fun writeVarInt(value: Int) {
        if (local) writeIntLE(value) else writeVarUInt((value shl 1) xor (value shr 31))
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

    inline fun <reified T : Enum<T>> readVarULongFlags(): Collection<T> {
        val flagsValue = readVarULong()
        return EnumSet.noneOf(T::class.java).apply { enumValues<T>().filter { (flagsValue and (1L shl it.ordinal)) != 0L }.forEach { add(it) } }
    }

    fun <T : Enum<T>> writeVarULongFlags(flags: Collection<T>) = writeVarULong(flags.map { 1L shl it.ordinal }.fold(0) { flagsValue, flagValue -> flagsValue or flagValue })

    fun readVarLong(): Long {
        val value = readVarULong()
        return (value ushr 1) xor -(value and 1)
    }

    fun writeVarLong(value: Long) {
        writeVarULong((value shl 1) xor (value shr 63))
    }

    inline fun <reified T : Enum<T>> readVarLongFlags(): Collection<T> {
        val flagsValue = readVarLong()
        return EnumSet.noneOf(T::class.java).apply { enumValues<T>().filter { (flagsValue and (1L shl it.ordinal)) != 0L }.forEach { add(it) } }
    }

    fun <T : Enum<T>> writeVarLongFlags(flags: Collection<T>) = writeVarLong(flags.map { 1L shl it.ordinal }.fold(0) { flagsValue, flagValue -> flagsValue or flagValue })

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

    fun readInt3() = Int3(readVarInt(), readVarInt(), readVarInt())

    fun writeInt3(value: Int3) {
        writeVarInt(value.x)
        writeVarInt(value.y)
        writeVarInt(value.z)
    }

    fun readInt3UnsignedY() = Int3(readVarInt(), readVarUInt(), readVarInt())

    fun writeInt3UnsignedY(value: Int3) {
        writeVarInt(value.x)
        writeVarUInt(value.y)
        writeVarInt(value.z)
    }

    fun readAngle() = readByte() * 360 / 256f

    fun writeAngle(value: Float) {
        writeByte((value * 256 / 360).toInt())
    }

    fun readAngle2() = Float2(readAngle(), readAngle())

    fun writeAngle2(value: Float2) {
        writeAngle(value.x)
        writeAngle(value.y)
    }

    fun readFloat2() = Float2(readFloatLE(), readFloatLE())

    fun writeFloat2(value: Float2) {
        writeFloatLE(value.x)
        writeFloatLE(value.y)
    }

    fun readFloat3() = Float3(readFloatLE(), readFloatLE(), readFloatLE())

    fun writeFloat3(value: Float3) {
        writeFloatLE(value.x)
        writeFloatLE(value.y)
        writeFloatLE(value.z)
    }

    fun toNbtOutputStream() = NbtOutputStream(if (local) LittleEndianByteBufOutputStream(buffer) else LittleEndianVarIntByteBufOutputStream(buffer))

    fun toNbtInputStream() = NbtInputStream(if (local) LittleEndianByteBufInputStream(buffer) else LittleEndianVarIntByteBufInputStream(buffer))

    companion object {
        const val MaximumVarUIntLength = 5
    }
}
