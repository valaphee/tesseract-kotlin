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

package com.valaphee.tesseract.world.chunk.actor

import com.valaphee.foundry.ecs.system.BaseBehavior
import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.actor.location.`object`
import com.valaphee.tesseract.actor.location.location
import com.valaphee.tesseract.data.Component
import com.valaphee.tesseract.world.AnyEntityOfWorld
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.ChunkType
import com.valaphee.tesseract.world.chunk.encodePosition
import com.valaphee.tesseract.world.chunk.position
import com.valaphee.tesseract.world.chunk.terrain.BlockStorage
import com.valaphee.tesseract.world.filter

/**
 * @author Kevin Ludwig
 */
@Component("tesseract:chunk.actor_updater")
class ActorUpdater : BaseBehavior<WorldContext>() {
    override suspend fun update(entity: AnyEntityOfWorld, context: WorldContext): Boolean {
        entity.filter<ChunkType> { chunk ->
            chunk.actors.forEach {
                val location = it.location
                val `object` = it.`object`
                val (chunkX, chunkZ) = chunk.position
                val offset = location.position.toMutableFloat3().sub(Float3((chunkX shl 4).toFloat(), `object`.yOffset, (chunkZ shl 4).toFloat()))
                /*val boundingBox = BoundingBox().set(`object`.boundingBox).move(offset)
                chunk.getCollisions(boundingBox).forEach {}*/

                val (x, _, z) = offset.toInt3()
                if (x < 0 || x >= BlockStorage.XZSize || z < 0 || z >= BlockStorage.XZSize) {
                    val (newX, _, newZ) = location.position.toInt3()
                    context.world.sendMessage(ChunkActorUpdate(context, chunk, encodePosition(newX shr 4, newZ shr 4), it))
                }
            }
        }

        return true
    }
}
