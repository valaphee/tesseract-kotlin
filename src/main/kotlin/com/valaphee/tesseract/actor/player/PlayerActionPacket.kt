/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.player

import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.world.WorldContext

/**
 * @author Kevin Ludwig
 */
data class PlayerActionPacket(
    var player: Player,
    var action: Action,
    var blockPosition: Int3,
    var data: Int
) : Packet {
    enum class Action {
        StartBreak,
        AbortBreak,
        StopBreak,
        GetUpdatedBlock,
        DropItem,
        StartSleep,
        StopSleep,
        Respawn,
        Jump,
        StartSprinting,
        StopSprinting,
        StartSneak,
        StopSneak,
        DimensionChangeRequestOrCreativeBlockDestroy,
        DimensionChangeSuccess,
        StartGlide,
        StopGlide,
        BuildDenied,
        ContinueBreak,
        ChangeSkin,
        SetEnchantmentSeed,
        StartSwimming,
        StopSwimming,
        StartSpinAttack,
        StopSpinAttack,
        BlockInteract,
        BlockPredictDestroy,
        BlockContinueDestroy
    }

    override val id get() = 0x24

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarULong(player.id)
        buffer.writeVarInt(action.ordinal)
        buffer.writeInt3UnsignedY(blockPosition)
        buffer.writeVarUInt(data)
    }

    override fun handle(handler: PacketHandler) = handler.playerAction(this)
}

/**
 * @author Kevin Ludwig
 */
class PlayerActionPacketReader(
    private val context: WorldContext
) : PacketReader {
    @Suppress("UNCHECKED_CAST")
    override fun read(buffer: PacketBuffer, version: Int) = PlayerActionPacket(
        context.engine.findEntityOrNull(buffer.readVarULong()) as Player,
        PlayerActionPacket.Action.values()[buffer.readVarInt()],
        buffer.readInt3UnsignedY(),
        buffer.readVarInt()
    )
}
