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

package com.valaphee.tesseract.world

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader

/**
 * @author Kevin Ludwig
 */
data class BossBarPacket(
    val uniqueEntityId: Long,
    val action: Action,
    val title: String?,
    val playerUniqueEntityId: Long,
    val percentage: Float,
    val darkenSky: Int,
    val color: Int,
    val overlay: Int
) : Packet {
    enum class Action {
        Show,
        RegisterPlayer,
        Hide,
        UnregisterPlayer,
        SetPercentage,
        SetTitle,
        SetDarkenSky,
        SetStyle
    }

    override val id get() = 0x4A

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarLong(uniqueEntityId)
        buffer.writeVarUInt(action.ordinal)
        @Suppress("NON_EXHAUSTIVE_WHEN") when (action) {
            Action.Show -> {
                buffer.writeString(title!!)
                buffer.writeFloatLE(percentage)
                buffer.writeShortLE(darkenSky)
                buffer.writeVarUInt(color)
                buffer.writeVarUInt(overlay)
            }
            Action.SetDarkenSky -> {
                buffer.writeShortLE(darkenSky)
                buffer.writeVarUInt(color)
                buffer.writeVarUInt(overlay)
            }
            Action.SetStyle -> {
                buffer.writeVarUInt(color)
                buffer.writeVarUInt(overlay)
            }
            Action.RegisterPlayer, Action.UnregisterPlayer -> buffer.writeVarLong(
                playerUniqueEntityId
            )
            Action.SetPercentage -> buffer.writeFloatLE(percentage)
            Action.SetTitle -> buffer.writeString(title!!)
        }
    }

    override fun handle(handler: PacketHandler) = handler.bossBar(this)
}

/**
 * @author Kevin Ludwig
 */
object BossBarPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): BossBarPacket {
        val uniqueEntityId = buffer.readVarLong()
        val action = BossBarPacket.Action.values()[buffer.readVarUInt()]
        val title: String?
        val percentage: Float
        val darkenSky: Int
        val color: Int
        val overlay: Int
        val playerUniqueEntityId: Long
        @Suppress("NON_EXHAUSTIVE_WHEN") when (action) {
            BossBarPacket.Action.Show -> {
                title = buffer.readString()
                percentage = buffer.readFloatLE()
                darkenSky = buffer.readUnsignedShortLE()
                color = buffer.readVarUInt()
                overlay = buffer.readVarUInt()
                playerUniqueEntityId = 0
            }
            BossBarPacket.Action.SetDarkenSky -> {
                title = null
                percentage = 0.0f
                darkenSky = buffer.readUnsignedShortLE()
                color = buffer.readVarUInt()
                overlay = buffer.readVarUInt()
                playerUniqueEntityId = 0
            }
            BossBarPacket.Action.SetStyle -> {
                title = null
                percentage = 0.0f
                darkenSky = 0
                color = buffer.readVarUInt()
                overlay = buffer.readVarUInt()
                playerUniqueEntityId = 0
            }
            BossBarPacket.Action.RegisterPlayer, BossBarPacket.Action.UnregisterPlayer -> {
                title = null
                percentage = 0.0f
                darkenSky = 0
                color = 0
                overlay = 0
                playerUniqueEntityId = buffer.readVarLong()
            }
            BossBarPacket.Action.SetPercentage -> {
                title = null
                percentage = buffer.readFloatLE()
                darkenSky = 0
                color = 0
                overlay = 0
                playerUniqueEntityId = buffer.readVarLong()
            }
            BossBarPacket.Action.SetTitle -> {
                title = buffer.readString()
                percentage = 0.0f
                darkenSky = 0
                color = 0
                overlay = 0
                playerUniqueEntityId = buffer.readVarLong()
            }
            else -> {
                title = null
                percentage = 0.0f
                darkenSky = 0
                color = 0
                overlay = 0
                playerUniqueEntityId = 0
            }
        }
        return BossBarPacket(uniqueEntityId, action, title, playerUniqueEntityId, percentage, darkenSky, color, overlay)
    }
}
