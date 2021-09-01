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

package com.valaphee.tesseract.world.chunk.terrain.generator.hijack

import com.valaphee.foundry.math.Int2
import com.valaphee.tesseract.actor.player.Appearance
import com.valaphee.tesseract.actor.player.AppearanceImage
import com.valaphee.tesseract.actor.player.AuthExtra
import com.valaphee.tesseract.actor.player.User
import com.valaphee.tesseract.actor.player.readAppearanceImage
import com.valaphee.tesseract.actor.player.view.ChunkPacket
import com.valaphee.tesseract.net.Connection
import com.valaphee.tesseract.net.EncryptionInitializer
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.base.CacheStatusPacket
import com.valaphee.tesseract.net.base.LocalPlayerAsInitializedPacket
import com.valaphee.tesseract.net.init.ClientToServerHandshakePacket
import com.valaphee.tesseract.net.init.InitPacketHandler
import com.valaphee.tesseract.net.init.LoginPacket
import com.valaphee.tesseract.net.init.PacksPacket
import com.valaphee.tesseract.net.init.PacksResponsePacket
import com.valaphee.tesseract.net.init.PacksStackPacket
import com.valaphee.tesseract.net.init.ServerToClientHandshakePacket
import com.valaphee.tesseract.util.generateKeyPair
import com.valaphee.tesseract.world.WorldPacket
import com.valaphee.tesseract.world.chunk.terrain.BlockStorage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.net.InetSocketAddress
import java.util.Locale
import java.util.UUID
import java.util.concurrent.CompletableFuture

/**
 * @author Kevin Ludwig
 */
class HijackPacketHandler(
    private val connection: Connection
) : PacketHandler {
    private val keyPair = generateKeyPair()
    lateinit var chunkPosition: Int2
    lateinit var chunkBlockStorageFuture: CompletableFuture<BlockStorage>

    override fun initialize() {
        connection.write(
            LoginPacket(
                448,
                keyPair.public,
                keyPair.private,
                AuthExtra(
                    UUID.randomUUID(),
                    "0",
                    "Tesseract"
                ),
                User(
                    UUID.randomUUID(),
                    0L,
                    "Tesseract",
                    false,
                    Appearance(null,
                        "Custom",
                        mapOf("geometry" to ("default" to "geometry.humanoid.customSlim")),
                        HijackPacketHandler::class.java.getResourceAsStream("/alex.png").readAppearanceImage(),
                        "",
                        "",
                        "",
                        AppearanceImage.Empty,
                        false,
                        emptyList(),
                        false,
                        false,
                        "",
                        "#0",
                        emptyList(),
                        emptyList(),
                        false,
                        ""
                    ),
                    "",
                    "",
                    UUID.randomUUID().toString(),
                    "",
                    User.OperatingSystem.Unknown,
                    "1.17.11",
                    Locale.ENGLISH,
                    User.InputMode.KeyboardAndMouse,
                    User.InputMode.KeyboardAndMouse,
                    0,
                    User.UiProfile.Classic,
                    InetSocketAddress("127.0.0.1", 19132)
                ),
                false
            )
        )
    }

    override fun other(packet: Packet) {
        log.debug("{}: Unhandled packet: {}", this, packet)
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
        connection.write(LocalPlayerAsInitializedPacket(packet.runtimeEntityId))
    }

    override fun chunk(packet: ChunkPacket) {
        if (::chunkPosition.isInitialized && packet.position == chunkPosition) {
            chunkBlockStorageFuture.complete(packet.blockStorage)
        }
    }

    companion object {
        private val log: Logger = LogManager.getLogger(InitPacketHandler::class.java)
    }
}
