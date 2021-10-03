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

import com.valaphee.tesseract.inventory.item.stack.Stack
import java.util.UUID

fun shapelessRecipe(type: Recipe.Type, id: UUID, name: String, inputs: Array<Stack?>, outputs: Array<Stack?>, tag: String, priority: Int, netId: Int) = Recipe(id, name, type, 0, 0, 0, 0, inputs, outputs, tag, priority, netId)

fun shapedRecipe(type: Recipe.Type, id: UUID, name: String, width: Int, height: Int, inputs: Array<Stack?>, outputs: Array<Stack?>, tag: String, priority: Int, netId: Int) = Recipe(id, name, type, 0, 0, width, height, inputs, outputs, tag, priority, netId)

fun furnaceRecipe(inputId: Int, inputSubId: Int, output: Stack?, tag: String) = Recipe(null, null, if (inputSubId != -1) Recipe.Type.FurnaceData else Recipe.Type.Furnace, inputId, inputSubId, 0, 0, null, arrayOf(output), tag, 0, 0)

fun multiRecipe(id: UUID, netId: Int) = Recipe(id, null, Recipe.Type.Multi, 0, 0, 0, 0, null, null, null, 0, netId)

/**
 * @author Kevin Ludwig
 */
class Recipe(
    val id: UUID?,
    val name: String?,
    val type: Type,
    val inputId: Int,
    val inputSubId: Int,
    val width: Int,
    val height: Int,
    val inputs: Array<Stack?>?,
    val outputs: Array<Stack?>?,
    val tag: String?,
    val priority: Int,
    val netId: Int
) {
    enum class Type {
        Shapeless,
        Shaped,
        Furnace,
        FurnaceData,
        Multi,
        ShulkerBox,
        ShapelessChemistry,
        ShapedChemistry
    }
}
