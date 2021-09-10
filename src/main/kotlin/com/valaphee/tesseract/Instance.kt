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

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.smile.SmileFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.google.inject.AbstractModule
import com.google.inject.Injector
import com.google.inject.Module
import com.google.inject.TypeLiteral
import com.google.inject.name.Names
import com.valaphee.foundry.ecs.entity.Entity
import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.data.ComponentRegistry
import com.valaphee.tesseract.data.Data
import com.valaphee.tesseract.data.entity.EntityFactory
import com.valaphee.tesseract.data.entity.EntityTypeData
import com.valaphee.tesseract.util.ecs.EntityDeserializer
import com.valaphee.tesseract.util.ecs.EntitySerializer
import com.valaphee.tesseract.util.jackson.Float2Deserializer
import com.valaphee.tesseract.util.jackson.Float2Serializer
import com.valaphee.tesseract.util.jackson.Float3Deserializer
import com.valaphee.tesseract.util.jackson.Float3Serializer
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.WorldEngine
import com.valaphee.tesseract.world.chunk.terrain.generator.Generator
import com.valaphee.tesseract.world.chunk.terrain.generator.normal.NormalGenerator
import com.valaphee.tesseract.world.provider.Provider
import com.valaphee.tesseract.world.provider.TesseractProvider
import io.github.classgraph.ClassGraph
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
    injector: Injector
) {
    private val entityDeserializer = EntityDeserializer()

    @Suppress("LeakingThis")
    val injector: Injector = injector.createChildInjector(object : AbstractModule() {
        override fun configure() {
            bind(ObjectMapper::class.java).annotatedWith(Names.named("world")).toInstance(ObjectMapper(SmileFactory()).apply {
                registerKotlinModule()
                registerModule(
                    SimpleModule()
                        .addSerializer(Float2::class.java, Float2Serializer)
                        .addDeserializer(Float2::class.java, Float2Deserializer)
                        .addSerializer(Float3::class.java, Float3Serializer)
                        .addDeserializer(Float3::class.java, Float3Deserializer)
                        .addSerializer(Entity::class.java, EntitySerializer)
                        .addDeserializer(Entity::class.java, entityDeserializer)
                )
                propertyNamingStrategy = PropertyNamingStrategies.LOWER_CASE
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            })
            bind(this@Instance.javaClass).toInstance(this@Instance)
            bind(Provider::class.java).to(TesseractProvider::class.java)
            bind(Generator::class.java).toInstance(/*FlatGenerator("minecraft:bedrock,3*minecraft:stone,52*minecraft:sandstone;minecraft:desert")*/NormalGenerator(0))

            ComponentRegistry.scan()
            ClassGraph().acceptPaths("data").scan().use {
                val objectMapper = jacksonObjectMapper()
                objectMapper.enable(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS)
                val data = it.allResources.map { resource -> objectMapper.readValue<Data>(resource.url) }
                bind(object : TypeLiteral<Map<String, EntityTypeData>>() {}).toInstance(data.filterIsInstance<EntityTypeData>().associateBy { it.key })
            }
        }
    }, getModule())

    protected val config: Config.Instance = injector.getInstance(Config.Instance::class.java)

    private val executor = Executors.newFixedThreadPool(config.concurrency, ThreadFactoryBuilder().setNameFormat("world-%d").setDaemon(false).build())
    internal val coroutineScope = CoroutineScope(executor.asCoroutineDispatcher() + SupervisorJob() + CoroutineExceptionHandler { context, throwable -> log.error("Unhandled exception caught in $context", throwable) })
    private val worldEngine = WorldEngine(config, 20.0f, coroutineScope.coroutineContext)

    @Suppress("LeakingThis")
    internal val worldContext = WorldContext(this.injector, coroutineScope, worldEngine, this.injector.getInstance(EntityFactory::class.java).also { entityDeserializer.entityFactory = it as EntityFactory<WorldContext> } as EntityFactory<WorldContext>, this.injector.getInstance(Provider::class.java))

    abstract fun getModule(): Module

    open fun run() {
        if (!worldEngine.running) worldEngine.run(worldContext)
    }

    open fun destroy() {
        defaultSystemOut.println("Stopping engine...")

        worldEngine.running = false

        /*worldContext.provider.saveWorld(worldContext.world) TODO Chunk and Players get saved because destroy is called in PacketHandler, but saving the world leads to a non-loadable world*/
        worldContext.provider.destroy()

        executor.shutdown()

        defaultSystemOut.println("Goodbye and see you soon!")
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
