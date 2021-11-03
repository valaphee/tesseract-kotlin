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

import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader

/**
 * @author Kevin Ludwig
 */
class InputCorrectPacket(
    val position: Float3,
    val positionDelta: Float3,
    val onGround: Boolean,
    val tick: Long
) : Packet() {
    override val id get() = 0xA1

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeFloat3(position)
        buffer.writeFloat3(positionDelta)
        buffer.writeBoolean(onGround)
        buffer.writeVarULong(tick)
    }

    override fun handle(handler: PacketHandler) = handler.inputCorrect(this)

    override fun toString() = "InputCorrectPacket(position=$position, positionDelta=$positionDelta, onGround=$onGround, tick=$tick)"
}

/**
 * @author Kevin Ludwig
 */
object InputCorrectPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = InputCorrectPacket(buffer.readFloat3(), buffer.readFloat3(), buffer.readBoolean(), buffer.readVarULong())
}
