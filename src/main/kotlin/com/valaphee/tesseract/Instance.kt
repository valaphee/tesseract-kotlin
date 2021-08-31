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
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.smile.SmileFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.google.inject.AbstractModule
import com.google.inject.Injector
import com.google.inject.Module
import com.google.inject.name.Names
import com.valaphee.foundry.ecs.entity.Entity
import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.actor.player.InputPacketReader
import com.valaphee.tesseract.actor.player.InteractPacketReader
import com.valaphee.tesseract.actor.player.PlayerActionPacketReader
import com.valaphee.tesseract.actor.player.PlayerLocationPacketReader
import com.valaphee.tesseract.actor.player.view.ViewDistanceRequestPacketReader
import com.valaphee.tesseract.command.net.CommandPacketReader
import com.valaphee.tesseract.inventory.InventoryRequestPacketReader
import com.valaphee.tesseract.inventory.WindowClosePacketReader
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.base.CacheBlobStatusPacketReader
import com.valaphee.tesseract.net.base.CacheStatusPacketReader
import com.valaphee.tesseract.net.base.TextPacketReader
import com.valaphee.tesseract.net.init.ClientToServerHandshakePacketReader
import com.valaphee.tesseract.net.init.LoginPacketReader
import com.valaphee.tesseract.net.init.PacksResponsePacketReader
import com.valaphee.tesseract.util.ecs.EntityDeserializer
import com.valaphee.tesseract.util.ecs.EntityFactory
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
            })
            bind(this@Instance.javaClass).toInstance(this@Instance)
            bind(Provider::class.java).to(TesseractProvider::class.java)
            bind(Generator::class.java).toInstance(NormalGenerator(1))
        }
    }, getModule())

    private val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), ThreadFactoryBuilder().setNameFormat("world-%d").build())
    private val coroutineScope = CoroutineScope(executor.asCoroutineDispatcher() + SupervisorJob() + CoroutineExceptionHandler { context, throwable -> log.error("Unhandled exception caught in $context", throwable) })
    private val worldEngine = WorldEngine(20.0f, coroutineScope.coroutineContext)

    @Suppress("LeakingThis")
    protected val worldContext = WorldContext(this.injector, coroutineScope, worldEngine, createEntityFactory().also { entityDeserializer.entityFactory = it }, this.injector.getInstance(Provider::class.java))

    protected val packetReaders = Int2ObjectOpenHashMap<PacketReader>().apply {
        this[0x01] = LoginPacketReader
        /*this[0x02] = StatusPacketReader*/

        this[0x04] = ClientToServerHandshakePacketReader
        /*this[0x05] = DisconnectPacketReader
        this[0x06] = PacksPacketReader
        this[0x07] = PacksStackPacketReader*/
        this[0x08] = PacksResponsePacketReader
        this[0x09] = TextPacketReader

        this[0x13] = PlayerLocationPacketReader(worldContext)

        this[0x21] = InteractPacketReader(worldContext)

        this[0x24] = PlayerActionPacketReader(worldContext)

        this[0x2F] = WindowClosePacketReader

        this[0x45] = ViewDistanceRequestPacketReader
        /*this[0x46] = ViewDistancePacketReader*/

        this[0x4D] = CommandPacketReader

        /*this[0x4F] = CommandResponsePacketReader*/

        this[0x81] = CacheStatusPacketReader

        this[0x87] = CacheBlobStatusPacketReader
        /*this[0x88] = ChunkCacheBlobsPacketReader*/

        this[0x90] = InputPacketReader

        this[0x93] = InventoryRequestPacketReader
    }

    abstract fun getModule(): Module

    open fun createEntityFactory() = EntityFactory<WorldContext>(this.injector)

    open fun run() {
        if (!worldEngine.running) worldEngine.run(worldContext)
    }

    open fun destroy() {
        worldEngine.running = false

        executor.shutdown()

        worldContext.provider.saveWorld(worldContext.world)
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
