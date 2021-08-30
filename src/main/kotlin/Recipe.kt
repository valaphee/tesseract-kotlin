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

import com.valaphee.tesseract.inventory.item.stack.Stack
import java.util.UUID

fun shapelessRecipe(type: Recipe.Type, id: UUID, name: String, inputs: Array<Stack<*>?>, outputs: Array<Stack<*>?>, tag: String, priority: Int, netId: Int) = Recipe(id, name, type, 0, 0, 0, 0, inputs, outputs, tag, priority, netId)

fun shapedRecipe(type: Recipe.Type, id: UUID, name: String, width: Int, height: Int, inputs: Array<Stack<*>?>, outputs: Array<Stack<*>?>, tag: String, priority: Int, netId: Int) = Recipe(id, name, type, 0, 0, width, height, inputs, outputs, tag, priority, netId)

fun furnaceRecipe(inputId: Int, inputSubId: Int, output: Stack<*>?, tag: String) = Recipe(null, null, if (inputSubId != -1) Recipe.Type.FurnaceData else Recipe.Type.Furnace, inputId, inputSubId, 0, 0, null, arrayOf(output), tag, 0, 0)

fun multiRecipe(id: UUID, netId: Int) = Recipe(id, null, Recipe.Type.Multi, 0, 0, 0, 0, null, null, null, 0, netId)

/**
 * @author Kevin Ludwig
 */
data class Recipe(
    var id: UUID?,
    var name: String?,
    var type: Type?,
    var inputId: Int,
    var inputDamage: Int,
    var width: Int,
    var height: Int,
    var inputs: Array<Stack<*>?>?,
    var outputs: Array<Stack<*>?>?,
    var tag: String?,
    var priority: Int,
    var netId: Int
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Recipe

        if (id != other.id) return false
        if (name != other.name) return false
        if (type != other.type) return false
        if (inputId != other.inputId) return false
        if (inputDamage != other.inputDamage) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (inputs != null) {
            if (other.inputs == null) return false
            if (!inputs.contentEquals(other.inputs)) return false
        } else if (other.inputs != null) return false
        if (outputs != null) {
            if (other.outputs == null) return false
            if (!outputs.contentEquals(other.outputs)) return false
        } else if (other.outputs != null) return false
        if (tag != other.tag) return false
        if (priority != other.priority) return false
        if (netId != other.netId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + inputId
        result = 31 * result + inputDamage
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + (inputs?.contentHashCode() ?: 0)
        result = 31 * result + (outputs?.contentHashCode() ?: 0)
        result = 31 * result + (tag?.hashCode() ?: 0)
        result = 31 * result + priority
        result = 31 * result + netId
        return result
    }
}
