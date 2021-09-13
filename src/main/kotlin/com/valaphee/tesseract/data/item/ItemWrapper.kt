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

package com.valaphee.tesseract.data.item

import com.valaphee.foundry.math.Float3
import com.valaphee.foundry.math.Int2
import com.valaphee.tesseract.actor.player.Player
import com.valaphee.tesseract.data.Component
import com.valaphee.tesseract.util.math.Direction
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.terrain.PropagationBlockUpdateList
import org.graalvm.polyglot.Value

/**
 * @author Kevin Ludwig
 */
@Component("tesseract:item")
class ItemWrapper(
    polyglot: Value
) : Item {
    override val key: String = polyglot.getMember("key").asString()
    private val onUseBlock = polyglot.getMember("on_use_block")

    override fun onUseBlock(context: WorldContext, player: Player, chunk: Int2, blockUpdates: PropagationBlockUpdateList, x: Int, y: Int, z: Int, direction: Direction, clickPosition: Float3) = onUseBlock?.executeVoid(context, player, chunk, blockUpdates, x, y, z, direction, clickPosition)?.run { true } ?: false
}
