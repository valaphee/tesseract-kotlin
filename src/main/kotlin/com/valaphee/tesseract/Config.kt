/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
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
