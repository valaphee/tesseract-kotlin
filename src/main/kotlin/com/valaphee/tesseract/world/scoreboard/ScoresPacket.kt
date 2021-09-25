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
 *
 */

package com.valaphee.tesseract.world.scoreboard

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
data class ScoresPacket(
    val action: Action,
    val scores: Array<Score>
) : Packet {
    enum class Action {
        Set, Remove
    }

    override val id get() = 0x6C

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeByte(action.ordinal)
        buffer.writeVarUInt(scores.size)
        scores.forEach { (scoreboardId, objectiveName, value, scorerType, name, entityId) ->
            buffer.writeVarLong(scoreboardId)
            buffer.writeString(objectiveName)
            buffer.writeIntLE(value)
            if (action == Action.Set) {
                buffer.writeByte(scorerType.ordinal)
                @Suppress("NON_EXHAUSTIVE_WHEN")
                when (scorerType) {
                    Score.ScorerType.Entity, Score.ScorerType.Player -> buffer.writeVarLong(entityId)
                    Score.ScorerType.Fake -> buffer.writeString(name!!)
                }
            }
        }
    }

    override fun handle(handler: PacketHandler) = handler.scores(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScoresPacket

        if (action != other.action) return false
        if (!scores.contentEquals(other.scores)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = action.hashCode()
        result = 31 * result + scores.contentHashCode()
        return result
    }
}

/**
 * @author Kevin Ludwig
 */
object ScoresPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): ScoresPacket {
        val action = ScoresPacket.Action.values()[buffer.readUnsignedByte().toInt()]
        val entries = Array(buffer.readVarUInt()) {
            val scoreboardId = buffer.readVarLong()
            val objectiveId = buffer.readString()
            val score = buffer.readIntLE()
            if (action == ScoresPacket.Action.Set) {
                when (val scorerType = Score.ScorerType.values()[buffer.readUnsignedByte().toInt()]) {
                    Score.ScorerType.Entity, Score.ScorerType.Player -> return@Array Score(scoreboardId, objectiveId, score, scorerType, buffer.readVarLong())
                    Score.ScorerType.Fake -> return@Array Score(scoreboardId, objectiveId, score, buffer.readString())
                }
            }
            Score(scoreboardId, objectiveId, score)
        }
        return ScoresPacket(action, entries)
    }
}
