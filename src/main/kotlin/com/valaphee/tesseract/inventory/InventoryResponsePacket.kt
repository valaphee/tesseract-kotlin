/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.inventory

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Clientbound)
data class InventoryResponsePacket(
    var responses: Array<Response>
) : Packet {
    data class ResponseWindowSlot(
        val slotId: Int,
        val hotbarSlot: Int,
        val count: Int,
        val netId: Int,
        val name: String,
        val damage: Int
    )

    data class ResponseWindow(
        val windowId: Int,
        val slots: Array<ResponseWindowSlot>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ResponseWindow

            if (windowId != other.windowId) return false
            if (!slots.contentEquals(other.slots)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = windowId
            result = 31 * result + slots.contentHashCode()
            return result
        }
    }

    enum class ResponseStatus {
        Ok, Error
    }

    data class Response(
        val status: ResponseStatus,
        var requestId: Int,
        var windows: Array<ResponseWindow>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Response

            if (status != other.status) return false
            if (requestId != other.requestId) return false
            if (!windows.contentEquals(other.windows)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = status.hashCode()
            result = 31 * result + requestId
            result = 31 * result + windows.contentHashCode()
            return result
        }
    }

    override val id get() = 0x94

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarUInt(responses.size)
        responses.forEach {
            if (version >= 419) buffer.writeByte(it.status.ordinal) else buffer.writeBoolean(
                when (it.status) {
                    ResponseStatus.Ok -> true
                    ResponseStatus.Error -> false
                }
            )
            buffer.writeVarInt(it.requestId)
            if (it.status == ResponseStatus.Ok) {
                buffer.writeVarUInt(it.windows.size)
                it.windows.forEach {
                    buffer.writeByte(it.windowId)
                    buffer.writeVarUInt(it.slots.size)
                    it.slots.forEach {
                        buffer.writeByte(it.slotId)
                        buffer.writeByte(it.hotbarSlot)
                        buffer.writeByte(it.count)
                        buffer.writeVarInt(it.netId)
                        if (version >= 422) {
                            buffer.writeString(it.name)
                            if (version >= 428) buffer.writeVarInt(it.damage)
                        }
                    }
                }
            }
        }
    }

    override fun handle(handler: PacketHandler) = handler.inventoryResponse(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InventoryResponsePacket

        if (!responses.contentEquals(other.responses)) return false

        return true
    }

    override fun hashCode() = responses.contentHashCode()
}
