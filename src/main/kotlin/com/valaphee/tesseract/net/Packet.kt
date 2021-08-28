/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net

/**
 * @author Kevin Ludwig
 */
interface Packet {
    val id: Int

    fun write(buffer: PacketBuffer, version: Int)

    fun handle(handler: PacketHandler)

    companion object {
        internal const val idMask = 0x3FF
        internal const val senderIdShift = 10
        internal const val senderIdClientIdMask = 0x3
        internal const val clientIdShift = 12
    }
}
