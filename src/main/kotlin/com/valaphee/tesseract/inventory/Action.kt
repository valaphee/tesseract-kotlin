/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.inventory

import com.valaphee.tesseract.item.stack.Stack
import com.valaphee.tesseract.item.stack.readStack
import com.valaphee.tesseract.item.stack.readStackPre431
import com.valaphee.tesseract.item.stack.writeStack
import com.valaphee.tesseract.item.stack.writeStackPre431
import com.valaphee.tesseract.net.PacketBuffer

/**
 * @author Kevin Ludwig
 */
data class Action(
    val source: Source,
    val slotId: Int,
    val fromStack: Stack<*>?,
    val toStack: Stack<*>?,
    val netId: Int? = null
)

fun PacketBuffer.readActionPre407() = Action(readSource(), readVarUInt(), readStackPre431(), readStackPre431())

fun PacketBuffer.readActionPre431() = Action(readSource(), readVarUInt(), readStackPre431(), readStackPre431(), readVarInt())

fun PacketBuffer.readAction() = Action(readSource(), readVarUInt(), readStack(), readStack())

fun PacketBuffer.writeActionPre407(value: Action) {
    writeSource(value.source)
    writeVarUInt(value.slotId)
    writeStackPre431(value.fromStack)
    writeStackPre431(value.toStack)
}

fun PacketBuffer.writeActionPre431(value: Action) {
    writeSource(value.source)
    writeVarUInt(value.slotId)
    writeStackPre431(value.fromStack)
    writeStackPre431(value.toStack)
    writeVarInt(value.netId!!)
}

fun PacketBuffer.writeAction(value: Action) {
    writeSource(value.source)
    writeVarUInt(value.slotId)
    writeStack(value.fromStack)
    writeStack(value.toStack)
}
