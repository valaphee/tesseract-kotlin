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

package com.valaphee.tesseract.inventory.item

import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.actor.insentient.chicken
import com.valaphee.tesseract.world.chunk.terrain.block.BlockState
import com.valaphee.tesseract.world.entity.addEntities

/**
 * @author Kevin Ludwig
 */
object Items {
    fun populate() {
        fun bucketEmptyAndFill(id: Int): OnUseBlock = { _, _, _, blockUpdates, x, y, z, direction, _ ->
            val (xOffset, yOffset, zOffset) = direction.axis
            blockUpdates[x + xOffset, y + yOffset, z + zOffset] = id
        }
        Item.byKeyOrNull("minecraft:water_bucket")?.apply { BlockState.byKeyWithStatesOrNull("minecraft:flowing_water[liquid_depth=0]")?.id?.let { onUseBlock = bucketEmptyAndFill(it) } }
        Item.byKeyOrNull("minecraft:lava_bucket")?.apply { BlockState.byKeyWithStatesOrNull("minecraft:flowing_lava[liquid_depth=0]")?.id?.let { onUseBlock = bucketEmptyAndFill(it) } }
        Item.byKeyOrNull("minecraft:chicken_spawn_egg")?.apply { onUseBlock = { context, player, chunk, _, x, y, z, _, clickPosition -> context.world.addEntities(context, player, context.entityFactory.chicken(Float3(x + chunk.x * 16 + clickPosition.x, y + clickPosition.y, z + chunk.y * 16 + clickPosition.z), Float2.Zero)) } }
    }
}
