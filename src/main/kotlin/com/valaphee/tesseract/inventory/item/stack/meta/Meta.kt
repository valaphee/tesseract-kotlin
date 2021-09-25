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
import com.valaphee.tesseract.util.getCompoundTagOrNull
import com.valaphee.tesseract.util.getInt
import com.valaphee.tesseract.util.getIntOrNull
import com.valaphee.tesseract.util.getListTagOrNull
import com.valaphee.tesseract.util.getOrCreateCompoundTag
import com.valaphee.tesseract.util.getShort
import com.valaphee.tesseract.util.getStringOrNull
import com.valaphee.tesseract.util.nbt.CompoundTag
import com.valaphee.tesseract.util.nbt.compoundTag
import com.valaphee.tesseract.util.nbt.listTag

/**
 * @author Kevin Ludwig
 */
open class Meta(
    var name: String? = null,
    var lore: List<String>? = null,
    var damage: Int = 0,
    var repairCost: Int = 0,
    var enchantments: Map<Enchantment, Int>? = null
) {
    open fun fromTag(tag: CompoundTag) {
        tag.getCompoundTagOrNull("display")?.let {
            name = it.getStringOrNull("Name")
            lore = it.getListTagOrNull("Lore")?.let { it.toList().map { it.asArrayTag()!!.valueToString() } }
        }
        damage = tag.getIntOrNull("Damage") ?: 0
        repairCost = tag.getIntOrNull("RepairCost") ?: 0
        enchantments = tag.getListTagOrNull("ench")?.let { it.toList().map { it.asCompoundTag()!! }.associate { Enchantment.values()[it.getShort("id").toInt()] to it.getInt("lvl") } }
    }

    open fun toTag() = compoundTag().apply {
        this@Meta.name?.let { this["display"] = compoundTag().apply { setString("Name", it) } }
        lore?.let { getOrCreateCompoundTag("display").apply { this["Lore"] = listTag().apply { it.forEach { putString(it) } } } }
        if (damage != 0) setInt("Damage", damage)
        if (repairCost != 0) setInt("RepairCost", repairCost)
        enchantments?.let {
            this["ench"] = listTag().apply {
                it.forEach {
                    put(compoundTag().apply {
                        setShort("id", it.key.ordinal.toShort())
                        setShort("lvl", it.value.toShort())
                    })
                }
            }
        }
    }
}
