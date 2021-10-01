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

package com.valaphee.tesseract.entity

import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import com.valaphee.tesseract.util.math.Direction

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
class PaintingAddPacket(
    val uniqueEntityId: Long,
    val runtimeEntityId: Long,
    val position: Float3,
    val direction: Direction,
    val painting: Painting
) : Packet {
    enum class Painting(
        val title: String,
        val width: Int,
        val height: Int
    ) {
        Kebab("Kebab", 1, 1),
        Aztec("Aztec", 1, 1),
        Alban("Alban", 1, 1),
        Aztec2("Aztec2", 1, 1),
        Bomb("Bomb", 1, 1),
        Plant("Plant", 1, 1),
        Wasteland("Wasteland", 1, 1),
        Wanderer("Wanderer", 1, 2),
        Graham("Graham", 1, 2),
        Pool("Pool", 2, 1),
        Courbet("Courbet", 2, 1),
        Sea("Sea", 2, 1),
        Sunset("Sunset", 2, 1),
        Creebet("Creebet", 2, 1),
        Match("Match", 2, 2),
        Bust("Bust", 2, 2),
        Stage("Stage", 2, 2),
        Void("Void", 2, 2),
        SkullAndRoses("SkullAndRoses", 2, 2),
        Wither("Wither", 2, 2),
        Fighters("Fighters", 4, 2),
        Skeleton("Skeleton", 4, 3),
        DonkeyKong("DonkeyKong", 4, 3),
        Pointer("Pointer", 4, 4),
        PigScene("Pigscene", 4, 4),
        FlamingSkull("Flaming Skull", 4, 4);

        companion object {
            fun byTitle(title: String) = checkNotNull(byTitle[title])

            private val byTitle: Map<String, Painting> = HashMap<String, Painting>(values().size).apply { values().forEach { this[it.title] = it } }
        }
    }

    override val id get() = 0x16

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarLong(uniqueEntityId)
        buffer.writeVarULong(runtimeEntityId)
        buffer.writeFloat3(position)
        when (direction) {
            Direction.North -> buffer.writeVarInt(2)
            Direction.South -> buffer.writeVarInt(0)
            Direction.West -> buffer.writeVarInt(1)
            Direction.East -> buffer.writeVarInt(3)
            else -> throw IndexOutOfBoundsException()
        }
        buffer.writeString(painting.title)
    }

    override fun handle(handler: PacketHandler) = handler.paintingAdd(this)

    override fun toString() = "PaintingAddPacket(uniqueEntityId=$uniqueEntityId, runtimeEntityId=$runtimeEntityId, position=$position, direction=$direction, painting=$painting)"
}

/**
 * @author Kevin Ludwig
 */
object PaintingAddPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = PaintingAddPacket(
        buffer.readVarLong(),
        buffer.readVarULong(),
        buffer.readFloat3(),
        when (buffer.readVarInt()) {
            0 -> Direction.South
            1 -> Direction.West
            2 -> Direction.North
            3 -> Direction.East
            else -> throw IndexOutOfBoundsException()
        },
        PaintingAddPacket.Painting.byTitle(buffer.readString())
    )
}
