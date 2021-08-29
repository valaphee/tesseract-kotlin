/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
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

    companion object {
        private val escapePattern = Pattern.compile("[\\\\\b\t\n\r\"]")
        private val escapes = mutableMapOf<String, String>(
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
