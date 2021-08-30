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

package com.valaphee.tesseract.inventory

import com.valaphee.tesseract.actor.location.position
import com.valaphee.tesseract.actor.player.Player
import com.valaphee.tesseract.inventory.item.stack.Stack
import com.valaphee.tesseract.net.connection

/**
 * @author Kevin Ludwig
 */
class Inventory(
    val type: WindowType,
    private val content: Array<Stack<*>?>
) {
    private val viewers = mutableSetOf<Player>()

    constructor(type: WindowType, size: Int = type.size) : this(type, arrayOfNulls(size))

    fun setContent(content: Array<Stack<*>?>): Unit = TODO()

    fun setSlot(slotId: Int, stack: Stack<*>?) {
        content[slotId] = stack
    }

    fun open(who: Player) {
        viewers += who

        who.connection.write(WindowOpenPacket(WindowId.Inventory, type, who.position.toInt3(), who.id))
    }

    fun close(who: Player) {
        viewers -= who

        who.connection.write(WindowClosePacket(WindowId.Inventory, false))
    }
}
