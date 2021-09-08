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

package com.valaphee.tesseract.util.nbt

import java.io.DataInput
import java.io.DataOutput
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

/**
 * @author Kevin Ludwig
 */
internal class StringTagImpl(
    override var name: String,
    private var value: String? = null
) : ArrayTag {
    override val type get() = TagType.String

    override fun toByteArray() = value!!.toByteArray(StandardCharsets.UTF_8)

    override fun toIntArray() = ByteBuffer.wrap(toByteArray()).asIntBuffer().array()

    override fun toLongArray() = ByteBuffer.wrap(toByteArray()).asLongBuffer().array()

    override fun valueToString() = value!!

    override fun read(input: DataInput, depth: Int, remainingBytes: IntArray) {
        value = input.readUTF()
    }

    override fun write(output: DataOutput) {
        output.writeUTF(value!!)
    }

    override fun print(string: StringBuilder) {
        string.append('"')
        val escapedString = StringBuffer()
        val matcher = escapePattern.matcher(value!!)
        while (matcher.find()) matcher.appendReplacement(escapedString, escapes[matcher.group()])
        matcher.appendTail(escapedString)
        string.append(escapedString)
        string.append('"')
    }

    override fun toString() = StringBuilder().apply(this::print).toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StringTagImpl

        if (value != other.value) return false

        return true
    }

    override fun hashCode() = value?.hashCode() ?: 0

    companion object {
        private val escapePattern = Pattern.compile("[\\\\\b\t\n\r\"]")
        private val escapes = mutableMapOf(
            "\b" to "\\\\b",
            "\t" to "\\\\t",
            "\n" to "\\\\n",
            "\r" to "\\\\r",
            "\"" to "\\\\\"",
            "'" to "\\\\'",
            "\\" to "\\\\\\\\"
        )
    }
}
