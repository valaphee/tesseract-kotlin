/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.command

import com.valaphee.tesseract.net.PacketBuffer
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
data class Origin(
    var where: Where,
    var userId: UUID,
    var requestId: String,
    var eventId: Long
) {
    enum class Where {
        Player,
        Block,
        MinecartBlock,
        DeveloperConsole,
        Test,
        AutomationPlayer,
        ClientAutomation,
        DedicatedServer,
        Entity,
        Virtual,
        GameArgument,
        EntityServer,
        PreCompiled,
        GameMasterEntityServer,
        Script
    }
}

fun PacketBuffer.readOrigin(): Origin {
    val where = Origin.Where.values()[readVarUInt()]
    return Origin(where, readUuid(), readString(), if (Origin.Where.DeveloperConsole == where || Origin.Where.Test == where) readVarLong() else 0)
}

fun PacketBuffer.writeOrigin(value: Origin) {
    writeVarUInt(value.where.ordinal)
    writeUuid(value.userId)
    writeString(value.requestId)
    if (value.where == Origin.Where.DeveloperConsole || value.where == Origin.Where.Test) writeVarLong(value.eventId)
}
