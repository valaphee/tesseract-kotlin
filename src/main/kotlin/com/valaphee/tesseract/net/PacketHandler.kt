/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net

import RecipesPacket
import com.valaphee.tesseract.actor.location.MoveRotatePacket
import com.valaphee.tesseract.actor.location.TeleportPacket
import com.valaphee.tesseract.actor.player.PlayerLocationPacket
import com.valaphee.tesseract.actor.player.ViewDistancePacket
import com.valaphee.tesseract.actor.player.ViewDistanceRequestPacket
import com.valaphee.tesseract.net.base.BehaviorTreePacket
import com.valaphee.tesseract.net.base.BiomeDefinitionsPacket
import com.valaphee.tesseract.net.base.CreativeInventoryPacket
import com.valaphee.tesseract.net.base.DisconnectPacket
import com.valaphee.tesseract.net.base.EntityIdentifiersPacket
import com.valaphee.tesseract.net.base.StatusPacket
import com.valaphee.tesseract.net.base.TextPacket
import com.valaphee.tesseract.net.init.ClientToServerHandshakePacket
import com.valaphee.tesseract.net.init.LoginPacket
import com.valaphee.tesseract.net.init.PacksPacket
import com.valaphee.tesseract.net.init.PacksResponsePacket
import com.valaphee.tesseract.net.init.PacksStackPacket
import com.valaphee.tesseract.net.init.ServerToClientHandshakePacket
import com.valaphee.tesseract.world.WorldPacket
import com.valaphee.tesseract.world.chunk.ChunkAddPacket
import com.valaphee.tesseract.net.base.CacheBlobStatusPacket
import com.valaphee.tesseract.net.base.CacheBlobsPacket
import com.valaphee.tesseract.net.base.CacheStatusPacket
import com.valaphee.tesseract.world.chunk.ChunkPublishPacket
import com.valaphee.tesseract.world.chunk.terrain.BlockUpdatePacket
import com.valaphee.tesseract.world.chunk.terrain.BlockUpdateSyncedPacket

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

    fun world(packet: WorldPacket) = other(packet)

    fun teleport(packet: TeleportPacket) = other(packet)

    fun playerLocation(packet: PlayerLocationPacket) = other(packet)

    fun blockUpdate(packet: BlockUpdatePacket) = other(packet)

    fun recipes(packet: RecipesPacket) = other(packet)

    fun chunkAdd(packet: ChunkAddPacket) = other(packet)

    fun viewDistanceRequest(packet: ViewDistanceRequestPacket) = other(packet)

    fun viewDistance(packet: ViewDistancePacket) = other(packet)

    fun behaviorTree(packet: BehaviorTreePacket) = other(packet)

    fun blockUpdateSynced(packet: BlockUpdateSyncedPacket) = other(packet)

    fun moveRotate(packet: MoveRotatePacket) = other(packet)

    fun entityIdentifiers(packet: EntityIdentifiersPacket) = other(packet)

    fun chunkPublish(packet: ChunkPublishPacket) = other(packet)

    fun biomeDefinitions(packet: BiomeDefinitionsPacket) = other(packet)

    fun cacheStatus(packet: CacheStatusPacket) = other(packet)

    fun cacheBlobStatus(packet: CacheBlobStatusPacket) = other(packet)

    fun cacheBlobs(packet: CacheBlobsPacket) = other(packet)

    fun creativeInventory(packet: CreativeInventoryPacket) = other(packet)
}
