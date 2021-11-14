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
import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3

/**
 * @author Kevin Ludwig
 */
class ModelData(
    @get:JsonProperty("format_version") val version: String,
    @get:JsonProperty("minecraft:geometry") val geometry: List<Model>,
)

/**
 * @author Kevin Ludwig
 */
@JsonTypeName("minecraft:geometry")
class Model(
    @get:JsonProperty("description") val description: Description,
    @get:JsonProperty("bones") val bones: List<Bone>,
) /*: Data */{
    class Description(
        @get:JsonProperty("identifier") val key: String,
        @get:JsonProperty("texture_width") val textureWidth: Int,
        @get:JsonProperty("texture_height") val textureHeight: Int,
        @get:JsonProperty("visible_bounds_width") val visibleBoundsWidth: Float? = null,
        @get:JsonProperty("visible_bounds_height") val visibleBoundsHeight: Float? = null,
        @get:JsonProperty("visible_bounds_offset") val visibleBoundsOffset: Float3? = null,
    )

    class Bone(
        @get:JsonProperty("name") val identifier: String,
        @get:JsonProperty("cubes") val cubes: List<Cube>,
        @get:JsonProperty("pivot") val pivot: Float3? = null,
    ) {
        class Cube(
            @get:JsonProperty("origin") val origin: Float3,
            @get:JsonProperty("size") val size: Float3,
            @get:JsonProperty("uv") val uv: Map<String, Uv>
        ) {
            class Uv(
                @get:JsonProperty("uv") val uv: Float2,
                @get:JsonProperty("uv_size") val uvSize: Float2? = null
            )
        }
    }
}
