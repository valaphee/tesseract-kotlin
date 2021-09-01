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

package com.valaphee.tesseract.world.chunk.terrain.generator

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.valaphee.foundry.math.Int2
import com.valaphee.tesseract.Instance
import com.valaphee.tesseract.actor.player.Appearance
import com.valaphee.tesseract.actor.player.AppearanceImage
import com.valaphee.tesseract.actor.player.AuthExtra
import com.valaphee.tesseract.actor.player.User
import com.valaphee.tesseract.actor.player.readAppearanceImage
import com.valaphee.tesseract.actor.player.view.ChunkPacket
import com.valaphee.tesseract.actor.player.view.ViewDistanceRequestPacket
import com.valaphee.tesseract.command.net.CommandPacket
import com.valaphee.tesseract.command.net.Origin
import com.valaphee.tesseract.net.Compressor
import com.valaphee.tesseract.net.Connection
import com.valaphee.tesseract.net.Decompressor
import com.valaphee.tesseract.net.EncryptionInitializer
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketDecoder
import com.valaphee.tesseract.net.PacketEncoder
import com.valaphee.tesseract.net.base.CacheStatusPacket
import com.valaphee.tesseract.net.base.LocalPlayerAsInitializedPacket
import com.valaphee.tesseract.net.init.ClientToServerHandshakePacket
import com.valaphee.tesseract.net.init.LoginPacket
import com.valaphee.tesseract.net.init.PacksPacket
import com.valaphee.tesseract.net.init.PacksResponsePacket
import com.valaphee.tesseract.net.init.PacksStackPacket
import com.valaphee.tesseract.net.init.ServerToClientHandshakePacket
import com.valaphee.tesseract.net.init.StatusPacket
import com.valaphee.tesseract.util.generateKeyPair
import com.valaphee.tesseract.world.WorldPacket
import com.valaphee.tesseract.world.chunk.encodePosition
import com.valaphee.tesseract.world.chunk.terrain.BlockStorage
import com.valaphee.tesseract.world.chunk.terrain.Terrain
import io.netty.bootstrap.Bootstrap
import io.netty.channel.AdaptiveRecvByteBufAllocator
import io.netty.channel.ChannelFactory
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.epoll.EpollChannelOption
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import network.ycc.raknet.RakNet
import network.ycc.raknet.client.channel.RakNetClientChannel
import network.ycc.raknet.pipeline.UserDataCodec
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.net.InetSocketAddress
import java.util.Locale
import java.util.UUID
import io.netty.channel.Channel as NettyChannel

/**
 * @author Kevin Ludwig
 */
class HijackGenerator : Generator {
    private var group = Instance.underlyingNetworking.groupFactory(0, ThreadFactoryBuilder().setNameFormat("hijack-%d").build())

    lateinit var connection: Connection
    lateinit var handler: PacketHandler
    var runtimeEntityId = 0L

    private val cachedChunks = Long2ObjectMaps.synchronize(Long2ObjectOpenHashMap<BlockStorage>())
    private val awaitingChunks = Long2ObjectMaps.synchronize(Long2ObjectOpenHashMap<CompletableDeferred<BlockStorage>>())

