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

package com.valaphee.tesseract.dev

import com.google.inject.Inject
import com.valaphee.tesseract.data.block.Block
import com.valaphee.tesseract.entity.EventPacket
import com.valaphee.tesseract.net.Connection
import com.valaphee.tesseract.net.EncryptionInitializer
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.base.ClientToServerHandshakePacket
import com.valaphee.tesseract.net.base.LoginPacket
import com.valaphee.tesseract.net.base.ServerToClientHandshakePacket
import com.valaphee.tesseract.util.Registry
import com.valaphee.tesseract.util.generateKeyPair
import com.valaphee.tesseract.world.WorldPacket
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * @author Kevin Ludwig
 */
class SniffClientPacketHandler(
    private val clientConnection: Connection,
    private val serverConnection: Connection,
    private val loginPacket: LoginPacket,
) : PacketHandler {
    @Inject private lateinit var blocks: Map<String, @JvmSuppressWildcards Block>
    private val keyPair = generateKeyPair()

    override fun initialize() {
        serverConnection.write(LoginPacket(serverConnection.version, keyPair.public, keyPair.private, loginPacket.authExtra, loginPacket.user))
    }

    override fun other(packet: Packet) {
        if (!ignoringPackets.contains(packet.id)) packetLog.info("{}", packet.toString())

        when (packet) {
            is WorldPacket -> {
                val blockStates = Registry<String>()
                var runtimeId = 0
                blocks.values.sortedWith(compareBy { it.key.split(":", limit = 2)[1].lowercase() }).forEach {
                    if (it.states.size != 1) blockStates.valueToId[it.key] = runtimeId
                    it.states.forEach { blockStates[runtimeId++] = it.toString() }
                }
                clientConnection.blockStates = blockStates
                serverConnection.blockStates = blockStates
                val items = Registry<String>()
                packet.items!!.forEach { items[it.key] = it.value.key }
                clientConnection.items = items
                serverConnection.items = items
            }
            is EventPacket -> return
        }

        clientConnection.write(packet)
    }

    override fun serverToClientHandshake(packet: ServerToClientHandshakePacket) {
        if (!ignoringPackets.contains(packet.id)) packetLog.info("{}", packet.toString())

        serverConnection.context.pipeline().addLast(EncryptionInitializer(keyPair, packet.serverPublicKey, true, packet.salt))
        serverConnection.write(ClientToServerHandshakePacket)
    }

    companion object {
        private val packetLog: Logger = LogManager.getLogger("Packet (ToClient)")
        private val ignoringPackets = setOf(
            0x28, // EntityVelocityPacket
            0x34, // RecipesPacket
            0x3A, // ChunkPacket
            0x4C, // CommandsPacket
            0x6F, // EntityMoveRotatePacket
            0x87, // CacheBlobsPacket
        )
    }
}
