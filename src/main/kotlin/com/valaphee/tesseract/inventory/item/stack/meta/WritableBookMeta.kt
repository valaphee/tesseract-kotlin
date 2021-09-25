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

package com.valaphee.tesseract.inventory.item.stack.meta

import com.valaphee.tesseract.inventory.item.Enchantment
import com.valaphee.tesseract.util.getListTagOrNull
import com.valaphee.tesseract.util.getString
import com.valaphee.tesseract.util.nbt.CompoundTag
import com.valaphee.tesseract.util.nbt.compoundTag
import com.valaphee.tesseract.util.nbt.listTag

/**
 * @author Kevin Ludwig
 */
open class WritableBookMeta(
    name: String? = null,
    lore: List<String>? = null,
    damage: Int = 0,
    repairCost: Int = 0,
    enchantments: Map<Enchantment, Int>? = null,
    var pages: List<String>? = null
) : Meta(name, lore, damage, repairCost, enchantments) {
    override fun fromTag(tag: CompoundTag) {
        super.fromTag(tag)
        pages = tag.getListTagOrNull("pages")?.let { it.toList().map { it.asCompoundTag()!!.getString("text") } }
    }

    override fun toTag() = super.toTag().apply {
        pages?.let { this["pages"] = listTag().apply { it.forEach { put(compoundTag().apply { setString("text", it) }) } } }
    }
}
