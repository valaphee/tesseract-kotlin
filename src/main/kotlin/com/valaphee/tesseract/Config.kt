/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.inject.ProvidedBy
import com.google.inject.Singleton
import java.net.InetSocketAddress
import java.util.regex.Pattern

/**
 * @author Kevin Ludwig
 */
@Singleton
@ProvidedBy(Config.Provider::class)
data class Config(
    @get:JsonProperty("address") var address: InetSocketAddress,
    @get:JsonProperty("timeout") var timeout: Int,
    @get:JsonProperty("maximum-players") var maximumPlayers: Int,
    @get:JsonProperty("compression-level") var compressionLevel: Int,
    @get:JsonProperty("verification") var verification: Boolean,
    @get:JsonProperty("user-name-pattern") var userNamePattern: Pattern,
    @get:JsonProperty("encryption") var encryption: Boolean,
) {
    class Provider : com.google.inject.Provider<Config> {
        override fun get() = Config(InetSocketAddress("127.0.0.1", 19132), 5_000, 20, 7, false, Pattern.compile("^[a-zA-Z0-9_-]{3,15}\$"), true)
    }
}
