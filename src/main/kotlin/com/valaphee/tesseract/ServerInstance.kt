/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.google.inject.AbstractModule
import com.google.inject.Inject
import com.google.inject.Injector
import com.valaphee.tesseract.actor.location.LocationManager
import com.valaphee.tesseract.actor.player.PlayerLocationPacketizer
import com.valaphee.tesseract.actor.player.PlayerType
import com.valaphee.tesseract.actor.player.View
import com.valaphee.tesseract.net.Compressor
import com.valaphee.tesseract.net.Connection
import com.valaphee.tesseract.net.Decompressor
import com.valaphee.tesseract.net.PacketDecoder
import com.valaphee.tesseract.net.PacketEncoder
import com.valaphee.tesseract.net.UnconnectedPingHandler
import com.valaphee.tesseract.net.init.InitPacketHandler
import com.valaphee.tesseract.util.generateKeyPair
import com.valaphee.tesseract.world.WorldType
import com.valaphee.tesseract.world.chunk.ChunkManager
import com.valaphee.tesseract.world.chunk.ChunkPacketizer
import com.valaphee.tesseract.world.chunk.ChunkType
import com.valaphee.tesseract.world.chunk.terrain.CartesianDeltaMergePacketizer
import com.valaphee.tesseract.world.chunk.terrain.CartesianDeltaMerger
import com.valaphee.tesseract.world.chunk.terrain.TerrainManager
import com.valaphee.tesseract.world.entity.EntityManager
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.PooledByteBufAllocator
import io.netty.channel.AdaptiveRecvByteBufAllocator
import io.netty.channel.Channel
import io.netty.channel.ChannelException
import io.netty.channel.ChannelFactory
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.WriteBufferWaterMark
import io.netty.channel.epoll.EpollChannelOption
import io.netty.channel.unix.UnixChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.opentelemetry.api.OpenTelemetry
import network.ycc.raknet.RakNet
import network.ycc.raknet.pipeline.UserDataCodec
import network.ycc.raknet.server.channel.RakNetServerChannel
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.security.KeyPair
import java.util.concurrent.TimeUnit

class ServerInstance(
    injector: Injector,
    telemetry: OpenTelemetry,
) : Instance(injector, telemetry) {
    @Inject
    lateinit var config: Config

    private val parentGroup = underlyingNetworking.groupFactory(0, ThreadFactoryBuilder().setNameFormat("server-%d").build())
    private val childGroup = underlyingNetworking.groupFactory(0, ThreadFactoryBuilder().setNameFormat("server-c-%d").build())

    lateinit var channel: Channel

    init {
        this.injector.injectMembers(this)
    }

    override fun getModule() = object : AbstractModule() {
        override fun configure() {
            bind(KeyPair::class.java).toInstance(generateKeyPair())
        }
    }

    override fun createEntityFactory() = super.createEntityFactory().apply {
        register(WorldType) {
            facets(
                EntityManager::class.java,
                ChunkManager::class.java,

                // integration
                ChunkPacketizer::class.java,
            )
        }
        register(ChunkType) {
            behaviors(
                CartesianDeltaMerger::class.java
            )
            facets(
                TerrainManager::class.java,

                // integration
                CartesianDeltaMergePacketizer::class.java
            )
        }
        register(PlayerType) {
            facets(
                LocationManager::class.java,
                View::class.java,

                // integration
                PlayerLocationPacketizer::class.java
            )
        }
    }

    fun bind() {
        val userDataCodec = UserDataCodec(0xFE)
        val serverBootstrap = ServerBootstrap()
            .group(parentGroup, childGroup)
            .channelFactory(ChannelFactory { RakNetServerChannel(underlyingNetworking.datagramChannel) })
            .option(RakNet.MAX_CONNECTIONS, config.maximumPlayers)
            .handler(object : ChannelInitializer<Channel>() {
                override fun initChannel(channel: Channel) {
                    channel.config().writeBufferWaterMark = writeBufferWaterMark

                    channel.pipeline().addLast(UnconnectedPingHandler())
                }
            })
            .childHandler(object : ChannelInitializer<Channel>() {
                override fun initChannel(channel: Channel) {
                    val config = channel.config()
                    try {
                        config.setOption(ChannelOption.IP_TOS, 0x18)
                    } catch (_: ChannelException) {
                    }
                    config.setAllocator(PooledByteBufAllocator.DEFAULT).writeBufferWaterMark = childWriteBufferWaterMark
                    (config as RakNet.Config).maxQueuedBytes = 8 * 1024 * 1024

                    val connection = Connection()
                    connection.setHandler(InitPacketHandler(worldContext, connection).apply { injector.injectMembers(this) })
                    channel.pipeline()
                        .addFirst("ta-timeout", ReadTimeoutHandler(this@ServerInstance.config.timeout))
                        .addLast(UserDataCodec.NAME, userDataCodec)
                        .addLast(Compressor.NAME, Compressor())
                        .addLast(Decompressor.NAME, Decompressor())
                        .addLast(PacketEncoder.NAME, PacketEncoder(true))
                        .addLast(PacketDecoder.NAME, PacketDecoder(readers))
                        .addLast(connection)
                }
            })
        val channelFutureListener = ChannelFutureListener {
            if (it.isSuccess) {
                channel = it.channel()

                log.info("Listening on {}", channel.localAddress())

                run()
            } else log.warn("Failed to bind to {}", it.channel().localAddress())
        }
        if (underlyingNetworking == UnderlyingNetworking.Epoll) {
            serverBootstrap
                .option(UnixChannelOption.SO_REUSEPORT, true)
                .option(EpollChannelOption.MAX_DATAGRAM_PAYLOAD_SIZE, 4 * 1024)
            repeat(Runtime.getRuntime().availableProcessors()) {
                serverBootstrap.clone()
                    .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator(4 * 1024, 64 * 1024, 256 * 1024))
                    .bind(config.address)
                    .addListener(channelFutureListener)
            }
        } else serverBootstrap.bind(config.address).addListener(channelFutureListener)
    }

    fun close() {
        channel.close().syncUninterruptibly()
    }

    override fun destroy() {
        parentGroup.shutdownGracefully()
        parentGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)

        childGroup.shutdownGracefully()
        childGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)

        super.destroy()
    }

    companion object {
        private val log: Logger = LogManager.getLogger(ServerInstance::class.java)
        private val writeBufferWaterMark = WriteBufferWaterMark(5 * 1024 * 1024, 10 * 1024 * 1024)
        private val childWriteBufferWaterMark = WriteBufferWaterMark(512 * 1024, 2 * 1024 * 1024)
    }
}
