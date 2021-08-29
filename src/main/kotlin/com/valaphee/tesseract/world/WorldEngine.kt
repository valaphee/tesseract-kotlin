/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world

import com.valaphee.foundry.ecs.Engine
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.trace.SpanKind
import io.opentelemetry.api.trace.Tracer
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * @author Kevin Ludwig
 */
class WorldEngine(
    cyclesPerSecond: Float = 50.0f,
    override val coroutineContext: CoroutineContext,
    val tracer: Tracer
) : Engine<WorldContext>, CoroutineScope {
    var running = false
    var stopped = false
        private set

    val timer = Timer()
    private val sleep = (1_000L / cyclesPerSecond).toLong()
    private var lastSleep = sleep

    var cycle = 0L

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

                while (running) {
                    lastSleep = sleep - (timer.realDelta - lastSleep)
                    @Suppress("BlockingMethodInNonBlockingContext")
                    if (lastSleep > 0L) try {
                        Thread.sleep(lastSleep)
                    } catch (_: InterruptedException) {
                    }
                    val cycles = timer.run()
                    while (cycles.hasNext()) {
                        context.cycleDelta = cycles.next()
                        val span = tracer.spanBuilder("cycle ${cycle++}").setSpanKind(SpanKind.INTERNAL).startSpan()
                        entityById.values.filter { it.needsUpdate }.map {
                            async {
                                try {
                                    it.update(context)
                                } catch (thrown: Throwable) {
                                    span.recordException(thrown)
                                }
                            }
                        }.also { span.setAttribute(updates, it.size.toLong()) }.awaitAll()
                        span.end()
                    }
                }
            }
            if (!stopped) stopped = true
        }
    }

    companion object {
        private val updates: AttributeKey<Long> = AttributeKey.longKey("updates")
    }
}
