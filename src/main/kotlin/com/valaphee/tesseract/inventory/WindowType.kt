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

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap

/**
 * @author Kevin Ludwig
 */
enum class WindowType(
    val id: Int,
    val size: Int = 0
) {
    None(-9),
    Inventory(-1, 9 * 4),
    Container(0, 9 * 6),
    Workbench(1, 3 * 3 + 1),
    Furnace(2, 3),
    EnchantmentTable(3, 2),
    BrewingStand(4, 5),
    Anvil(5, 3),
    Dispenser(6, 3 * 3),
    Dropper(7, 3 * 3),
    Hopper(8, 5),
    Cauldron(9, 1),
    MinecartChest(10, 9 * 3),
    MinecartHopper(11, 5),
    Horse(12, 9 * 3),
    Beacon(13, 1),
    StructureEditor(14),
    Merchant(15, 3),
    CommandBlock(16),
    Jukebox(17),
    CompoundCreator(20),
    ElementConstructor(21),
    MaterialReducer(22),
    LabTable(23),
    Loom(24, 4),
    Lectern(25, 1),
    Grindstone(26, 3),
    BlastFurnace(27, 3),
    Smoker(28, 3),
    Stonecutter(29, 2),
    Cartography(30, 3),
    Hud(31),
    JigsawEditor(32),
    SmithingTable(33);

    companion object {
        fun byId(id: Int) = byId[id]

        private val byId = Int2ObjectOpenHashMap<WindowType>(values().size).apply { values().forEach { this[it.id] = it } }
    }
}
