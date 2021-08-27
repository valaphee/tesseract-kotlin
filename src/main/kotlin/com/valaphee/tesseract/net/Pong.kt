/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net

import GameMode
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
