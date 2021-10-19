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
import java.util.Formatter

fun ByteBuf.dump(displayedOffset: Long, offset: Int, length: Int) = if (isDirect) ByteArray(length).also { getBytes(offset, it, 0, length) }.dump(displayedOffset + offset, 0, length) else array().dump(displayedOffset, offset, length)

fun ByteArray.dump(displayedOffset: Long, offset: Int, length: Int): String {
    var displayedOffset = displayedOffset
    val string = StringBuilder()
    Formatter(string).use {
        string.appendLine("  Offset: 00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F")
        var i = offset
        while (i < length) {
            it.format("%08X", displayedOffset)
            string.append(": ")
            run {
                var j = i
                val k = i + 16
                while (j < k) {
                    if (j < length) it.format("%02X", this[j]) else string.append("  ")
                    string.append(' ')
                    j++
                }
            }
            string.append(' ')
            for (j in i until i + 16) {
                if (j < length) {
                    val char = this[j].toChar()
                    if (!char.isISOControl()) string.append(char) else string.append('.')
                } else string.append(" ")
            }
            string.appendLine()
            i += 16
            displayedOffset += 16
        }
    }
    return string.toString()
}
