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

package com.valaphee.tesseract.command.net

import com.valaphee.tesseract.net.PacketBuffer
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
data class Origin(
    val where: Where,
    val requestId: UUID,
    val purpose: String,
    val eventId: Long
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
    writeUuid(value.requestId)
    writeString(value.purpose)
    if (value.where == Origin.Where.DeveloperConsole || value.where == Origin.Where.Test) writeVarLong(value.eventId)
}
