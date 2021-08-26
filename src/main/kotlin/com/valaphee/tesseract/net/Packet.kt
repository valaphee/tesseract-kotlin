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
}
