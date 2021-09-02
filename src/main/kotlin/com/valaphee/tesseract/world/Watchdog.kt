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

package com.valaphee.tesseract.world

import com.valaphee.tesseract.defaultSystemErr
import com.valaphee.tesseract.terminal
import com.valaphee.tesseract.util.center
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.lang.management.ManagementFactory

/**
 * @author Kevin Ludwig
 */
class Watchdog : Thread("watchdog") {
    private var last = 0L

    var paused = false

    override fun start() {
        last = System.currentTimeMillis()

        super.start()
    }

    override fun run() {
        try {
            while (true) {
                val delta = System.currentTimeMillis() - last
                if (last != 0L && delta > timeout && !paused) {
                    defaultSystemErr.println("Engine has stopped working")
                    defaultSystemErr.println()
                    val threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true)
                    threads.forEach {
                        defaultSystemErr.println("]${it.threadName}[".center(terminal.width, '-'))
                        defaultSystemErr.println("Suspended: ${it.isSuspended}")
                        defaultSystemErr.println("Native: ${it.isInNative}")
                        defaultSystemErr.println("State: ${it.threadState}")
                        if (it.lockedMonitors.isNotEmpty()) {
                            defaultSystemErr.println("Locked Monitors:")
                            it.lockedMonitors.forEach { defaultSystemErr.println("\t" + it.lockedStackFrame.toString()) }
                        }
                        if (it.stackTrace.isNotEmpty()) {
                            defaultSystemErr.println("Stack Trace:")
                            it.stackTrace.forEach { defaultSystemErr.println("\t" + it.toString()) }
                        }
                    }
                    Runtime.getRuntime().halt(1)
                }
                sleep(timeout)
            }
        } catch (ignore: InterruptedException) {
        }
    }

    fun update(time: Long) {
        last = time
    }

    companion object {
        private const val timeout = 10000L
        private val log: Logger = LogManager.getLogger(Watchdog::class.java)
    }
}
