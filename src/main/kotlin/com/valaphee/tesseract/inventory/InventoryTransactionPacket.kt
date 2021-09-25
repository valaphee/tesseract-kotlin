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

import com.valaphee.foundry.math.Float3
import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.inventory.item.stack.Stack
import com.valaphee.tesseract.inventory.item.stack.readStack
import com.valaphee.tesseract.inventory.item.stack.readStackPre431
import com.valaphee.tesseract.inventory.item.stack.writeStack
import com.valaphee.tesseract.inventory.item.stack.writeStackPre431
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToServer)
data class InventoryTransactionPacket(
    val legacySlots: Array<LegacySlot>?,
    val type: Type,
    val usingNetIds: Boolean,
    val actions: Array<Action>,
    val actionId: Int,
    val runtimeEntityId: Long,
    val position: Int3?,
    val auxInt: Int,
    val hotbarSlot: Int,
    val stackInHand: Stack<*>?,
    val fromPosition: Float3?,
    val clickPosition: Float3?,
    val headPosition: Float3?,
    val blockRuntimeId: Int
) : Packet {
    data class LegacySlot(
        val windowId: Int,
        val slotIds: ByteArray
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as LegacySlot

            if (windowId != other.windowId) return false
            if (!slotIds.contentEquals(other.slotIds)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = windowId
            result = 31 * result + slotIds.contentHashCode()
            return result
        }
    }

    enum class Type {
        Normal, Mismatch, ItemUse, ItemUseOnEntity, ItemRelease
    }

    data class Action(
        val source: Source,
        val slotId: Int,
        val fromStack: Stack<*>?,
        val toStack: Stack<*>?,
        val netId: Int? = null
    )

    data class Source(
        val type: Type,
        val windowId: Int = WindowId.None,
        val action: Action = Action.None
    ) {
        enum class Type(
            val id: Int
        ) {
            Invalid(-1),
            Inventory(0),
            Global(1),
            World(2),
            Creative(3),
            UntrackedInteractionUi(100),
            NonImplemented(99999);

            companion object {
                fun byId(id: Int): Type = byId[id]

                private val byId = Int2ObjectOpenHashMap<Type>(values().size).apply { values().forEach { this[it.id] = it } }
            }
        }

        enum class Action {
            DropItem, PickupItem, None
        }
    }

    companion object ActionId {
        const val ItemUseBlock = 0
        const val ItemUseAir = 1
        const val ItemUseDestroy = 2
        const val ItemUseOnEntityInteract = 0
        const val ItemUseOnEntityAttack = 1
        const val ItemReleaseRelease = 0
        const val ItemReleaseConsume = 1
    }

    override val id get() = 0x1E

    override fun write(buffer: PacketBuffer, version: Int) {
        if (version >= 407) TODO()
        buffer.writeVarUInt(type.ordinal)
        if (version in 407 until 431) buffer.writeBoolean(usingNetIds)
        actions.let {
            buffer.writeVarUInt(it.size)
            it.forEach { if (version >= 431) buffer.writeAction(it) else if (usingNetIds && version >= 407) buffer.writeActionPre431(it) else buffer.writeActionPre407(it) }
        }
        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (type) {
            Type.ItemUse -> {
                buffer.writeVarUInt(actionId)
                buffer.writeInt3UnsignedY(position!!)
                buffer.writeVarInt(auxInt)
                buffer.writeVarInt(hotbarSlot)
                if (version >= 431) buffer.writeStack(stackInHand) else buffer.writeStackPre431(stackInHand)
                buffer.writeFloat3(fromPosition!!)
                buffer.writeFloat3(clickPosition!!)
                if (version < 407) buffer.writeVarUInt(blockRuntimeId)
            }
            Type.ItemUseOnEntity -> {
                buffer.writeVarULong(runtimeEntityId)
                buffer.writeVarUInt(actionId)
                buffer.writeVarInt(hotbarSlot)
                if (version >= 431) buffer.writeStack(stackInHand) else buffer.writeStackPre431(stackInHand)
                buffer.writeFloat3(fromPosition!!)
                if (version < 407) buffer.writeFloat3(clickPosition!!)
            }
            Type.ItemRelease -> {
                buffer.writeVarUInt(actionId)
                buffer.writeVarInt(hotbarSlot)
                if (version >= 431) buffer.writeStack(stackInHand) else buffer.writeStackPre431(stackInHand)
                if (version >= 407) buffer.writeFloat3(fromPosition!!) else buffer.writeFloat3(headPosition!!)
            }
        }
    }

    override fun handle(handler: PacketHandler) = handler.inventoryTransaction(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InventoryTransactionPacket

        if (legacySlots != null) {
            if (other.legacySlots == null) return false
            if (!legacySlots.contentEquals(other.legacySlots)) return false
        } else if (other.legacySlots != null) return false
        if (type != other.type) return false
        if (usingNetIds != other.usingNetIds) return false
        if (!actions.contentEquals(other.actions)) return false
        if (actionId != other.actionId) return false
        if (runtimeEntityId != other.runtimeEntityId) return false
        if (position != other.position) return false
        if (auxInt != other.auxInt) return false
        if (hotbarSlot != other.hotbarSlot) return false
        if (stackInHand != other.stackInHand) return false
        if (fromPosition != other.fromPosition) return false
        if (clickPosition != other.clickPosition) return false
        if (headPosition != other.headPosition) return false
        if (blockRuntimeId != other.blockRuntimeId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = legacySlots?.contentHashCode() ?: 0
        result = 31 * result + type.hashCode()
        result = 31 * result + usingNetIds.hashCode()
        result = 31 * result + actions.contentHashCode()
        result = 31 * result + actionId
        result = 31 * result + runtimeEntityId.hashCode()
        result = 31 * result + (position?.hashCode() ?: 0)
        result = 31 * result + auxInt
        result = 31 * result + hotbarSlot
        result = 31 * result + (stackInHand?.hashCode() ?: 0)
        result = 31 * result + (fromPosition?.hashCode() ?: 0)
        result = 31 * result + (clickPosition?.hashCode() ?: 0)
        result = 31 * result + (headPosition?.hashCode() ?: 0)
        result = 31 * result + blockRuntimeId
        return result
    }
}

/**
 * @author Kevin Ludwig
 */
object InventoryTransactionPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): InventoryTransactionPacket {
        val legacySlots = if (version >= 407) {
            val legacyRequestId = buffer.readVarInt()
            if (legacyRequestId < -1 && (legacyRequestId and 1) == 0) Array(buffer.readVarUInt()) { InventoryTransactionPacket.LegacySlot(buffer.readByte().toInt(), buffer.readByteArray()) } else null
        } else null
        val type = InventoryTransactionPacket.Type.values()[buffer.readVarUInt()]
        val usingNetIds = if (version in 407 until 431) buffer.readBoolean() else false
        val actions = Array(buffer.readVarUInt()) { if (version >= 431) buffer.readAction() else if (usingNetIds) buffer.readActionPre431() else buffer.readActionPre407() }
        val actionId: Int
        val runtimeEntityId: Long
        val position: Int3?
        val auxInt: Int
        val hotbarSlot: Int
        val stackInHand: Stack<*>?
        val fromPosition: Float3?
        val clickPosition: Float3?
        val headPosition: Float3?
        val blockRuntimeId: Int
        when (type) {
            InventoryTransactionPacket.Type.ItemUse -> {
                actionId = buffer.readVarUInt()
                runtimeEntityId = 0
                position = buffer.readInt3UnsignedY()
                auxInt = buffer.readVarInt()
                hotbarSlot = buffer.readVarInt()
                stackInHand = if (version >= 431) buffer.readStack() else buffer.readStackPre431()
                fromPosition = buffer.readFloat3()
                clickPosition = buffer.readFloat3()
                headPosition = null
                blockRuntimeId = buffer.readVarUInt()
            }
            InventoryTransactionPacket.Type.ItemUseOnEntity -> {
                runtimeEntityId = buffer.readVarULong()
                actionId = buffer.readVarUInt()
                position = null
                auxInt = 0
                hotbarSlot = buffer.readVarInt()
                stackInHand = if (version >= 431) buffer.readStack() else buffer.readStackPre431()
                fromPosition = buffer.readFloat3()
                clickPosition = buffer.readFloat3()
                headPosition = null
                blockRuntimeId = 0
            }
            InventoryTransactionPacket.Type.ItemRelease -> {
                actionId = buffer.readVarUInt()
                runtimeEntityId = 0
                position = null
                auxInt = 0
                hotbarSlot = buffer.readVarInt()
                stackInHand = if (version >= 431) buffer.readStack() else buffer.readStackPre431()
                fromPosition = null
                clickPosition = null
                headPosition = buffer.readFloat3()
                blockRuntimeId = 0
            }
            else -> {
                actionId = 0
                runtimeEntityId = 0
                position = null
                auxInt = 0
                hotbarSlot = 0
                stackInHand = null
                fromPosition = null
                clickPosition = null
                headPosition = null
                blockRuntimeId = 0
            }
        }
        return InventoryTransactionPacket(legacySlots, type, usingNetIds, actions, actionId, runtimeEntityId, position, auxInt, hotbarSlot, stackInHand, fromPosition, clickPosition, headPosition, blockRuntimeId)
    }
}

