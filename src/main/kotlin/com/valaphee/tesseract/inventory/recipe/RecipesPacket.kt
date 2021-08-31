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

package com.valaphee.tesseract.inventory.recipe

import com.valaphee.tesseract.inventory.item.stack.writeIngredient
import com.valaphee.tesseract.inventory.item.stack.writeStackInstance
import com.valaphee.tesseract.inventory.item.stack.writeStackPre431
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Clientbound)
data class RecipesPacket(
    var recipes: Array<Recipe>,
    var potionMixRecipes: Array<PotionMixRecipe>,
    var containerMixRecipes: Array<ContainerMixRecipe>,
    var cleanRecipes: Boolean
) : Packet {
    override val id get() = 0x34

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarUInt(recipes.size)
        recipes.forEach {
            val type = it.type
            buffer.writeVarInt(type!!.ordinal)
            when (type) {
                Recipe.Type.Shapeless, Recipe.Type.ShulkerBox, Recipe.Type.ShapelessChemistry -> {
                    buffer.writeString(it.name!!)
                    it.inputs!!.let {
                        buffer.writeVarUInt(it.size)
                        it.forEach { buffer.writeIngredient(it) }
                    }
                    it.outputs!!.let {
                        buffer.writeVarUInt(it.size)
                        it.forEach { if (version >= 431) buffer.writeStackInstance(it) else buffer.writeStackPre431(it) }
                    }
                    buffer.writeUuid(it.id!!)
                    buffer.writeString(it.tag!!)
                    buffer.writeVarInt(it.priority)
                    if (version >= 407) buffer.writeVarUInt(it.netId)
                }
                Recipe.Type.Shaped, Recipe.Type.ShapedChemistry -> {
                    buffer.writeString(it.name!!)
                    buffer.writeVarInt(it.width)
                    buffer.writeVarInt(it.height)
                    it.inputs!!.forEach { buffer.writeIngredient(it) }
                    it.outputs!!.let {
                        buffer.writeVarUInt(it.size)
                        it.forEach { if (version >= 431) buffer.writeStackInstance(it) else buffer.writeStackPre431(it) }
                    }
                    buffer.writeUuid(it.id!!)
                    buffer.writeString(it.tag!!)
                    buffer.writeVarInt(it.priority)
                    if (version >= 407) buffer.writeVarUInt(it.netId)
                }
                Recipe.Type.Furnace, Recipe.Type.FurnaceData -> {
                    buffer.writeVarInt(it.inputId)
                    if (type == Recipe.Type.FurnaceData) buffer.writeVarInt(it.inputDamage)
                    if (version >= 431) buffer.writeStackInstance(it.outputs!![0]) else buffer.writeStackPre431(it.outputs!![0])
                    buffer.writeString(it.tag!!)
                }
                Recipe.Type.Multi -> {
                    buffer.writeUuid(it.id!!)
                    buffer.writeVarUInt(it.netId)
                }
            }
        }
        buffer.writeVarUInt(potionMixRecipes.size)
        potionMixRecipes.forEach {
            buffer.writeVarInt(it.inputId)
            if (version >= 407) buffer.writeVarInt(it.inputSubId)
            buffer.writeVarInt(it.reagentId)
            if (version >= 407) buffer.writeVarInt(it.reagentSubId)
            buffer.writeVarInt(it.outputId)
            if (version >= 407) buffer.writeVarInt(it.outputSubId)
        }
        buffer.writeVarUInt(containerMixRecipes.size)
        containerMixRecipes.forEach {
            buffer.writeVarInt(it.inputId)
            buffer.writeVarInt(it.reagentId)
            buffer.writeVarInt(it.outputId)
        }
        buffer.writeBoolean(cleanRecipes)
    }

    override fun handle(handler: PacketHandler) = handler.recipes(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecipesPacket

        if (!recipes.contentEquals(other.recipes)) return false
        if (!potionMixRecipes.contentEquals(other.potionMixRecipes)) return false
        if (!containerMixRecipes.contentEquals(other.containerMixRecipes)) return false
        if (cleanRecipes != other.cleanRecipes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = recipes.contentHashCode()
        result = 31 * result + potionMixRecipes.contentHashCode()
        result = 31 * result + containerMixRecipes.contentHashCode()
        result = 31 * result + cleanRecipes.hashCode()
        return result
    }
}
