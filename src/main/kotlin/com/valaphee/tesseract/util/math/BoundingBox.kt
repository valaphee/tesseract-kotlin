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

package com.valaphee.tesseract.util.math

import com.valaphee.foundry.math.collision.BoundingBox

fun BoundingBox.calculateXOffset(boundingBox: BoundingBox, x: Float): Float {
    if (boundingBox.maximum.y <= minimum.y || boundingBox.minimum.y >= maximum.y || boundingBox.maximum.z <= minimum.z || boundingBox.minimum.z >= maximum.z) return x
    if (x > 0 && boundingBox.maximum.x <= minimum.x) {
        val x0 = minimum.x - boundingBox.maximum.x
        if (x0 < x) return x0
    }
    if (x < 0 && boundingBox.minimum.x >= maximum.x) {
        val x0 = maximum.x - boundingBox.minimum.x
        if (x0 > x) return x0
    }
    return x
}

fun BoundingBox.calculateYOffset(boundingBox: BoundingBox, y: Float): Float {
    if (boundingBox.maximum.x <= minimum.x || boundingBox.minimum.x >= maximum.x || boundingBox.maximum.z <= minimum.z || boundingBox.minimum.z >= maximum.z) return y
    if (y > 0 && boundingBox.maximum.y <= minimum.y) {
        val y0 = minimum.y - boundingBox.maximum.y
        if (y0 < y) return y0
    }
    if (y < 0 && boundingBox.minimum.y >= maximum.y) {
        val y0 = maximum.y - boundingBox.minimum.y
        if (y0 > y) return y0
    }
    return y
}

fun BoundingBox.calculateZOffset(boundingBox: BoundingBox, z: Float): Float {
    if (boundingBox.maximum.x <= minimum.x || boundingBox.minimum.x >= maximum.x || boundingBox.maximum.y <= minimum.y || boundingBox.minimum.y >= maximum.y) return z
    if (z > 0 && boundingBox.maximum.z <= minimum.z) {
        val z0 = minimum.z - boundingBox.maximum.x
        if (z0 < z) return z0
    }
    if (z < 0 && boundingBox.minimum.x >= maximum.z) {
        val z0 = maximum.z - boundingBox.minimum.z
        if (z0 > z) return z0
    }
    return z
}
