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

import com.valaphee.foundry.ecs.Engine
import com.valaphee.tesseract.data.Config
import it.unimi.dsi.fastutil.objects.ObjectOpenHashBigSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * @author Kevin Ludwig
 */
class WorldEngine(
    config: Config,
    cyclesPerSecond: Float = 50.0f,
    override val coroutineContext: CoroutineContext
) : Engine<WorldContext>, CoroutineScope {
    var running = false
    var stopped = false
        private set

    private val clock = Clock()
    private val sleep = (1_000L / cyclesPerSecond).toLong()
    private var lastSleep = sleep

    private val entities = ObjectOpenHashBigSet<AnyEntityOfWorld>()

    override fun addEntity(entity: AnyEntityOfWorld) {
        entities += entity
    }

    override fun removeEntity(entity: AnyEntityOfWorld) {
        entities -= entity
    }

    override fun run(context: WorldContext) {
        launch {
            if (!running) {
                running = true

                while (running) {
                    lastSleep = sleep - (clock.realDelta - lastSleep)
                    @Suppress("BlockingMethodInNonBlockingContext")
                    if (lastSleep > 0L) try {
                        delay(lastSleep)
                    } catch (_: InterruptedException) {
                    }
                    val cycles = clock.run()
                    while (cycles.hasNext()) {
                        context.cycle++
                        context.cycleDelta = cycles.next()
                        entities.filter { it.needsUpdate }.map { launch { runCatching { it.update(context) }.onFailure { it.printStackTrace() } } }.joinAll()
                    }
                }
            }
            if (!stopped) stopped = true
        }
    }
}
