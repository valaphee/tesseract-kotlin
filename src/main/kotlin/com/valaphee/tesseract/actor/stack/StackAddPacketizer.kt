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

package com.valaphee.tesseract.actor.stack

import com.valaphee.foundry.ecs.Consumed
import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.tesseract.actor.location.location
import com.valaphee.tesseract.actor.metadata.metadata
import com.valaphee.tesseract.data.Component
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.chunkBroadcast
import com.valaphee.tesseract.world.entity.EntityAdd
import com.valaphee.tesseract.world.filter

/**
 * @author Kevin Ludwig
 */
@Component("tesseract:stack_add_packetizer")
class StackAddPacketizer : BaseFacet<WorldContext, EntityAdd>(EntityAdd::class) {
    override suspend fun receive(message: EntityAdd): Response {
        message.entities.first().filter<StackType> {
            val context = message.context
            val location = it.location
            context.world.chunkBroadcast(context, location.position, StackAddPacket(it.id, it.id, it.stack, location.position, location.velocity, it.metadata, false))

            return Consumed
        }

        return Pass
    }
}
