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

package com.valaphee.tesseract.util.nbs

import com.valaphee.foundry.ecs.system.BaseBehavior
import com.valaphee.tesseract.actor.location.position
import com.valaphee.tesseract.actor.player.PlayerType
import com.valaphee.tesseract.net.connection
import com.valaphee.tesseract.world.AnyEntityOfWorld
import com.valaphee.tesseract.world.SoundEvent
import com.valaphee.tesseract.world.SoundEventPacket
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.whenTypeIs
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import java.io.DataInputStream
import java.io.FileInputStream

/**
 * @author Kevin Ludwig
 */
class SongBroadcaster : BaseBehavior<WorldContext>() {
    private val song = DataInputStream(FileInputStream("song.nbs")).readSong()
    private var wait = 0
    private var position = 0

    override suspend fun update(entity: AnyEntityOfWorld, context: WorldContext): Boolean {
        entity.whenTypeIs<PlayerType> {
            if (wait-- == 0) {
                song.layers.values.forEach { layer -> layer.notes[position]?.let { note -> it.connection.write(SoundEventPacket(SoundEvent.Note, it.position, (instrumentMappings[note.instrument] shl 8) or (note.pitch - 0x21), "", false, true)) } }
                if (position++ > song.length) position = 0
                wait = song.delay.toInt()
            }
        }

        return true
    }

    companion object {
        private val instrumentMappings = Int2IntOpenHashMap().apply {
            this[0x00] = 0x00
            this[0x01] = 0x04
            this[0x02] = 0x01
            this[0x03] = 0x02
            this[0x04] = 0x03
            this[0x05] = 0x08
            this[0x06] = 0x06
            this[0x07] = 0x05
            this[0x08] = 0x07
            this[0x09] = 0x09
            this[0x0A] = 0x0A
            this[0x0B] = 0x0B
            this[0x0C] = 0x0C
            this[0x0D] = 0x0D
            this[0x0E] = 0x0E
            this[0x0F] = 0x0F
        }
    }
}
