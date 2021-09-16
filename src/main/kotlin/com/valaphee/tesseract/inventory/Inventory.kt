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

import com.valaphee.tesseract.world.chunk.actor.location.location
import com.valaphee.tesseract.actor.player.Player
import com.valaphee.tesseract.inventory.item.stack.Stack
import com.valaphee.tesseract.net.connection
import java.util.Arrays
import kotlin.math.min

/**
 * @author Kevin Ludwig
 */
open class Inventory(
    val type: WindowType,
    protected val content: Array<Stack<*>?>
) {
    private val sessions = mutableMapOf<Player, Int>()

    constructor(type: WindowType, size: Int = type.size) : this(type, arrayOfNulls(size))

    fun setContent(content: Array<Stack<*>?>) {
        System.arraycopy(content, 0, this.content, 0, min(content.size, this.content.size))
    }

    fun getSlot(slotId: Int) = content[slotId]

    fun setSlot(slotId: Int, stack: Stack<*>?) {
        content[slotId] = stack
        writeSlot(slotId)
    }

    fun clear() {
        Arrays.fill(content, null)
        writeContent()
    }

    fun open(who: Player, windowId: Int) = sessions.putIfAbsent(who, windowId) ?: run {
        who.connection.write(WindowOpenPacket(windowId, type, who.location.position.toInt3(), who.id))
        windowId
    }

    fun close(who: Player, serverside: Boolean) {
        sessions.remove(who)?.let { who.connection.write(WindowClosePacket(it, serverside)) }
    }

    fun writeSlot(slotId: Int) {
        sessions.forEach { (player, windowId) -> player.connection.write(InventorySlotPacket(windowId, slotId, content[slotId])) }
    }

    fun writeContent() {
        sessions.forEach { (player, windowId) -> player.connection.write(InventoryContentPacket(windowId, content)) }
    }
}
