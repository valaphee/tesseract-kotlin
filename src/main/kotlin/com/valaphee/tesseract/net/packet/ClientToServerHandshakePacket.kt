/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net.packet

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Serverbound)
object ClientToServerHandshakePacket : Packet {
    override val id get() = 0x04

    override fun write(buffer: PacketBuffer, version: Int) = Unit

    override fun handle(handler: PacketHandler) = Unit

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode() = javaClass.hashCode()

    override fun toString() = "ClientToServerHandshakePacket()"
}

/**
 * @author Kevin Ludwig
 */
class ClientToServerHandshakePacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = ClientToServerHandshakePacket
}
