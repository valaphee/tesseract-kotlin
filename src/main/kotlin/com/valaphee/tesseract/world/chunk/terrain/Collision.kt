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

package com.valaphee.tesseract.world.chunk.terrain

import com.valaphee.foundry.math.Float3
import com.valaphee.foundry.math.collision.BoundingBox
import com.valaphee.tesseract.data.block.Blocks
import com.valaphee.tesseract.util.math.ceil
import com.valaphee.tesseract.util.math.floor
import com.valaphee.tesseract.world.chunk.Chunk

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
