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

package com.valaphee.tesseract.net

import com.valaphee.tesseract.world.GameMode
import java.util.StringJoiner

/**
 * @author Kevin Ludwig
 */
data class Pong(
    val serverId: Long,
    val serverName: String? = null,
    val version: String? = null,
    val protocolVersion: Int,
    val edition: String = "MCPE",
    val nintendoLimited: Boolean = false,
    val gameMode: GameMode? = null,
    val playerCount: Int,
    val maximumPlayerCount: Int,
    val ipv4Port: Int = 19132,
    val ipv6Port: Int = 19133,
    val description: String? = null
) {
    override fun toString() = StringJoiner(";")
        .add(edition)
        .add(description ?: "")
        .add(protocolVersion.toString())
        .add(version ?: "")
        .add(playerCount.toString())
        .add(maximumPlayerCount.toString())
        .add(serverId.toString())
        .add(serverName ?: "")
        .add(gameMode?.key ?: "")
        .add(if (nintendoLimited) "0" else "1")
        .add(ipv4Port.toString())
        .add(ipv6Port.toString())
        .toString()
}
