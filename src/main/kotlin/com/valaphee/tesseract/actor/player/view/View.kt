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

package com.valaphee.tesseract.actor.player.view

import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.tesseract.Config
import com.valaphee.tesseract.actor.location.Location
import com.valaphee.tesseract.actor.location.LocationManagerMessage
import com.valaphee.tesseract.world.WorldContext
import it.unimi.dsi.fastutil.longs.LongOpenHashSet

/**
 * @author Kevin Ludwig
 */
abstract class View(
    private val config: Config
) : BaseFacet<WorldContext, LocationManagerMessage>(LocationManagerMessage::class, Location::class) {
    protected val _acquiredChunks = LongOpenHashSet()
    val acquiredChunks: LongArray get() = _acquiredChunks.toLongArray()

    var distance = config.maximumViewDistance
        set(value) {
            field = Integer.min(value, config.maximumViewDistance)
        }
}
