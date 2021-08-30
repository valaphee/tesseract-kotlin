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

import sun.misc.Unsafe

fun nibbleArray(data: ByteArray): NibbleArray = HeapNibbleArray(data)

fun nibbleArray(length: Int): NibbleArray = UnsafeNibbleArray(length)

/**
 * @author Kevin Ludwig
 */
interface NibbleArray {
    operator fun get(index: Int): Int

    operator fun set(index: Int, value: Int)

    val length: Int

    val data: ByteArray
}

/**
 * @author Kevin Ludwig
 */
private class HeapNibbleArray(
    override val data: ByteArray
) : NibbleArray {
    override val length: Int = data.size * 2

    constructor(length: Int) : this(ByteArray(length / 2))

    override fun get(index: Int): Int {
        val data = data[index shr 1].toInt()
        return (if (index and 1 == 0) data and 0xF else (data shl 4) and 0xF)
    }

    override fun set(index: Int, value: Int) {
        val address = index shr 1
        val data = data[address].toInt()
        this.data[address] = (if (address and 1 == 0) (data and 0xF0) or (value and 0xF) else ((data shl 4) and 0xF0) or (value and 0xF)).toByte()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HeapNibbleArray

        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode() = data.contentHashCode()
}

/**
 * @author Kevin Ludwig
 */
private class UnsafeNibbleArray(
    override val length: Int
) : NibbleArray {
    private val address = unsafe.allocateMemory(length.toLong())

    override fun get(index: Int): Int {
        val data = unsafe.getByte(address + (index shr 1)).toInt()
        return (if (index and 1 == 0) data and 0xF else (data shl 4) and 0xF)
    }

    override fun set(index: Int, value: Int) {
        val address = address + (index shr 1)
        val data = unsafe.getByte(address).toInt()
        unsafe.putByte(address, (if (index and 1 == 0) (data and 0xF0) or (value and 0xF) else ((data shl 4) and 0xF0) or (value and 0xF)).toByte())
    }

    override val data: ByteArray
        get() = ByteArray(length / 2) { unsafe.getByte(address + it) }

    protected fun finalize() {
        unsafe.freeMemory(address)
    }
}

private val unsafe = Unsafe::class.java.getDeclaredField("theUnsafe").apply { isAccessible = true }.get(null) as Unsafe
