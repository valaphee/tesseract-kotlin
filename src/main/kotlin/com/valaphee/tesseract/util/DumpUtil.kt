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
import io.netty.buffer.ByteBufUtil
import java.util.Formatter

fun ByteBuf.dump(displayedOffset: Long, offset: Int, length: Int) = ByteBufUtil.getBytes(this).dump(displayedOffset, offset, length)

fun ByteArray.dump(displayedOffset: Long, offset: Int, length: Int): String {
    var _displayedOffset = displayedOffset
    return StringBuilder().apply {
        Formatter(this).use {
            appendLine("  Offset: 00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F")
            var i = offset
            while (i < length) {
                it.format("%08X", _displayedOffset)
                append(": ")
                run {
                    var j = i
                    val k = i + 16
                    while (j < k) {
                        if (j < length) it.format("%02X", this[j]) else append("  ")
                        append(' ')
                        j++
                    }
                }
                append(' ')
                for (j in i until i + 16) {
                    if (j < length) {
                        val char = this[j]
                        if (!char.isISOControl()) append(char) else append('.')
                    } else append(" ")
                }
                appendLine()
                i += 16
                _displayedOffset += 16
            }
        }
    }.toString()
}
