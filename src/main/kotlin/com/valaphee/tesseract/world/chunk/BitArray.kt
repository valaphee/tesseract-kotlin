/*
 * Copyright (c) 2021, GrieferGames, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk

import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap

/**
 * @author Kevin Ludwig
 */
sealed class BitArray(
    var version: Version,
    var size: Int,
    var data: IntArray
) : Cloneable {
    abstract operator fun get(index: Int): Int

    abstract operator fun set(index: Int, value: Int)

    val empty get() = data.all { it == 0 }

    enum class Version(
        val bitsPerEntry: Int,
        val entriesPerIndex: Int,
        val next: Version?
    ) {
        V16(16, 2, null),
        V8(8, 4, V16),
        V6(6, 5, V8),
        V5(5, 6, V6),
        V4(4, 8, V5),
        V3(3, 10, V4),
        V2(2, 16, V3),
        V1(1, 32, V2);

        val maximumEntryValue = (1 shl bitsPerEntry) - 1

        fun bitArrayDataSize(size: Int): Int {
            val indices = size.toFloat() / entriesPerIndex
            val indicesRounded = indices.toInt()
            return if (indices > indicesRounded) indicesRounded + 1 else indicesRounded
        }

        @JvmOverloads
        fun bitArray(size: Int, data: IntArray = IntArray(bitArrayDataSize(size))) = if (this == V3 || this == V5 || this == V6) PaddedBitArray(this, size, data) else PowerOfTwoBitArray(this, size, data)

        companion object {
            @JvmStatic
            fun byBitsPerEntry(bitsPerEntry: Int): Version {
                var bitsPerEntry0 = bitsPerEntry.toByte()
                var version: Version?
                do version = bitArrayByBitsPerEntry[bitsPerEntry0++] while (version == null)
                return version
            }

            private val bitArrayByBitsPerEntry = Byte2ObjectOpenHashMap<Version>(values().size).apply { values().forEach { this[it.bitsPerEntry.toByte()] = it } }
        }
    }
}

/**
 * @author Kevin Ludwig
 */
internal class PaddedBitArray(
    version: Version,
    size: Int,
    data: IntArray
) : BitArray(version, size, data) {
    override fun get(index: Int): Int {
        val intIndexStart = index / version.entriesPerIndex
        val localBitIndexStart = index % version.entriesPerIndex * version.bitsPerEntry
        return (data[intIndexStart] ushr localBitIndexStart) and version.maximumEntryValue
    }

    override fun set(index: Int, value: Int) {
        val intIndexStart = index / version.entriesPerIndex
        val localBitIndexStart = index % version.entriesPerIndex * version.bitsPerEntry
        data[intIndexStart] = (data[intIndexStart] and (version.maximumEntryValue shl localBitIndexStart).inv()) or ((value and version.maximumEntryValue) shl localBitIndexStart)
    }
}

/**
 * @author Kevin Ludwig
 */
internal class PowerOfTwoBitArray(
    version: Version,
    size: Int,
    data: IntArray
) : BitArray(version, size, data) {
    override fun get(index: Int): Int {
        val bitIndexStart = index * version.bitsPerEntry
        val intIndexStart = bitIndexStart shr 5
        val localBitIndexStart = bitIndexStart and 31
        return (data[intIndexStart] ushr localBitIndexStart) and version.maximumEntryValue
    }

    override fun set(index: Int, value: Int) {
        val bitIndexStart = index * version.bitsPerEntry
        val intIndexStart = bitIndexStart shr 5
        val localBitIndexStart = bitIndexStart and 31
        data[intIndexStart] = (data[intIndexStart] and (version.maximumEntryValue shl localBitIndexStart).inv()) or ((value and version.maximumEntryValue) shl localBitIndexStart)
    }
}