fun PacketBuffer.readActionPre407() = InventoryTransactionPacket.Action(readSource(), readVarUInt(), readStackPre431(), readStackPre431())

fun PacketBuffer.readActionPre431() = InventoryTransactionPacket.Action(readSource(), readVarUInt(), readStackPre431(), readStackPre431(), readVarInt())

fun PacketBuffer.readAction() = InventoryTransactionPacket.Action(readSource(), readVarUInt(), readStack(), readStack())

fun PacketBuffer.writeActionPre407(value: InventoryTransactionPacket.Action) {
    writeSource(value.source)
    writeVarUInt(value.slotId)
    writeStackPre431(value.fromStack)
    writeStackPre431(value.toStack)
}

fun PacketBuffer.writeActionPre431(value: InventoryTransactionPacket.Action) {
    writeSource(value.source)
    writeVarUInt(value.slotId)
    writeStackPre431(value.fromStack)
    writeStackPre431(value.toStack)
    writeVarInt(value.netId!!)
}

fun PacketBuffer.writeAction(value: InventoryTransactionPacket.Action) {
    writeSource(value.source)
    writeVarUInt(value.slotId)
    writeStack(value.fromStack)
    writeStack(value.toStack)
}

fun PacketBuffer.readSource() = when (val type = InventoryTransactionPacket.Source.Type.byId(readVarUInt())) {
    InventoryTransactionPacket.Source.Type.Invalid, InventoryTransactionPacket.Source.Type.Global, InventoryTransactionPacket.Source.Type.Creative -> InventoryTransactionPacket.Source(type)
    InventoryTransactionPacket.Source.Type.Inventory, InventoryTransactionPacket.Source.Type.UntrackedInteractionUi, InventoryTransactionPacket.Source.Type.NonImplemented -> InventoryTransactionPacket.Source(type, readVarInt())
    InventoryTransactionPacket.Source.Type.World -> InventoryTransactionPacket.Source(InventoryTransactionPacket.Source.Type.World, action = InventoryTransactionPacket.Source.Action.values()[readVarUInt()])
    else -> throw IndexOutOfBoundsException()
}

fun PacketBuffer.writeSource(value: InventoryTransactionPacket.Source) {
    writeVarUInt(value.type.id)
    @Suppress("NON_EXHAUSTIVE_WHEN")
    when (value.type) {
        InventoryTransactionPacket.Source.Type.Inventory, InventoryTransactionPacket.Source.Type.UntrackedInteractionUi, InventoryTransactionPacket.Source.Type.NonImplemented -> writeVarInt(value.windowId)
        InventoryTransactionPacket.Source.Type.World -> writeVarUInt(value.action.ordinal)
    }
}
