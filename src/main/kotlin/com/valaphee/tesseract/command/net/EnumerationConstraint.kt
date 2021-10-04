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

package com.valaphee.tesseract.command.net

import com.valaphee.tesseract.net.PacketBuffer

/**
 * @author Kevin Ludwig
 */
data class EnumerationConstraint(
    val value: String,
    val enumeration: Enumeration,
    val constraints: Array<Constraint>
) {
    enum class Constraint {
        CheatsEnabled, OperatorPermissions, HostPermissions, Unknown3
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EnumerationConstraint

        if (value != other.value) return false
        if (enumeration != other.enumeration) return false
        if (!constraints.contentEquals(other.constraints)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + enumeration.hashCode()
        result = 31 * result + constraints.contentHashCode()
        return result
    }
}

fun PacketBuffer.readEnumerationConstraint(values: Array<String>, enumerations: Array<Enumeration>) = EnumerationConstraint(values[buffer.readIntLE()], enumerations[buffer.readIntLE()], Array(readVarUInt()) { EnumerationConstraint.Constraint.values()[buffer.readByte().toInt()] })

fun PacketBuffer.writeEnumerationConstraint(value: EnumerationConstraint, values: Collection<String>, enumerations: Collection<Enumeration>) {
    writeIntLE(values.indexOf(value.value))
    writeIntLE(enumerations.indexOf(value.enumeration))
    writeVarUInt(value.constraints.size)
    value.constraints.forEach { buffer.writeByte(it.ordinal) }
}
