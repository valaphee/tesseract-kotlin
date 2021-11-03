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
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
class SoundPacket(
    val sound: Sound? = null,
    val soundKey: String? = null,
    val position: Float3,
    val volume: Float,
    val pitch: Float
) : Packet() {
    override val id get() = 0x56

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeString(sound?.key ?: soundKey!!)
        buffer.writeInt3UnsignedY(position.toMutableFloat3().scale(8.0f).toInt3())
        buffer.writeFloatLE(volume)
        buffer.writeFloatLE(pitch)
    }

    override fun handle(handler: PacketHandler) = handler.sound(this)

    override fun toString() = "SoundPacket(sound=$sound, soundKey=$soundKey, position=$position, volume=$volume, pitch=$pitch)"
}

/**
 * @author Kevin Ludwig
 */
object SoundPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): SoundPacket {
        val soundKey = buffer.readString()
        val sound = Sound.byKeyOrNull(soundKey)
        val position = buffer.readInt3UnsignedY().toMutableFloat3().scale(1 / 8.0f)
        val volume = buffer.readFloatLE()
        val pitch = buffer.readFloatLE()
        return SoundPacket(sound, soundKey, position, volume, pitch)
    }
}
