/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

@file:Suppress("LeakingThis")

package com.valaphee.tesseract

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.smile.SmileFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.google.inject.AbstractModule
import com.google.inject.Injector
import com.google.inject.Module
import com.valaphee.foundry.ecs.entity.Entity
import com.valaphee.foundry.math.Double3
import com.valaphee.foundry.math.Double3Deserializer
import com.valaphee.foundry.math.Double3Serializer
import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float2Deserializer
import com.valaphee.foundry.math.Float2Serializer
import com.valaphee.tesseract.actor.player.PlayerActionPacketReader
import com.valaphee.tesseract.actor.player.PlayerLocationPacketReader
import com.valaphee.tesseract.actor.player.ViewDistanceRequestPacketReader
import com.valaphee.tesseract.command.CommandPacketReader
import com.valaphee.tesseract.ecs.EntityDeserializer
import com.valaphee.tesseract.ecs.EntityFactory
import com.valaphee.tesseract.ecs.EntitySerializer
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.base.CacheBlobStatusPacketReader
import com.valaphee.tesseract.net.base.CacheStatusPacketReader
import com.valaphee.tesseract.net.base.TextPacketReader
import com.valaphee.tesseract.net.init.ClientToServerHandshakePacketReader
import com.valaphee.tesseract.net.init.LoginPacketReader
import com.valaphee.tesseract.net.init.PacksResponsePacketReader
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.WorldEngine
import com.valaphee.tesseract.world.persistence.InMemoryBackend
import io.netty.channel.EventLoopGroup
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollDatagramChannel
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.kqueue.KQueue
import io.netty.channel.kqueue.KQueueDatagramChannel
import io.netty.channel.kqueue.KQueueEventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramChannel
import io.netty.channel.socket.nio.NioDatagramChannel
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.trace.Tracer
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

/**
 * @author Kevin Ludwig
 */
abstract class Instance(
    injector: Injector,
    val telemetry: OpenTelemetry,
) {
    private val entityDeserializer = EntityDeserializer()

    @Suppress("LeakingThis")
    val injector: Injector = injector.createChildInjector(object : AbstractModule() {
        override fun configure() {
            bind(ObjectMapper::class.java).toInstance(ObjectMapper(SmileFactory()).apply {
                registerKotlinModule()
                registerModule(
                    SimpleModule()
                        .addSerializer(Float2::class.java, Float2Serializer)
                        .addDeserializer(Float2::class.java, Float2Deserializer)
                        .addSerializer(Double3::class.java, Double3Serializer)
                        .addDeserializer(Double3::class.java, Double3Deserializer)
                        .addSerializer(Entity::class.java, EntitySerializer)
                        .addDeserializer(Entity::class.java, entityDeserializer)
                )
            })
            bind(this@Instance.javaClass).toInstance(this@Instance)
        }
    }, getModule())

    val tracer: Tracer = telemetry.getTracer("tesseract")

    private val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), ThreadFactoryBuilder().setNameFormat("world-%d").build())
    private val coroutineScope = CoroutineScope(executor.asCoroutineDispatcher() + SupervisorJob() + CoroutineExceptionHandler { context, throwable -> log.error("Unhandled exception caught in $context", throwable) })
    private val worldEngine = WorldEngine(20.0f, coroutineScope.coroutineContext, tracer)

    @Suppress("LeakingThis")
    protected val worldContext = WorldContext(this.injector, coroutineScope, worldEngine, createEntityFactory().also { entityDeserializer.entityFactory = it }, /*AxolotlBackend(this.injector.getInstance(ObjectMapper::class.java), File("world"))*/InMemoryBackend())

    protected val readers = Int2ObjectOpenHashMap<PacketReader>().apply {
        this[0x01] = LoginPacketReader
        /*this[0x02] = StatusPacketReader*/

        this[0x04] = ClientToServerHandshakePacketReader
        /*this[0x05] = DisconnectPacketReader
        this[0x06] = PacksPacketReader
        this[0x07] = PacksStackPacketReader*/
        this[0x08] = PacksResponsePacketReader
        this[0x09] = TextPacketReader

        this[0x13] = PlayerLocationPacketReader(worldContext)

        this[0x24] = PlayerActionPacketReader(worldContext)

        this[0x45] = ViewDistanceRequestPacketReader
        /*this[0x46] = ViewDistancePacketReader*/

        this[0x4D] = CommandPacketReader

        /*this[0x4F] = CommandResponsePacketReader*/

        this[0x81] = CacheStatusPacketReader

        this[0x87] = CacheBlobStatusPacketReader
        /*this[0x88] = ChunkCacheBlobsPacketReader*/
    }

    abstract fun getModule(): Module

    open fun createEntityFactory() = EntityFactory<WorldContext>(this.injector)

    open fun run() {
        if (!worldEngine.running) worldEngine.run(worldContext)
    }

    open fun destroy() {
        worldEngine.running = false

        executor.shutdown()

        worldContext.backend.saveWorld(worldContext.world)
    }

    companion object {
        private val log: Logger = LogManager.getLogger(Instance::class.java)
        val underlyingNetworking = if (Epoll.isAvailable()) UnderlyingNetworking.Epoll else if (KQueue.isAvailable()) UnderlyingNetworking.Kqueue else UnderlyingNetworking.Nio
    }

    enum class UnderlyingNetworking(
        val groupFactory: (Int, ThreadFactory) -> EventLoopGroup,
        val datagramChannel: Class<out DatagramChannel>
    ) {
        Epoll({ threadCount, threadFactory -> EpollEventLoopGroup(threadCount, threadFactory) }, EpollDatagramChannel::class.java),
        Kqueue({ threadCount, threadFactory -> KQueueEventLoopGroup(threadCount, threadFactory) }, KQueueDatagramChannel::class.java),
        Nio({ threadCount, threadFactory -> NioEventLoopGroup(threadCount, threadFactory) }, NioDatagramChannel::class.java)
    }
}
