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

package com.valaphee.tesseract.actor

import com.valaphee.tesseract.net.PacketBuffer

/**
 * @author Kevin Ludwig
 */
data class Link(
    val type: Type,
    val fromUniqueEntityId: Long,
    val toUniqueEntityId: Long,
    val immediate: Boolean,
    val driversAction: Boolean
) {
    enum class Type {
        Remove, Driver, Passenger
    }

    constructor(type: Type, fromUniqueEntityId: Long, toUniqueEntityId: Long, immediate: Boolean) : this(type, fromUniqueEntityId, toUniqueEntityId, immediate, false)
}

fun PacketBuffer.readLinkPre407(): Link {
    val fromUniqueEntityId = readVarLong()
    val toUniqueEntityId = readVarLong()
    val type = Link.Type.values()[readUnsignedByte().toInt()]
    return Link(type, fromUniqueEntityId, toUniqueEntityId, readBoolean())
}

fun PacketBuffer.readLink(): Link {
    val fromUniqueEntityId = readVarLong()
    val toUniqueEntityId = readVarLong()
    val type = Link.Type.values()[readUnsignedByte().toInt()]
    return Link(type, fromUniqueEntityId, toUniqueEntityId, readBoolean(), readBoolean())
}

fun PacketBuffer.writeLinkPre407(value: Link) {
    writeVarLong(value.fromUniqueEntityId)
    writeVarLong(value.toUniqueEntityId)
    writeByte(value.type.ordinal)
    writeBoolean(value.immediate)
}

fun PacketBuffer.writeLink(value: Link) {
    writeLinkPre407(value)
    writeBoolean(value.driversAction)
}
