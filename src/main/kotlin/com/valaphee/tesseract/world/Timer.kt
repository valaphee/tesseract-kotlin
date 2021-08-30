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

import org.apache.logging.log4j.LogManager
import kotlin.math.ceil
import kotlin.math.sign

/**
 * @author Kevin Ludwig
 */
class Timer {
    private var last = 0L
    val realTime get() = System.currentTimeMillis()
    var realDelta = 0L
        private set
    var simulationTimeDilation = 1.0f
    var simulationTime = 0L
        private set
    var simulationDelta = 0L
        private set
    private var desync = 0L
    var paused = false

    init {
        reset()
    }

    fun run(): Iterator<Float> {
        var now = realTime
        var delta = now - last
        if (delta == 0L) {
            now = realTime
            delta = now - last
        }
        last = now
        realDelta = delta

        delta = (delta * simulationTimeDilation).toLong()
        if (delta >= cycleCap) {
            log.warn("Took {}ms, capping to {}ms", delta, cycleCap)
            delta = cycleCap
        }
        val cycles = ((delta - 1) / maximumUpdateCycleLength).toInt() + 1

        if (desync != 0L) {
            var difference = ceil(desync * resync).toLong()
            if (difference == 0L) difference = sign(desync.toFloat()).toLong()
            simulationTime += difference
            desync -= difference
        }

        return if (paused) {
            simulationDelta = 0
            TimeStepper(1, 0)
        } else {
            if (cycles > 0) simulationDelta = delta / cycles
            TimeStepper(cycles, delta / cycles)
        }
    }

    fun sync(time: Long) {
        desync = time - realTime
    }

    fun reset() {
        last = realTime
    }

    private inner class TimeStepper(
        private val cycles: Int,
        private val deltaPerCycle: Long
    ) : Iterator<Float> {
        private var currentCycle = 0

        override fun hasNext() = currentCycle < cycles

        override fun next(): Float {
            currentCycle++
            simulationTime += deltaPerCycle
            return deltaPerCycle / 1000.0f
        }
    }

    companion object {
        private val log = LogManager.getLogger(Timer::class.java)
        private const val resync = 0.1f
        private const val maximumUpdateCycleLength = 1000L
        private const val cycleCap = 1000L
    }
}
