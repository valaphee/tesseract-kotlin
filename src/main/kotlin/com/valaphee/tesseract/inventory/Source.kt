/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.inventory

import com.valaphee.tesseract.net.PacketBuffer
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap

/**
 * @author Kevin Ludwig
 */
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
            @JvmStatic
            fun byId(id: Int) = byId[id]

            private val byId = Int2ObjectOpenHashMap<Type>(values().size).apply { values().forEach { this[it.id] = it } }
        }
    }

    enum class Action {
        DropItem, PickupItem, None
    }
}

fun PacketBuffer.readSource() = when (val type = Source.Type.byId(readVarUInt())) {
    Source.Type.Invalid, Source.Type.Global, Source.Type.Creative -> Source(type)
    Source.Type.Inventory, Source.Type.UntrackedInteractionUi, Source.Type.NonImplemented -> Source(type, readVarInt())
    Source.Type.World -> Source(Source.Type.World, action = Source.Action.values()[readVarUInt()])
    else -> throw IndexOutOfBoundsException()
}

fun PacketBuffer.writeSource(value: Source) {
    writeVarUInt(value.type.id)
    @Suppress("NON_EXHAUSTIVE_WHEN")
    when (value.type) {
        Source.Type.Inventory, Source.Type.UntrackedInteractionUi, Source.Type.NonImplemented -> writeVarInt(value.windowId)
        Source.Type.World -> writeVarUInt(value.action.ordinal)
    }
}
