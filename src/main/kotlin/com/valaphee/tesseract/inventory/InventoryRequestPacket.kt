/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.inventory

import com.valaphee.tesseract.inventory.item.stack.Stack
import com.valaphee.tesseract.inventory.item.stack.readStackInstance
import com.valaphee.tesseract.inventory.item.stack.readStackPre431
import com.valaphee.tesseract.inventory.item.stack.writeStackInstance
import com.valaphee.tesseract.inventory.item.stack.writeStackPre431
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
data class InventoryRequestPacket(
    var requests: Array<Request>
) : Packet {
    enum class ActionType {
        Take, Place, Swap, Drop, Destroy, Consume, Create, LabTableCombine, BeaconPayment, CraftRecipe, CraftRecipeAuto, CraftCreative, CraftRecipeOptional, CraftNonImplementedDeprecated, CraftResultsDeprecated
    }

    data class Action(
        val result: Array<Stack<*>?>?,
        var type: ActionType,
        var count: Int,
        var sourceWindowId: Int,
        var sourceSlotId: Int,
        var sourceNetId: Int,
        var random: Boolean,
        var destinationWindowId: Int,
        var destinationSlotId: Int,
        var destinationNetId: Int,
        var slotId: Int,
        var auxInt: Int,
        var auxInt2: Int
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Action

            if (!result.contentEquals(other.result)) return false
            if (type != other.type) return false
            if (count != other.count) return false
            if (sourceWindowId != other.sourceWindowId) return false
            if (sourceSlotId != other.sourceSlotId) return false
            if (sourceNetId != other.sourceNetId) return false
            if (random != other.random) return false
            if (destinationWindowId != other.destinationWindowId) return false
            if (destinationSlotId != other.destinationSlotId) return false
            if (destinationNetId != other.destinationNetId) return false
            if (slotId != other.slotId) return false
            if (auxInt != other.auxInt) return false
            if (auxInt2 != other.auxInt2) return false

            return true
        }

        override fun hashCode(): Int {
            var result1 = result.contentHashCode()
            result1 = 31 * result1 + type.hashCode()
            result1 = 31 * result1 + count
            result1 = 31 * result1 + sourceWindowId
            result1 = 31 * result1 + sourceSlotId
            result1 = 31 * result1 + sourceNetId
            result1 = 31 * result1 + random.hashCode()
            result1 = 31 * result1 + destinationWindowId
            result1 = 31 * result1 + destinationSlotId
            result1 = 31 * result1 + destinationNetId
            result1 = 31 * result1 + slotId
            result1 = 31 * result1 + auxInt
            result1 = 31 * result1 + auxInt2
            return result1
        }
    }

    data class Request(
        var requestId: Int,
        val actions: Array<Action>,
        val filteredTexts: Array<String>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Request

            if (!actions.contentEquals(other.actions)) return false
            if (requestId != other.requestId) return false

            return true
        }

        override fun hashCode(): Int {
            var result = actions.contentHashCode()
            result = 31 * result + requestId
            return result
        }
    }

    override val id get() = 0x93

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarUInt(requests.size)
        requests.forEach {
            buffer.writeVarInt(it.requestId)
            buffer.writeVarUInt(it.actions.size)
            it.actions.forEach {
                @Suppress("NON_EXHAUSTIVE_WHEN") when (it.type) {
                    ActionType.Take, ActionType.Place -> {
                        buffer.writeByte(it.count)
                        buffer.writeByte(it.sourceWindowId)
                        buffer.writeByte(it.sourceSlotId)
                        buffer.writeVarInt(it.sourceNetId)
                    }
                    ActionType.Swap -> {
                        buffer.writeByte(it.sourceWindowId)
                        buffer.writeByte(it.sourceSlotId)
                        buffer.writeVarInt(it.sourceNetId)
                    }
                    ActionType.Drop -> {
                        buffer.writeByte(it.count)
                        buffer.writeByte(it.sourceWindowId)
                        buffer.writeByte(it.sourceSlotId)
                        buffer.writeVarInt(it.sourceNetId)
                        buffer.writeBoolean(it.random)
                    }
                    ActionType.Destroy, ActionType.Consume -> {
                        buffer.writeByte(it.count)
                        buffer.writeByte(it.sourceWindowId)
                        buffer.writeByte(it.sourceSlotId)
                        buffer.writeVarInt(it.sourceNetId)
                        buffer.writeByte(it.destinationWindowId)
                        buffer.writeByte(it.destinationSlotId)
                        buffer.writeVarInt(it.destinationNetId)
                    }
                    ActionType.Create -> buffer.writeByte(it.sourceSlotId)
                    ActionType.BeaconPayment -> {
                        buffer.writeVarInt(it.auxInt)
                        buffer.writeVarInt(it.auxInt2)
                    }
                    ActionType.CraftRecipe, ActionType.CraftRecipeAuto, ActionType.CraftCreative -> buffer.writeVarInt(it.auxInt)
                    ActionType.CraftRecipeOptional -> {
                        buffer.writeVarUInt(it.auxInt)
                        buffer.writeIntLE(it.auxInt2)
                    }
                    ActionType.CraftResultsDeprecated -> {
                        it.result!!.let {
                            buffer.writeVarUInt(it.size)
                            it.forEach { if (version >= 431) buffer.writeStackInstance(it) else buffer.writeStackPre431(it) }
                        }
                        buffer.writeByte(it.auxInt)
                    }
                }
            }
            if (version >= 422) {
                buffer.writeVarUInt(it.filteredTexts.size)
                it.filteredTexts.forEach { buffer.writeString(it) }
            }
        }
    }

    override fun handle(handler: PacketHandler) = handler.inventoryRequest(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InventoryRequestPacket

        if (!requests.contentEquals(other.requests)) return false

        return true
    }

    override fun hashCode() = requests.contentHashCode()
}

