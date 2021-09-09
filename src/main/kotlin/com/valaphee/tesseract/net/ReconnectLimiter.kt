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

package com.valaphee.tesseract.net

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.InetAddress
import kotlin.coroutines.CoroutineContext

/**
 * @author Kevin Ludwig
 */
class ReconnectLimiter(
    private val limit: Int,
    override val coroutineContext: CoroutineContext
) : CoroutineScope {
    private val trackers = mutableMapOf<InetAddress, Tracker>()

    fun available(address: InetAddress) = trackers.computeIfAbsent(address, ::Tracker).available(System.currentTimeMillis())

    private inner class Tracker(
        private val address: InetAddress
    ) {
        private val history = LongArray(limit)
        private var index = 0

        init {
            launch {
                delay(120_000L)
                update()
            }
        }

        fun available(now: Long): Boolean {
            val last = history[index]
            history[index] = now
            index = (index + 1) % history.size

            return (last == 0L) || ((now - last) > 60_000L)
        }

        fun update() {
            val last = history[(if (index == 0) history.size else index) - 1]
            if ((last != 0L) && ((System.currentTimeMillis() - last) < 1_000L)) {
                launch {
                    delay(120_000L)
                    update()
                }
            } else trackers.remove(address)
        }
    }
}
