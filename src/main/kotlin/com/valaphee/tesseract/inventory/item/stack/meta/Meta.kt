/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.inventory.item.stack.meta

import com.valaphee.tesseract.util.getCompoundTagOrNull
import com.valaphee.tesseract.util.getIntOrNull
import com.valaphee.tesseract.util.getListTagOrNull
import com.valaphee.tesseract.util.getOrCreateCompoundTag
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
) {
    open fun fromTag(tag: CompoundTag) {
        tag.getCompoundTagOrNull("display")?.let {
            name = it.getStringOrNull("Name")
            lore = it.getListTagOrNull("Lore")?.let { it.toList().map { it.asArrayTag()!!.valueToString() } }
        }
        damage = tag.getIntOrNull("Damage") ?: 0
        repairCost = tag.getIntOrNull("RepairCost") ?: 0
    }

    open fun toTag() = compoundTag().apply {
        this@Meta.name?.let { this["display"] = compoundTag().apply { setString("Name", it) } }
        lore?.let { getOrCreateCompoundTag("display").apply { this["Lore"] = listTag().apply { it.forEach { putString(it) } } } }
        if (damage != 0) setInt("Damage", damage)
        if (repairCost != 0) setInt("RepairCost", repairCost)
    }
}
