/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.command

import com.valaphee.tesseract.net.PacketBuffer

/**
 * @author Kevin Ludwig
 */
data class Message(
    val success: Boolean,
    val message: String,
    val arguments: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (success != other.success) return false
        if (message != other.message) return false
        if (!arguments.contentEquals(other.arguments)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = success.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + arguments.contentHashCode()
        return result
    }
}

fun PacketBuffer.readMessage() = Message(readBoolean(), readString(), Array(readVarUInt()) { readString() })

fun PacketBuffer.writeMessage(value: Message) {
    writeBoolean(value.success)
    writeString(value.message)
    writeVarUInt(value.arguments.size)
    value.arguments.forEach { writeString(it) }
}
