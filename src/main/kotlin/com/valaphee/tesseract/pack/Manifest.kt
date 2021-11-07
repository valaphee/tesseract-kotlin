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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.valaphee.tesseract.util.Version
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Manifest(
    @get:JsonProperty("format_version") val version: String,
    @get:JsonProperty("header") val header: Header,
    @get:JsonProperty("modules") val modules: List<Module>?,
    @get:JsonProperty("metadata") val metadata: Metadata?,
    @get:JsonProperty("dependencies") val dependencies: List<Dependency>?,
    @get:JsonProperty("capabilities") val capabilities: List<String>?,
    @get:JsonProperty("subpacks") val subPacks: List<SubPack>?
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    class Header(
        @get:JsonProperty("uuid") val id: UUID,
        @get:JsonProperty("name") val name: String,
        @get:JsonProperty("version") val version: Version,
        @get:JsonProperty("description") val description: String?,
        @get:JsonProperty("platform_locked") val platformLocked: Boolean?,
        @get:JsonProperty("min_engine_version") val minimumEngineVersion: Version?,
        @get:JsonProperty("max_engine_version") val maximumEngineVersion: Version?,
        @get:JsonProperty("pack_scope") val scope: String?,
        @get:JsonProperty("directory_load") val directoryLoad: Boolean?,
        @get:JsonProperty("load_before_game") val loadBeforeGame: Boolean?,
        @get:JsonProperty("lock_template_options") val lockTemplateOptions: Boolean?,
        @get:JsonProperty("population_control") val populationControl: Boolean?
    )

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    class Module(
        @get:JsonProperty("uuid") val id: UUID,
        @get:JsonProperty("version") val version: Version,
        @get:JsonProperty("description") val description: String?,
        @get:JsonProperty("type") val type: Type?,
    ) {
        enum class Type {
            @JsonProperty("invalid") Invalid,
            @JsonProperty("resources") Resource,
            @JsonProperty("data") Data,
            @JsonProperty("plugin") Plugin,
            @JsonProperty("client_data") ClientData,
            @JsonProperty("interface") Interface,
            @JsonProperty("mandatory") Mandatory,
            @JsonProperty("world_template") WorldTemplate
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    class Metadata(
        val authors: List<String>?,
        @get:JsonProperty("license") val license: String?,
        @get:JsonProperty("url") val website: String?
    )

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    class Dependency(
        @get:JsonProperty("uuid") val id: UUID,
        @get:JsonProperty("version") val version: Version
    )

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    class SubPack(
        @get:JsonProperty("folder_name") val path: String? = null,
        @get:JsonProperty("name") val name: String? = null,
        @get:JsonProperty("memory_tier") val memoryTier: Int = 0
    )
}
