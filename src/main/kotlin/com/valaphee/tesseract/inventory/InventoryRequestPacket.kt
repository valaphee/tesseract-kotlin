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
@Restrict(Restriction.ToServer)
class InventoryRequestPacket(
    val requests: Array<Request>
) : Packet() {
    enum class ActionType {
        Move, Place, Swap, Drop, Destroy, Consume, Create, LabTableCombine, BeaconPayment, MineBlock, CraftRecipe, CraftRecipeAuto, CraftCreative, CraftRecipeOptional, CraftNonImplementedDeprecated, CraftResultsDeprecated
    }

    class Action(
        val result: Array<Stack?>?,
        val type: ActionType,
        val count: Int,
        val sourceSlotType: WindowSlotType?,
        val sourceSlotId: Int,
        val sourceNetId: Int,
        val random: Boolean,
        val destinationSlotType: WindowSlotType?,
        val destinationSlotId: Int,
        val destinationNetId: Int,
        val slotId: Int,
        val auxInt: Int,
        val auxInt2: Int
    ) {
        override fun toString() = "Action(result=${result?.contentToString()}, type=$type, count=$count, sourceSlotType=$sourceSlotType, sourceSlotId=$sourceSlotId, sourceNetId=$sourceNetId, random=$random, destinationSlotType=$destinationSlotType, destinationSlotId=$destinationSlotId, destinationNetId=$destinationNetId, slotId=$slotId, auxInt=$auxInt, auxInt2=$auxInt2)"
    }

    class Request(
        val requestId: Int,
        val actions: Array<Action>,
        val filteredTexts: Array<String>
    ) {
        override fun toString() = "Request(requestId=$requestId, actions=${actions.contentToString()}, filteredTexts=${filteredTexts.contentToString()})"
    }

    override val id get() = 0x93

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarUInt(requests.size)
        requests.forEach {
            buffer.writeVarInt(it.requestId)
            buffer.writeVarUInt(it.actions.size)
            it.actions.forEach {
                @Suppress("NON_EXHAUSTIVE_WHEN") when (it.type) {
                    ActionType.Move, ActionType.Place -> {
                        buffer.writeByte(it.count)
                        buffer.writeByte(it.sourceSlotType!!.ordinal)
                        buffer.writeByte(it.sourceSlotId)
                        buffer.writeVarInt(it.sourceNetId)
                        buffer.writeByte(it.destinationSlotType!!.ordinal)
                        buffer.writeByte(it.destinationSlotId)
                        buffer.writeVarInt(it.destinationNetId)
                    }
                    ActionType.Swap -> {
                        buffer.writeByte(it.sourceSlotType!!.ordinal)
                        buffer.writeByte(it.sourceSlotId)
                        buffer.writeVarInt(it.sourceNetId)
                        buffer.writeByte(it.destinationSlotType!!.ordinal)
                        buffer.writeByte(it.destinationSlotId)
                        buffer.writeVarInt(it.destinationNetId)
                    }
                    ActionType.Drop -> {
                        buffer.writeByte(it.count)
                        buffer.writeByte(it.sourceSlotType!!.ordinal)
                        buffer.writeByte(it.sourceSlotId)
                        buffer.writeVarInt(it.sourceNetId)
                        buffer.writeBoolean(it.random)
                    }
                    ActionType.Destroy, ActionType.Consume -> {
                        buffer.writeByte(it.count)
                        buffer.writeByte(it.sourceSlotType!!.ordinal)
                        buffer.writeByte(it.sourceSlotId)
                        buffer.writeVarInt(it.sourceNetId)
                    }
                    ActionType.Create -> buffer.writeByte(it.sourceSlotId)
                    ActionType.BeaconPayment -> {
                        buffer.writeVarInt(it.auxInt)
                        buffer.writeVarInt(it.auxInt2)
                    }
                    ActionType.CraftRecipe, ActionType.CraftCreative -> buffer.writeVarUInt(it.auxInt)
                    ActionType.CraftRecipeAuto -> {
                        buffer.writeVarUInt(it.auxInt)
                        if (version >= 448) buffer.writeByte(it.auxInt2)
                    }
                    ActionType.CraftRecipeOptional -> {
                        buffer.writeVarUInt(it.auxInt)
                        buffer.writeIntLE(it.auxInt2)
                    }
                    ActionType.CraftNonImplementedDeprecated, ActionType.CraftResultsDeprecated -> {
                        it.result!!.let {
                            buffer.writeVarUInt(it.size)
                            it.forEach { if (version >= 431) buffer.writeStackInstance(it) else buffer.writeStackPre431(it) }
                        }
                        buffer.writeByte(it.count)
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

    override fun toString() = "InventoryRequestPacket(requests=${requests.contentToString()})"
}

/**
 * @author Kevin Ludwig
 */
object InventoryRequestPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = InventoryRequestPacket(Array(buffer.readVarUInt()) {
        InventoryRequestPacket.Request(buffer.readVarInt(), Array(buffer.readVarUInt()) {
            when (val actionType = InventoryRequestPacket.ActionType.values()[buffer.readByte().toInt()]) {
                InventoryRequestPacket.ActionType.Move, InventoryRequestPacket.ActionType.Place -> InventoryRequestPacket.Action(null, actionType, buffer.readByte().toInt(), WindowSlotType.values()[buffer.readByte().toInt()], buffer.readByte().toInt(), buffer.readVarInt(), false, WindowSlotType.values()[buffer.readByte().toInt()], buffer.readByte().toInt(), buffer.readVarInt(), 0, 0, 0)
                InventoryRequestPacket.ActionType.Swap -> InventoryRequestPacket.Action(null, actionType, 0, WindowSlotType.values()[buffer.readByte().toInt()], buffer.readByte().toInt(), buffer.readVarInt(), false, WindowSlotType.values()[buffer.readByte().toInt()], buffer.readByte().toInt(), buffer.readVarInt(), 0, 0, 0)
                InventoryRequestPacket.ActionType.Drop -> InventoryRequestPacket.Action(null, actionType, buffer.readByte().toInt(), WindowSlotType.values()[buffer.readByte().toInt()], buffer.readByte().toInt(), buffer.readVarInt(), buffer.readBoolean(), null, 0, 0, 0, 0, 0)
                InventoryRequestPacket.ActionType.Destroy, InventoryRequestPacket.ActionType.Consume -> InventoryRequestPacket.Action(null, actionType, buffer.readByte().toInt(), WindowSlotType.values()[buffer.readByte().toInt()], buffer.readByte().toInt(), buffer.readVarInt(), false, null, 0, 0, 0, 0, 0)
                InventoryRequestPacket.ActionType.Create -> InventoryRequestPacket.Action(null, actionType, 0, null, buffer.readByte().toInt(), 0, false, null, 0, 0, 0, 0, 0)
                InventoryRequestPacket.ActionType.BeaconPayment -> InventoryRequestPacket.Action(null, actionType, 0, null, 0, 0, false, null, 0, 0, 0, buffer.readVarInt(), buffer.readVarInt())
                InventoryRequestPacket.ActionType.CraftRecipe, InventoryRequestPacket.ActionType.CraftCreative -> InventoryRequestPacket.Action(null, actionType, 0, null, 0, 0, false, null, 0, 0, 0, buffer.readVarInt(), 0)
                InventoryRequestPacket.ActionType.CraftRecipeAuto -> InventoryRequestPacket.Action(null, actionType, 0, null, 0, 0, false, null, 0, 0, 0, buffer.readVarInt(), if (version >= 448) buffer.readByte().toInt() else 0)
                InventoryRequestPacket.ActionType.CraftRecipeOptional -> InventoryRequestPacket.Action(null, actionType, 0, null, 0, 0, false, null, 0, 0, 0, buffer.readVarUInt(), buffer.readIntLE())
                InventoryRequestPacket.ActionType.CraftResultsDeprecated -> InventoryRequestPacket.Action(Array(buffer.readVarUInt()) { if (version >= 431) buffer.readStackInstance() else buffer.readStackPre431() }, actionType, 0, null, 0, 0, false, null, 0, 0, 0, buffer.readByte().toInt(), 0)
                else -> TODO()
            }
        }, if (version >= 422) Array(buffer.readVarUInt()) { buffer.readString() } else emptyArray())
    })
}
