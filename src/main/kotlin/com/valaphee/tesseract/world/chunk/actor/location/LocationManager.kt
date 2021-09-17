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
import com.valaphee.tesseract.actor.`object`
import com.valaphee.tesseract.actor.location.Input
import com.valaphee.tesseract.actor.location.Location
import com.valaphee.tesseract.actor.location.LocationManagerMessage
import com.valaphee.tesseract.actor.location.location
import com.valaphee.tesseract.data.Component
import com.valaphee.tesseract.data.block.Blocks
import com.valaphee.tesseract.util.math.calculateXOffset
import com.valaphee.tesseract.util.math.calculateYOffset
import com.valaphee.tesseract.util.math.calculateZOffset
import com.valaphee.tesseract.util.math.ceil
import com.valaphee.tesseract.util.math.floor
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.Chunk
import com.valaphee.tesseract.world.chunk.actor.ChunkActorUpdate
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
                val `object` = actor.`object`
                val chunk = message.entity
                val (chunkX, chunkZ) = chunk.position

                val oldOffset = location.position.toMutableFloat3().sub(Float3((chunkX shl 4).toFloat(), `object`.yOffset, (chunkZ shl 4).toFloat()))
                val newOffset = message.position.toMutableFloat3().sub(Float3((chunkX shl 4).toFloat(), `object`.yOffset, (chunkZ shl 4).toFloat()))
                val boundingBox = BoundingBox().set(`object`.boundingBox).move(oldOffset).add(newOffset)

                val (dx, dy, dz) = location.position - message.position
                val collisions = chunk.getCollisions(boundingBox)
                var cx = dx
                collisions.forEach { cx = it.calculateXOffset(boundingBox, cx) }
                boundingBox.move(dx, 0.0f, 0.0f)
                var cy = dy
                collisions.forEach { cy = it.calculateYOffset(boundingBox, cy) }
                boundingBox.move(0.0f, dy, 0.0f)
                var cz = dz
                collisions.forEach { cz = it.calculateZOffset(boundingBox, cz) }
                boundingBox.move(0.0f, 0.0f, cz)

                val (oldX, oldY, oldZ) = location.position
                val position = Float3(oldX + cx, oldY + cy, oldZ + cz)
                location.position = position
                val (xInt, _, zInt) = newOffset
                if (xInt < 0 || xInt >= BlockStorage.XZSize || zInt < 0 || zInt >= BlockStorage.XZSize) {
                    val context = message.context
                    context.world.sendMessage(ChunkActorUpdate(context, chunk, encodePosition(position.x.toInt() shr 4, position.z.toInt() shr 4), actor))
                }

                println("$position")
                //if (dx != cx || dy != cy || dz != cz) actor.filter<PlayerType> { it.connection.write(PlayerLocationPacket(it.id, position, location.rotation, location.headRotationYaw, PlayerLocationPacket.Mode.Teleport, true, 0, null, 0)) }
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
    for (x in xMinInt..xMaxInt) for (y in yMinInt..yMaxInt) for (z in zMinInt..zMaxInt) if (!Blocks.isTransparent(blockStorage[x, y, z])) {
        val xFloat = x.toFloat()
        val yFloat = y.toFloat()
        val zFloat = z.toFloat()
        collisions.add(BoundingBox(Float3(xFloat, yFloat, zFloat), Float3(xFloat + 1.0f, yFloat + 1.0f, zFloat + 1.0f)))
    }
    return collisions
}

