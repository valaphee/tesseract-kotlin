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

package com.valaphee.tesseract.inventory.item.craft

import com.valaphee.tesseract.inventory.item.stack.readIngredient
import com.valaphee.tesseract.inventory.item.stack.readStackInstance
import com.valaphee.tesseract.inventory.item.stack.readStackPre431
import com.valaphee.tesseract.inventory.item.stack.writeIngredient
import com.valaphee.tesseract.inventory.item.stack.writeStackInstance
import com.valaphee.tesseract.inventory.item.stack.writeStackPre431
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
class RecipesPacket(
    val recipes: Array<Recipe>,
    val potionMixRecipes: Array<PotionMixRecipe>,
    val containerMixRecipes: Array<ContainerMixRecipe>,
    val materialReducers: Array<MaterialReducer>,
    val cleanRecipes: Boolean
) : Packet() {
    override val id get() = 0x34

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarUInt(recipes.size)
        recipes.forEach {
            val type = it.type
            buffer.writeVarInt(type.ordinal)
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
                    if (type == Recipe.Type.FurnaceData) buffer.writeVarInt(it.inputSubId)
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
            buffer.writeVarInt(buffer.items.getId(it.inputKey))
            if (version >= 407) buffer.writeVarInt(it.inputSubId)
            buffer.writeVarInt(buffer.items.getId(it.reagentKey))
            if (version >= 407) buffer.writeVarInt(it.reagentSubId)
            buffer.writeVarInt(buffer.items.getId(it.outputKey))
            if (version >= 407) buffer.writeVarInt(it.outputSubId)
        }
        buffer.writeVarUInt(containerMixRecipes.size)
        containerMixRecipes.forEach {
            buffer.writeVarInt(buffer.items.getId(it.inputKey))
            buffer.writeVarInt(buffer.items.getId(it.reagentKey))
            buffer.writeVarInt(buffer.items.getId(it.outputKey))
        }
        if (version >= 465) {
            buffer.writeVarUInt(materialReducers.size)
            materialReducers.forEach {
                buffer.writeVarInt(it.inputId)
                buffer.writeVarUInt(it.itemCounts.size)
                it.itemCounts.forEach {
                    buffer.writeVarInt(it.key)
                    buffer.writeVarInt(it.value)
                }
            }
        }
        buffer.writeBoolean(cleanRecipes)
    }

    override fun handle(handler: PacketHandler) = handler.recipes(this)

    override fun toString() = "RecipesPacket(recipes=${recipes.contentToString()}, potionMixRecipes=${potionMixRecipes.contentToString()}, containerMixRecipes=${containerMixRecipes.contentToString()}, materialReducers=${materialReducers.contentToString()}, cleanRecipes=$cleanRecipes)"
}

/**
 * @author Kevin Ludwig
 */
object RecipesPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = RecipesPacket(
        Array(buffer.readVarUInt()) {
            when (val type = Recipe.Type.values()[buffer.readVarInt()]) {
                Recipe.Type.Shapeless, Recipe.Type.ShulkerBox, Recipe.Type.ShapelessChemistry -> {
                    val name = buffer.readString()
                    val inputs = Array(buffer.readVarUInt()) { buffer.readIngredient() }
                    val outputs = Array(buffer.readVarUInt()) { if (version >= 431) buffer.readStackInstance() else buffer.readStackPre431() }
                    shapelessRecipe(type, buffer.readUuid(), name, inputs, outputs, buffer.readString(), buffer.readVarInt(), if (version >= 407) buffer.readVarUInt() else 0)
                }
                Recipe.Type.Shaped, Recipe.Type.ShapedChemistry -> {
                    val name = buffer.readString()
                    val width = buffer.readVarInt()
                    val height = buffer.readVarInt()
                    val inputs = Array(width * height) { buffer.readIngredient() }
                    val outputs = Array(buffer.readVarUInt()) { if (version >= 431) buffer.readStackInstance() else buffer.readStackPre431() }
                    shapedRecipe(type, buffer.readUuid(), name, width, height, inputs, outputs, buffer.readString(), buffer.readVarInt(), if (version >= 407) buffer.readVarUInt() else 0)
                }
                Recipe.Type.Furnace, Recipe.Type.FurnaceData -> furnaceRecipe(buffer.readVarInt(), if (type == Recipe.Type.FurnaceData) buffer.readVarInt() else -1, if (version >= 431) buffer.readStackInstance() else buffer.readStackPre431(), buffer.readString())
                Recipe.Type.Multi -> multiRecipe(buffer.readUuid(), if (version >= 407) buffer.readVarUInt() else 0)
            }
        },
        Array(buffer.readVarUInt()) { PotionMixRecipe(checkNotNull(buffer.items[buffer.readVarInt()]), if (version >= 407) buffer.readVarInt() else 0, checkNotNull(buffer.items[buffer.readVarInt()]), if (version >= 407) buffer.readVarInt() else 0, checkNotNull(buffer.items[buffer.readVarInt()]), if (version >= 407) buffer.readVarInt() else 0) },
        Array(buffer.readVarUInt()) { ContainerMixRecipe(checkNotNull(buffer.items[buffer.readVarInt()]), checkNotNull(buffer.items[buffer.readVarInt()]), checkNotNull(buffer.items[buffer.readVarInt()])) },
        if (version >= 465) Array(buffer.readVarUInt()) { MaterialReducer(buffer.readVarInt(), Int2IntOpenHashMap().apply { repeat(buffer.readVarUInt()) { this[buffer.readVarInt()] = buffer.readVarInt() } }) } else emptyArray(),
        buffer.readBoolean()
    )
}
