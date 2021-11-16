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

package com.valaphee.tesseract.pack.block

import com.fasterxml.jackson.annotation.JsonProperty
import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.data.DataType
import com.valaphee.tesseract.pack.Component
import com.valaphee.tesseract.pack.WrapperComponent
import com.valaphee.tesseract.util.nbt.Nbt
import com.valaphee.tesseract.util.nbt.compoundTag
import com.valaphee.tesseract.util.nbt.toTag
import com.valaphee.tesseract.util.setBool

/**
 * @author Kevin Ludwig
 */
interface BlockComponent : Component

/**
 * @author Kevin Ludwig
 */
@DataType("minecraft:block_light_absorption")
class BlockLightAbsorptionComponent(
    value: Float
) : BlockComponent, WrapperComponent<Float>(value), Nbt {
    override fun toTag() = compoundTag().apply { setFloat("value", value) }
}

/**
 * @author Kevin Ludwig
 */
@DataType("minecraft:creative_category")
class CreativeCategoryComponent(
    @get:JsonProperty("category") val category: String,
    @get:JsonProperty("group") val group: String
) : BlockComponent, Nbt {
    override fun toTag() = compoundTag().apply {
        setString("category", category)
        setString("group", group)
    }
}

/**
 * @author Kevin Ludwig
 */
@DataType("minecraft:destroy_time")
class DestroyTimeComponent(
    value: Float
) : BlockComponent, WrapperComponent<Float>(value), Nbt {
    override fun toTag() = compoundTag().apply { setFloat("value", value) }
}

/**
 * @author Kevin Ludwig
 */
@DataType("minecraft:entity_collision")
class EntityCollisionComponent(
    @get:JsonProperty("origin") val origin: Float3,
    @get:JsonProperty("size") val size: Float3
) : BlockComponent, Nbt {
    override fun toTag() = compoundTag().apply {
        setBool("enabled", true)
        this["origin"] = origin.toMutableFloat3().vector.toList().toTag()
        this["size"] = this@EntityCollisionComponent.size.toMutableFloat3().vector.toList().toTag()
    }
}

/**
 * @author Kevin Ludwig
 */
@DataType("minecraft:explosion_resistance")
class ExplosionResistanceComponent(
    value: Float
) : BlockComponent, WrapperComponent<Float>(value), Nbt {
    override fun toTag() = compoundTag().apply { setFloat("value", value) }
}

/**
 * @author Kevin Ludwig
 */
@DataType("minecraft:geometry")
class GeometryComponent(
    value: String
) : BlockComponent, WrapperComponent<String>(value), Nbt {
    override fun toTag() = compoundTag().apply { setString("value", value) }
}

/**
 * @author Kevin Ludwig
 */
@DataType("minecraft:material_instances")
class MaterialInstancesComponent(
    value: Map<String, Material>,
) : BlockComponent, WrapperComponent<Map<String, MaterialInstancesComponent.Material>>(value), Nbt {
    class Material(
        @get:JsonProperty("texture") val texture: String,
        @get:JsonProperty("render_method") val renderMethod: String
    ) : Nbt {
        override fun toTag() = compoundTag().apply {
            setString("texture", texture)
            setString("render_method", renderMethod)
        }
    }

    override fun toTag() = compoundTag().apply {
        this["mappings"] = compoundTag()
        this["materials"] = value.toTag()
    }
}

/**
 * @author Kevin Ludwig
 */
@DataType("minecraft:on_player_placing")
class OnPlayerPlacingComponent(
    @get:JsonProperty("event") val event: String
) : BlockComponent, Nbt {
    override fun toTag() = compoundTag().apply { setString("triggerType", event) }
}

/**
 * @author Kevin Ludwig
 */
@DataType("minecraft:pick_collision")
class PickCollisionComponent(
    @get:JsonProperty("origin") val origin: Float3,
    @get:JsonProperty("size") val size: Float3
) : BlockComponent, Nbt {
    override fun toTag() = compoundTag().apply {
        setBool("enabled", true)
        this["origin"] = origin.toMutableFloat3().vector.toList().toTag()
        this["size"] = this@PickCollisionComponent.size.toMutableFloat3().vector.toList().toTag()
    }
}

/**
 * @author Kevin Ludwig
 */
@DataType("minecraft:rotation")
class RotationComponent(
    value: Float3
) : BlockComponent, WrapperComponent<Float3>(value), Nbt {
    override fun toTag() = compoundTag().apply {
        setFloat("x", value.x)
        setFloat("y", value.y)
        setFloat("z", value.z)
    }
}
