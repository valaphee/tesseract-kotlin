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

package com.valaphee.tesseract.entity.player

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.world.GameMode

/**
 * @author Kevin Ludwig
 */
class PlayerGameModePacket(
    val uniqueEntityId: Long,
    val gameMode: GameMode
) : Packet {
    override val id get() = 0x97

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarInt(gameMode.ordinal)
        buffer.writeVarLong(uniqueEntityId)
    }

    override fun handle(handler: PacketHandler) = handler.playerGameMode(this)

    override fun toString() = "PlayerGameModePacket(uniqueEntityId=$uniqueEntityId, gameMode=$gameMode)"
}

/**
 * @author Kevin Ludwig
 */
object PlayerGameModePacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): PlayerGameModePacket {
        val gameMode = GameMode.values()[buffer.readVarInt()]
        val uniqueEntityId = buffer.readVarLong()
        return PlayerGameModePacket(uniqueEntityId, gameMode)
    }
}
