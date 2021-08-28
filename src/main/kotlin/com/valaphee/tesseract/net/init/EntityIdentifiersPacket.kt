/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net.init

import com.valaphee.tesseract.nbt.CompoundTag
import com.valaphee.tesseract.nbt.NbtOutputStream
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import com.valaphee.tesseract.util.LittleEndianVarIntByteBufOutputStream

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Clientbound)
data class EntityIdentifiersPacket(
    val data: ByteArray?,
    var tag: CompoundTag? = null
) : Packet {
    override val id get() = 0x77

    override fun write(buffer: PacketBuffer, version: Int) {
        data?.let { buffer.writeBytes(it) } ?: NbtOutputStream(LittleEndianVarIntByteBufOutputStream(buffer)).use { it.writeTag(tag) }
    }

    override fun handle(handler: PacketHandler) = handler.entityIdentifiers(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EntityIdentifiersPacket

        if (data != null) {
            if (other.data == null) return false
            if (!data.contentEquals(other.data)) return false
        } else if (other.data != null) return false
        if (tag != other.tag) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data?.contentHashCode() ?: 0
        result = 31 * result + (tag?.hashCode() ?: 0)
        return result
    }

    override fun toString() = "EntityIdentifiersPacket(tag=$tag)"
}
