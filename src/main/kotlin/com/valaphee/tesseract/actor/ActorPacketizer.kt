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

package com.valaphee.tesseract.actor

import com.valaphee.foundry.ecs.Consumed
import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.actor.attribute._attributes
import com.valaphee.tesseract.actor.location.position
import com.valaphee.tesseract.actor.location.rotation
import com.valaphee.tesseract.actor.metadata.metadata
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.broadcast
import com.valaphee.tesseract.world.entity.EntityAdd
import com.valaphee.tesseract.world.entity.EntityManagerMessage
import com.valaphee.tesseract.world.filterType

/**
 * @author Kevin Ludwig
 */
class ActorPacketizer : BaseFacet<WorldContext, EntityManagerMessage>(EntityManagerMessage::class) {
    override suspend fun receive(message: EntityManagerMessage): Response {
        when (message) {
            is EntityAdd -> message.entities.filterType<ActorType>().forEach {
                message.context.world.broadcast(ActorAddPacket(it.id, it.id, it.type, it.position, Float3.Zero, it.rotation, 0.0f, it._attributes, it.metadata, emptyArray())) // TODO replace broadcast

                return Consumed
            }
        }

        return Pass
    }
}
