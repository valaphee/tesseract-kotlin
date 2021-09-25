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
data class SoundStopPacket(
    val sound: Sound? = null,
    val soundKey: String? = null
) : Packet {
    constructor(sound: Sound) : this(sound, null)

    constructor(soundName: String) : this(null, soundName)

    override val id get() = 0x57

    override fun write(buffer: PacketBuffer, version: Int) {
        val soundKey = sound?.key ?: soundKey
        soundKey?.let {
            buffer.writeString(it)
            buffer.writeBoolean(false)
        } ?: run {
            buffer.writeString("")
            buffer.writeBoolean(true)
        }
    }

    override fun handle(handler: PacketHandler) = handler.soundStop(this)
}

/**
 * @author Kevin Ludwig
 */
object SoundStopPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): SoundStopPacket {
        val _soundKey = buffer.readString()
        val sound: Sound?
        val soundKey: String?
        if (!buffer.readBoolean()) {
            sound = Sound.byKeyOrNull(_soundKey)
            soundKey = _soundKey
        } else {
            sound = null
            soundKey = null
        }
        return SoundStopPacket(sound, soundKey)
    }
}