/**
 * @author Kevin Ludwig
 */
object InventoryRequestPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = InventoryRequestPacket(Array(buffer.readVarUInt()) {
        InventoryRequestPacket.Request(buffer.readVarInt(), Array(buffer.readVarUInt()) {
            when (val actionType = InventoryRequestPacket.ActionType.values()[buffer.readByte().toInt()]) {
                InventoryRequestPacket.ActionType.Take, InventoryRequestPacket.ActionType.Place -> InventoryRequestPacket.Action(null, actionType, buffer.readByte().toInt(), buffer.readByte().toInt(), buffer.readByte().toInt(), buffer.readVarInt(), false, buffer.readByte().toInt(), buffer.readByte().toInt(), buffer.readVarInt(), 0, 0, 0)
                InventoryRequestPacket.ActionType.Swap -> InventoryRequestPacket.Action(null, actionType, 0, buffer.readByte().toInt(), buffer.readByte().toInt(), buffer.readVarInt(), false, buffer.readByte().toInt(), buffer.readByte().toInt(), buffer.readVarInt(), 0, 0, 0)
                InventoryRequestPacket.ActionType.Drop -> InventoryRequestPacket.Action(null, actionType, buffer.readByte().toInt(), buffer.readByte().toInt(), buffer.readByte().toInt(), buffer.readVarInt(), buffer.readBoolean(), 0, 0, 0, 0, 0, 0)
                InventoryRequestPacket.ActionType.Destroy, InventoryRequestPacket.ActionType.Consume -> InventoryRequestPacket.Action(null, actionType, buffer.readByte().toInt(), buffer.readByte().toInt(), buffer.readByte().toInt(), buffer.readVarInt(), false, 0, 0, 0, 0, 0, 0)
                InventoryRequestPacket.ActionType.Create -> InventoryRequestPacket.Action(null, actionType, 0, 0, buffer.readByte().toInt(), 0, false, 0, 0, 0, 0, 0, 0)
                InventoryRequestPacket.ActionType.BeaconPayment -> InventoryRequestPacket.Action(null, actionType, 0, 0, 0, 0, false, 0, 0, 0, 0, buffer.readVarInt(), buffer.readVarInt())
                InventoryRequestPacket.ActionType.CraftRecipe, InventoryRequestPacket.ActionType.CraftRecipeAuto, InventoryRequestPacket.ActionType.CraftCreative -> InventoryRequestPacket.Action(null, actionType, 0, 0, 0, 0, false, 0, 0, 0, 0, buffer.readVarInt(), 0)
                InventoryRequestPacket.ActionType.CraftRecipeOptional -> InventoryRequestPacket.Action(null, actionType, 0, 0, 0, 0, false, 0, 0, 0, 0, buffer.readVarUInt(), buffer.readIntLE())
                InventoryRequestPacket.ActionType.CraftResultsDeprecated -> InventoryRequestPacket.Action(Array(buffer.readVarUInt()) { if (version >= 431) buffer.readStackInstance() else buffer.readStackPre431() }, actionType, 0, 0, 0, 0, false, 0, 0, 0, 0, buffer.readByte().toInt(), 0)
                else -> InventoryRequestPacket.Action(null, actionType, 0, 0, 0, 0, false, 0, 0, 0, 0, 0, 0)
            }
        }, if (version >= 422) Array(buffer.readVarUInt()) { buffer.readString() } else emptyArray())
    })
}
