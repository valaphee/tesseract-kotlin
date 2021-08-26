/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net

/**
 * @author Kevin Ludwig
 */
interface PacketReader {
    fun read(buffer: PacketBuffer): Packet
}
