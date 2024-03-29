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

package com.valaphee.tesseract.world

import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader

/**
 * @author Kevin Ludwig
 */
data class SoundEventPacketV1(
    var soundEvent: SoundEvent,
    var position: Float3,
    var extraData: Int,
    var pitch: Int,
    var babySound: Boolean,
    var relativeVolumeDisabled: Boolean
) : Packet {
    override val id get() = 0x18

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeByte(SoundEventPacket.soundEvents.getKey(soundEvent))
        buffer.writeFloat3(position)
        buffer.writeVarInt(extraData)
        buffer.writeVarInt(pitch)
        buffer.writeBoolean(babySound)
        buffer.writeBoolean(relativeVolumeDisabled)
    }

    override fun handle(handler: PacketHandler) = handler.soundEventV1(this)
}

/**
 * @author Kevin Ludwig
 */
object SoundEventPacketV1Reader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = SoundEventPacketV1(SoundEventPacket.soundEvents[buffer.readUnsignedByte().toInt()], buffer.readFloat3(), buffer.readVarInt(), buffer.readVarInt(), buffer.readBoolean(), buffer.readBoolean())
}
