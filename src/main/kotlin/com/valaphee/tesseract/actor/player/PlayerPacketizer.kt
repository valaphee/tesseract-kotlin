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

package com.valaphee.tesseract.actor.player

import com.valaphee.tesseract.actor.ActorPacketizer
import com.valaphee.tesseract.actor.AnyActorOfWorld
import com.valaphee.tesseract.actor.location.location
import com.valaphee.tesseract.actor.metadata.metadata
import com.valaphee.tesseract.data.Component
import com.valaphee.tesseract.data.entity.Runtime
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.world.filter

/**
 * @author Kevin Ludwig
 */
@Runtime
@Component("tesseract:player.packetizer")
open class PlayerPacketizer : ActorPacketizer() {
    override fun addPacket(actor: AnyActorOfWorld): Packet {
        actor.filter<PlayerType> {
            val location = it.location
            return PlayerAddPacket(it.authExtra.userId, it.authExtra.userName, it.id, it.id, "", location.position, location.velocity, location.rotation, location.headRotationYaw, null, it.metadata, 0, emptyArray(), "", it.user.operatingSystem)
        }
        return super.addPacket(actor)
    }
}
