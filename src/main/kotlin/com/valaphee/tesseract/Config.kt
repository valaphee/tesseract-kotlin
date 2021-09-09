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
    var version: Int = 0,
    var instance: Instance = Instance()
) {
    @ProvidedBy(Instance.Provider::class)
    data class Instance(
        var concurrency: Int = Runtime.getRuntime().availableProcessors(),
        var watchdog: Watchdog = Watchdog(),
        var listener: Listener = Listener(),
        var maximumPlayers: Int = 10,
        var maximumViewDistance: Int = 32
    ) {
        @ProvidedBy(Watchdog.Provider::class)
        data class Watchdog(
            var enabled: Boolean = true,
            var timeout: Long = 30_000L
        ) {
            class Provider @Inject constructor(
                private val config: Instance
            ) : com.google.inject.Provider<Watchdog> {
                override fun get() = config.watchdog
            }
        }

        @ProvidedBy(Listener.Provider::class)
        data class Listener(
            var address: InetSocketAddress = InetSocketAddress("0.0.0.0", 19132),
            var maximumQueuedBytes: Int = 8 * 1024 * 1024,
            var serverName: String = "Tesseract",
            var timeout: Int = 30_000,
            var compressionLevel: Int = 7,
            var verification: Boolean = true,
            var userNamePattern: Pattern = Pattern.compile("^[a-zA-Z0-9_-]{3,16}\$"),
            var encryption: Boolean = true,
            var caching: Boolean = false
        ) {
            class Provider @Inject constructor(
                private val config: Instance
            ) : com.google.inject.Provider<Listener> {
                override fun get() = config.listener
            }
        }

        class Provider @Inject constructor(
            private val config: Config
        ) : com.google.inject.Provider<Instance> {
            override fun get() = config.instance
        }
    }

    class Provider @Inject constructor(
        private val argument: Argument,
        @Named("config") private val objectMapper: ObjectMapper
    ) : com.google.inject.Provider<Config> {
        override fun get() = (if (argument.config.exists()) objectMapper.readValue(argument.config) else Config()).also { objectMapper.writeValue(argument.config, it) }
    }
}
