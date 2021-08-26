/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net

import com.valaphee.tesseract.net.packet.DisconnectPacket
import com.valaphee.tesseract.net.packet.StatusPacket
import com.valaphee.tesseract.net.packet.TextPacket
import com.valaphee.tesseract.init.ClientToServerHandshakePacket
import com.valaphee.tesseract.init.LoginPacket
import com.valaphee.tesseract.init.PacksPacket
import com.valaphee.tesseract.init.PacksResponsePacket
import com.valaphee.tesseract.init.PacksStackPacket
import com.valaphee.tesseract.init.ServerToClientHandshakePacket
import com.valaphee.tesseract.world.TimeUpdatePacket
import com.valaphee.tesseract.world.WorldPacket
import com.valaphee.tesseract.world.chunk.ChunkAddPacket
import com.valaphee.tesseract.world.chunk.ChunkCacheBlobStatusPacket
import com.valaphee.tesseract.world.chunk.ChunkCacheBlobsPacket
import com.valaphee.tesseract.world.chunk.ChunkCacheStatusPacket
import com.valaphee.tesseract.world.chunk.ChunkPublishPacket

/**
 * @author Kevin Ludwig
 */
interface PacketHandler : ProtocolHandler {
    fun other(packet: Packet)

    fun login(packet: LoginPacket) = other(packet)

    fun status(packet: StatusPacket) = other(packet)

    fun serverToClientHandshake(packet: ServerToClientHandshakePacket) = other(packet)

    fun clientToServerHandshake(packet: ClientToServerHandshakePacket) = other(packet)

    fun disconnect(packet: DisconnectPacket) = other(packet)

    fun packs(packet: PacksPacket) = other(packet)

    fun packsStack(packet: PacksStackPacket) = other(packet)

    fun packsResponse(packet: PacksResponsePacket) = other(packet)

    fun text(packet: TextPacket) = other(packet)

    fun timeUpdate(packet: TimeUpdatePacket) = other(packet)

    fun world(packet: WorldPacket) = other(packet)

    fun chunkAdd(packet: ChunkAddPacket) = other(packet)

    fun chunkPublish(packet: ChunkPublishPacket) = other(packet)

    fun chunkCacheStatus(packet: ChunkCacheStatusPacket) = other(packet)

    fun chunkCacheBlobStatus(packet: ChunkCacheBlobStatusPacket) = other(packet)

    fun chunkCacheBlobs(packet: ChunkCacheBlobsPacket) = other(packet)
}
