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

package com.valaphee.tesseract.pack

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.util.nbt.CustomTag
import com.valaphee.tesseract.util.nbt.compoundTag
import com.valaphee.tesseract.util.nbt.listTag
import com.valaphee.tesseract.util.nbt.ofBool
import com.valaphee.tesseract.util.nbt.toTag
import com.valaphee.tesseract.util.setBool
import jdk.jfr.Event

/**
 * @author Kevin Ludwig
 */
@JsonTypeName("minecraft:block")
class Block(
    @get:JsonProperty("description") val description: Description,
    @get:JsonProperty("events") val events: Map<String, Map<String, Any>>? = null,
    @get:JsonProperty("components") val components: Components? = null,
    @get:JsonProperty("permutations") val permutations: List<Permutation>? = null
) : Data, CustomTag {
    class Description(
        @get:JsonProperty("identifier") val key: String,
        @get:JsonProperty("properties") val properties: LinkedHashMap<String, LinkedHashSet<*>>? = null
    )

    class Permutation(
        @get:JsonProperty("condition") val condition: String,
        @get:JsonProperty("components") val components: Components
    ) : CustomTag {
        override fun toTag() = compoundTag().apply {
            setString("condition", condition)
            this["components"] = components.toTag()
        }
    }

    class Components(
        @get:JsonProperty("minecraft:creative_category") var creativeCategory: CreativeCategory? = null,
        @get:JsonProperty("minecraft:entity_collision") var entityCollision: Collision? = null,
        @get:JsonProperty("minecraft:geometry") var geometry: String? = null,
        @get:JsonProperty("minecraft:destroy_time") var hardness: Float? = null,
        @get:JsonProperty("minecraft:block_light_absorption") var lightAbsorption: Float? = null,
        @get:JsonProperty("minecraft:material_instances") var materialInstances: Map<String, MaterialInstance>? = null,
        @get:JsonProperty("minecraft:on_player_placing") var onPlayerPlacing: Event? = null,
        @get:JsonProperty("minecraft:pick_collision") var pickCollision: Collision? = null,
        @get:JsonProperty("minecraft:explosion_resistance") var resistance: Float? = null,
        @get:JsonProperty("minecraft:rotation") var rotation: Float3? = null
    ) : CustomTag {
        class CreativeCategory(
            @get:JsonProperty("category") val category: String,
            @get:JsonProperty("group") val group: String
        ) : CustomTag {
            override fun toTag() = compoundTag().apply {
                setString("category", category)
                setString("group", group)
            }
        }

        class Collision(
            @get:JsonProperty("origin") val origin: Float3,
            @get:JsonProperty("size") val size: Float3
        ) : CustomTag {
            override fun toTag() = compoundTag().apply {
                setBool("enabled", true)
                this["origin"] = origin.toMutableFloat3().vector.toList().toTag()
                this["size"] = this@Collision.size.toMutableFloat3().vector.toList().toTag()
            }
        }

        class Event(
            @get:JsonProperty("event") val event: String
        ) : CustomTag {
            override fun toTag() = compoundTag().apply { setString("triggerType", event) }
        }

        class MaterialInstance(
            @get:JsonProperty("texture") val texture: String,
            @get:JsonProperty("render_method") val renderMethod: String
        ) : CustomTag {
            override fun toTag() = compoundTag().apply {
                setString("texture", texture)
                setString("render_method", renderMethod)
            }
        }

        override fun toTag() = compoundTag().apply {
            /*creativeCategory?.let { this["minecraft:creative_category"] = it.toTag() }*/
            entityCollision?.let { this["minecraft:entity_collision"] = it.toTag() }
            geometry?.let { this["minecraft:geometry"] = compoundTag().apply { setString("value", it) } }
            hardness?.let { this["minecraft:destroy_time"] = compoundTag().apply { setFloat("value", it) } }
            lightAbsorption?.let { this["minecraft:block_light_absorption"] = compoundTag().apply { setFloat("value", it) } }
            materialInstances?.let {
                this["minecraft:material_instances"] = compoundTag().apply {
                    this["mappings"] = compoundTag()
                    this["materials"] = it.toTag()
                }
            }
            onPlayerPlacing?.let { this["minecraft:on_player_placing"] = it.toTag() }
            pickCollision?.let { this["minecraft:pick_collision"] = it.toTag() }
            resistance?.let { this["minecraft:explosion_resistance"] = compoundTag().apply { setFloat("value", it) } }
            rotation?.let {
                this["minecraft:rotation"] = compoundTag().apply {
                    setFloat("x", it.x)
                    setFloat("y", it.y)
                    setFloat("z", it.z)
                }
            }
        }
    }

    override fun toTag() = compoundTag().apply {
        setInt("molangVersion", 0)
        description.properties?.let {
            if (it.isNotEmpty()) this["properties"] = listTag().apply {
                description.properties.forEach {
                    put(compoundTag().apply {
                        setString("name", it.key)
                        this["enum"] = listTag().apply {
                            when (it.value.first()) {
                                is Boolean -> it.value.forEach { put(ofBool(it as Boolean)) }
                                is Int -> it.value.forEach { putInt(it as Int) }
                                is String -> it.value.forEach { putString(it as String) }
                                else -> TODO()
                            }
                        }
                    })
                }
            }
        }
        components?.let { this["components"] = components.toTag() }
        permutations?.let { if (permutations.isNotEmpty()) this["permutations"] = it.toTag() }
    }
}
