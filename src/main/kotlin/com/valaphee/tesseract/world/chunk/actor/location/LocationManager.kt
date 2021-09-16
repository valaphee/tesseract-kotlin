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

package com.valaphee.tesseract.world.chunk.actor.location

import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.foundry.math.Float3
import com.valaphee.foundry.math.collision.BoundingBox
import com.valaphee.tesseract.data.Component
import com.valaphee.tesseract.util.math.ceil
import com.valaphee.tesseract.util.math.floor
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.Chunk
import com.valaphee.tesseract.world.chunk.actor.ChunkActorUpdate
import com.valaphee.tesseract.world.chunk.actor.chunk
import com.valaphee.tesseract.world.chunk.encodePosition
import com.valaphee.tesseract.world.chunk.position
import com.valaphee.tesseract.world.chunk.terrain.BlockStorage
import com.valaphee.tesseract.world.chunk.terrain.blockStorage

/**
 * @author Kevin Ludwig
 */
@Component("tesseract:chunk.actor_location_manager")
class LocationManager : BaseFacet<WorldContext, LocationManagerMessage>(LocationManagerMessage::class, Location::class) {
    override suspend fun receive(message: LocationManagerMessage): Response {
        when (message) {
            is Input -> {
                val actor = message.source
                val location = actor.location
                val position = message.position
                location.position = position
                val (xInt, _, zInt) = position.toInt3()
                if (xInt < 0 || xInt >= BlockStorage.XZSize || zInt < 0 || zInt >= BlockStorage.XZSize) {
                    val (newX, _, newZ) = location.position.toInt3()
                    val context = message.context
                    context.world.sendMessage(ChunkActorUpdate(context, message.entity, encodePosition(newX shr 4, newZ shr 4), actor))
                }

                val chunk = message.entity
                val (chunkX, chunkZ) = chunk.position
                val offset = location.position.toMutableFloat3().add(Float3(-(chunkX shl 4).toFloat() + deltaX, -`object`.yOffset + deltaY, -(chunkZ shl 4).toFloat() + deltaZ ))
                val boundingBox = BoundingBox().set(`object`.boundingBox).move(offset)
                actor.chunk.getCollisions()
            }
        }

        return Pass
    }
}

fun Chunk.getCollisions(boundingBox: BoundingBox): List<BoundingBox> {
    val (xMin, yMin, zMin) = boundingBox.minimum
    val (xMax, yMax, zMax) = boundingBox.maximum
    val xMinInt = floor(xMin)
    val yMinInt = floor(yMin)
    val zMinInt = floor(zMin)
    val xMaxInt = ceil(xMax)
    val yMaxInt = ceil(yMax)
    val zMaxInt = ceil(zMax)
    val collisions = mutableListOf<BoundingBox>()
    for (x in xMinInt..xMaxInt) for (y in yMinInt..yMaxInt) for (z in zMinInt..zMaxInt) if (!com.valaphee.tesseract.data.block.Blocks.isTransparent(blockStorage[x, y, z])) {
        val xFloat = x.toFloat()
        val yFloat = y.toFloat()
        val zFloat = z.toFloat()
        collisions.add(BoundingBox(Float3(xFloat, yFloat, zFloat), Float3(xFloat + 1.0f, yFloat + 1.0f, zFloat + 1.0f)))
    }
    return collisions
}
