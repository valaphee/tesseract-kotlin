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

package com.valaphee.tesseract.actor.player

import com.valaphee.foundry.ecs.BaseAttribute
import com.valaphee.tesseract.data.entity.Runtime
import com.valaphee.tesseract.inventory.Inventory
import com.valaphee.tesseract.inventory.OneSlotInventory
import com.valaphee.tesseract.inventory.PlayerInventory
import com.valaphee.tesseract.inventory.UiInventory
import com.valaphee.tesseract.inventory.WindowId
import com.valaphee.tesseract.inventory.WindowSlotType
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap

/**
 * @author Kevin Ludwig
 */
@Runtime
class WindowManager(
    playerInventory: PlayerInventory
) : BaseAttribute() {
    val inventories: Int2ObjectMap<Inventory> = Int2ObjectOpenHashMap<Inventory>().apply {
        this[WindowId.CraftingResult] = OneSlotInventory()
        this[WindowId.Inventory] = playerInventory
        this[WindowId.Ui] = UiInventory()
    }

    fun select(slotType: WindowSlotType, slotId: Int): Pair<Inventory, Int> {
        return when (slotType) {
            WindowSlotType.HotbarAndInventory, WindowSlotType.Hotbar, WindowSlotType.Inventory -> inventories[WindowId.Inventory] to slotId
            WindowSlotType.Cursor -> inventories[WindowId.Ui] to 0
            WindowSlotType.CreativeOutput -> inventories[WindowId.CraftingResult] to 0
            else -> TODO()
        }
    }
}

val Player.windowManager get() = findAttribute(WindowManager::class)

fun Player.openWindow(inventory: Inventory) {
    findAttribute(WindowManager::class).inventories[0] = inventory
    inventory.open(this, 0)
}

fun Player.closeWindow(windowId: Int, serverside: Boolean = true) {
    findAttribute(WindowManager::class).inventories[windowId]?.close(this, serverside)
}
