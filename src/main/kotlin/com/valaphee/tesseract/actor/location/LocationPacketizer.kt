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

package com.valaphee.tesseract.actor.location

import com.valaphee.foundry.ecs.Consumed
import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.foundry.math.Int3
import com.valaphee.foundry.math.isZero
import com.valaphee.tesseract.actor.ActorType
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.broadcast
import com.valaphee.tesseract.world.whenTypeIs

/**
 * @author Kevin Ludwig
 */
class LocationPacketizer : BaseFacet<WorldContext, LocationManagerMessage>(LocationManagerMessage::class, Location::class) {
    override suspend fun receive(message: LocationManagerMessage): Response {
        message.entity?.whenTypeIs<ActorType> {
            message.context.world.broadcast(
                when (message) {
                    is Move -> {
                        val (x, y, z) = it.position
                        MoveRotatePacket(it, Int3.Zero, Float3(if (message.move.x.isZero()) Float.NaN else x, if (message.move.y.isZero()) Float.NaN else y, if (message.move.z.isZero()) Float.NaN else z), Float2(Float.NaN), Float.NaN, true, false, false)
                    }
                    is MoveRotate -> {
                        val (x, y, z) = it.position
                        val (yaw, pitch) = it.rotation
                        MoveRotatePacket(it, Int3.Zero, Float3(if (message.move.x.isZero()) Float.NaN else x, if (message.move.y.isZero()) Float.NaN else y, if (message.move.z.isZero()) Float.NaN else z), Float2(pitch, yaw), Float.NaN, true, false, false)
                    }
                    is Rotate -> {
                        val (yaw, pitch) = it.rotation
                        MoveRotatePacket(it, Int3.Zero, Float3(Float.NaN), Float2(pitch, yaw), Float.NaN, true, false, false)
                    }
                    is Teleport -> {
                        val (yaw, pitch) = it.rotation
                        TeleportPacket(it, it.position, Float2(pitch, yaw), Float.NaN, true, false)
                    }
                }
            ) // TODO replace broadcast

            return Consumed
        }

        return Pass
    }
}
