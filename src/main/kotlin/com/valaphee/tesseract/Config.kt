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

package com.valaphee.tesseract

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Inject
import com.google.inject.ProvidedBy
import com.google.inject.Singleton
import com.google.inject.name.Named
import java.net.InetSocketAddress
import java.util.regex.Pattern

/**
 * @author Kevin Ludwig
 */
@Singleton
@ProvidedBy(Config.Provider::class)
data class Config(
    @get:JsonProperty("address") var address: InetSocketAddress = InetSocketAddress("0.0.0.0", 19132),
    @get:JsonProperty("maximum-players") var maximumPlayers: Int = 10,
    @get:JsonProperty("server-name") var serverName: String = "Tesseract",
    @get:JsonProperty("timeout") var timeout: Int = 30_000,
    @get:JsonProperty("compression-level") var compressionLevel: Int = 7,
    @get:JsonProperty("verification") var verification: Boolean = true,
    @get:JsonProperty("user-name-pattern") var userNamePattern: Pattern = Pattern.compile("^[a-zA-Z0-9_-]{3,16}\$"),
    @get:JsonProperty("encryption") var encryption: Boolean = true,
    @get:JsonProperty("caching") var caching: Boolean = false,
    @get:JsonProperty("maximum-view-distance") var maximumViewDistance: Int = 32,
) {
    class Provider @Inject constructor(
        private val argument: Argument,
        @Named("config") private val objectMapper: ObjectMapper
    ) : com.google.inject.Provider<Config> {
        override fun get() = if (argument.config.exists()) objectMapper.readValue(argument.config) else Config().also { objectMapper.writeValue(argument.config, it) }
    }
}
