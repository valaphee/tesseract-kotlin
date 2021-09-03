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
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * @author Kevin Ludwig
 */
class WorldEngine(
    cyclesPerSecond: Float = 50.0f,
    override val coroutineContext: CoroutineContext
) : Engine<WorldContext>, CoroutineScope {
    var running = false
    var stopped = false
        private set

    private val watchdog = Watchdog()

    private val clock = Clock()
    private val sleep = (1_000L / cyclesPerSecond).toLong()
    private var lastSleep = sleep

    private val entityById = Long2ObjectOpenHashMap<AnyEntityOfWorld>()

    override fun addEntity(entity: AnyEntityOfWorld) {
        synchronized(entityById) { entityById.put(entity.id, entity) }
    }

    override fun removeEntity(entity: AnyEntityOfWorld) {
        synchronized(entityById) { entityById.remove(entity.id) }
    }

    override fun findEntityOrNull(id: Long): AnyEntityOfWorld? = synchronized(entityById) { entityById[id] }

    override fun run(context: WorldContext) {
        launch {
            if (!running) {
                running = true
                watchdog.start()

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
                        entityById.values.filter { it.needsUpdate }.map { async { it.update(context) } }.awaitAll()
                    }
                    watchdog.update(clock.realTime)
                }
            }
            if (!stopped) {
                watchdog.interrupt()
                stopped = true
            }
        }
    }
}