    init {
        val userDataCodec = UserDataCodec(0xFE)
        val bootstrap = Bootstrap()
            .group(group)
            .channelFactory(ChannelFactory { RakNetClientChannel(Instance.underlyingNetworking.datagramChannel) })
            .option(RakNet.MTU, 1_464)
            .option(RakNet.PROTOCOL_VERSION, 10)
            .handler(object : ChannelInitializer<NettyChannel>() {
                override fun initChannel(channel: NettyChannel) {
                    connection = Connection()
                    handler = PacketHandler()
                    connection.setHandler(handler)
                    channel.pipeline()
                        .addLast(UserDataCodec.NAME, userDataCodec)
                        .addLast(Compressor.NAME, Compressor())
                        .addLast(Decompressor.NAME, Decompressor())
                        .addLast(PacketEncoder.NAME, PacketEncoder(false))
                        .addLast(PacketDecoder.NAME, PacketDecoder())
                        .addLast(connection)
                }
            })
        if (Instance.underlyingNetworking == Instance.UnderlyingNetworking.Epoll) bootstrap
            .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator(4 * 1024, 8 * 1024, 16 * 1024))
            .option(EpollChannelOption.MAX_DATAGRAM_PAYLOAD_SIZE, 4 * 1024)
        bootstrap.connect(InetSocketAddress("172.16.1.116", 19132)).addListener(ChannelFutureListener {
            if (it.isSuccess) log.info("Hijacking {}", it.channel().remoteAddress())
            else log.warn("Failed to hijack {}", it.channel().remoteAddress(), it.cause())
        })
    }

    override fun generate(position: Int2): Terrain {
        val (x, z) = position
        return Terrain(cachedChunks[encodePosition(x, z)] ?: run {
            connection.write(CommandPacket("tp ${-x * 16 + 7} 255 ${-z * 16 + 7}", Origin(Origin.Where.Player, UUID.randomUUID(), "", 0L), false))
            connection.write(CommandPacket("tp ${x * 16 + 7} 255 ${z * 16 + 7}", Origin(Origin.Where.Player, UUID.randomUUID(), "", 0L), false))
            val awaitingChunk = CompletableDeferred<BlockStorage>()
            awaitingChunks[encodePosition(x, z)] = awaitingChunk
            runBlocking { awaitingChunk.await() }
        }, true)
    }

    inner class PacketHandler : com.valaphee.tesseract.net.PacketHandler {
        private val keyPair = generateKeyPair()

        override fun initialize() {
            connection.protocolVersion = 448
            connection.write(LoginPacket(connection.protocolVersion, keyPair.public, keyPair.private, AuthExtra(UUID.randomUUID(), "0", "Tesseract"), User(UUID.randomUUID(), 0L, "Tesseract", false, Appearance(null, "Custom", mapOf("geometry" to ("default" to "geometry.humanoid.customSlim")), PacketHandler::class.java.getResourceAsStream("/alex.png").readAppearanceImage(), "", "", "", AppearanceImage.Empty, false, emptyList(), false, false, "", "#0", emptyList(), emptyList(), false, ""), "", "", UUID.randomUUID().toString(), "", User.OperatingSystem.Unknown, "1.17.11", Locale.getDefault(), User.InputMode.KeyboardAndMouse, User.InputMode.KeyboardAndMouse, 0, User.UiProfile.Classic, connection.address), false))
        }

        override fun other(packet: Packet) {
            log.debug("{}: Unhandled packet: {}", this, packet)
        }

        override fun status(packet: StatusPacket) {
            if (packet.status == StatusPacket.Status.PlayerSpawn) connection.write(LocalPlayerAsInitializedPacket(runtimeEntityId))
        }

        override fun serverToClientHandshake(packet: ServerToClientHandshakePacket) {
            connection.context.pipeline().addLast(EncryptionInitializer(keyPair, packet.serverPublicKey, true, packet.salt))
            connection.write(ClientToServerHandshakePacket)
        }

        override fun packs(packet: PacksPacket) {
            connection.write(CacheStatusPacket(false))
            connection.write(PacksResponsePacket(PacksResponsePacket.Status.HaveAllPacks, emptyArray()))
        }

        override fun packsStack(packet: PacksStackPacket) {
            connection.write(PacksResponsePacket(PacksResponsePacket.Status.Completed, emptyArray()))
        }

        override fun world(packet: WorldPacket) {
            runtimeEntityId = packet.runtimeEntityId
            connection.write(ViewDistanceRequestPacket(32))
        }

        override fun chunk(packet: ChunkPacket) {
            val (x, z) = packet.position
            val position = encodePosition(x, z)
            cachedChunks[position] = packet.blockStorage
            awaitingChunks.remove(position)?.complete(packet.blockStorage)
        }
    }

    companion object {
        private val log: Logger = LogManager.getLogger(HijackGenerator::class.java)
    }
}
