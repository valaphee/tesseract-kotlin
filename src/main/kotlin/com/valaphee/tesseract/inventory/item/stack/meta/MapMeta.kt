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
import com.valaphee.tesseract.util.getLongOrNull
import com.valaphee.tesseract.util.nbt.CompoundTag
import com.valaphee.tesseract.util.setBool

/**
 * @author Kevin Ludwig
 */
class MapMeta(
    name: String? = null,
    lore: List<String>? = null,
    damage: Int = 0,
    repairCost: Int = 0,
    enchantments: Map<Enchantment, Int>? = null,
    var mapId: Int? = null
) : Meta(name, lore, damage, repairCost, enchantments) {
    override fun fromTag(tag: CompoundTag) {
        super.fromTag(tag)
        mapId = tag.getLongOrNull("map_uuid")?.toInt()
    }

    override fun toTag() = super.toTag().apply {
        mapId?.let {
            setLong("map_uuid", it.toLong())
            setInt("map_name_index", it)
        }
        setBool("map_display_players", true)
    }
}
