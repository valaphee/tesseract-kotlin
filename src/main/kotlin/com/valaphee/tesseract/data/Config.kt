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

package com.valaphee.tesseract.data

import java.net.InetSocketAddress
import java.util.regex.Pattern

/**
 * @author Kevin Ludwig
 */
@Component("tesseract:config")
class Config(
    var concurrency: Int = Runtime.getRuntime().availableProcessors(),
    var watchdog: Watchdog = Watchdog(),
    var listener: Listener = Listener(),
    var maximumPlayers: Int = 10,
    var maximumViewDistance: Int = 32
) : Data {
    class Watchdog(
        var enabled: Boolean = true,
        var timeout: Long = 30_000L
    )

    class Listener(
        var address: InetSocketAddress = InetSocketAddress("0.0.0.0", 19132),
        var maximumQueuedBytes: Int = 8 * 1024 * 1024,
        var serverName: String = "Tesseract",
        var timeout: Int = 30_000,
        var compressionLevel: Int = 7,
        var verification: Boolean = true,
        var userNamePattern: Pattern = Pattern.compile("^[a-zA-Z0-9_-]{3,16}\$"),
        var encryption: Boolean = true,
        var caching: Boolean = false
    )
}
