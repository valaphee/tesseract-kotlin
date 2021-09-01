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

import com.valaphee.tesseract.actor.player.Appearance
import com.valaphee.tesseract.actor.player.AppearanceImage
import com.valaphee.tesseract.actor.player.AuthExtra
import com.valaphee.tesseract.actor.player.User
import com.valaphee.tesseract.net.Connection
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.init.InitPacketHandler
import com.valaphee.tesseract.net.init.LoginPacket
import com.valaphee.tesseract.util.generateKeyPair
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.net.InetSocketAddress
import java.util.Locale
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
class InitPacketHandler(
    private val connection: Connection
) : PacketHandler {
    private val keyPair = generateKeyPair()

    override fun initialize() {
        connection.write(LoginPacket(448, keyPair.public, AuthExtra(UUID.randomUUID(), "0", "Tesseract"), User(UUID.randomUUID(), 0L, "", false, Appearance("", "", emptyMap(), AppearanceImage.Empty, "", "", "", AppearanceImage.Empty, false, emptyList(), false, false, "", "", emptyList(), emptyList(), false, ""), "", "", "", "", User.OperatingSystem.Unknown, "1.17.11", Locale.ENGLISH, User.InputMode.Unknown, User.InputMode.Unknown, 0, User.UiProfile.Classic, InetSocketAddress("127.0.0.1", 19132)), false))
    }

    override fun other(packet: Packet) {
        log.debug("{}: Unhandled packet: {}", packet)
    }

    companion object {
        private val log: Logger = LogManager.getLogger(InitPacketHandler::class.java)
    }
}
