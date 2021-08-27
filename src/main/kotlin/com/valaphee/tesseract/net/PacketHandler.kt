/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net

import BehaviorTreePacket
import BiomeDefinitionsPacket
import CreativeInventoryPacket
import EntityIdentifiersPacket
import com.valaphee.tesseract.actor.location.MoveRotatePacket
import com.valaphee.tesseract.actor.location.TeleportPacket
import com.valaphee.tesseract.actor.player.PlayerLocationPacket
import com.valaphee.tesseract.inventory.recipe.RecipesPacket
import com.valaphee.tesseract.net.base.DisconnectPacket
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
import com.valaphee.tesseract.world.chunk.ChunkCacheBlobStatusPacket
import com.valaphee.tesseract.world.chunk.ChunkCacheBlobsPacket
import com.valaphee.tesseract.world.chunk.ChunkCacheStatusPacket
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

    fun playerLocation(packet: PlayerLocationPacket) = other(packet)

    fun teleport(packet: TeleportPacket) = other(packet)

    fun moveRotate(packet: MoveRotatePacket) = other(packet)

    fun chunkPublish(packet: ChunkPublishPacket) = other(packet)

    fun chunkAdd(packet: ChunkAddPacket) = other(packet)

    fun chunkCacheBlobs(packet: ChunkCacheBlobsPacket) = other(packet)

    fun chunkCacheBlobStatus(packet: ChunkCacheBlobStatusPacket) = other(packet)

    fun chunkCacheStatus(packet: ChunkCacheStatusPacket) = other(packet)

    fun blockUpdate(packet: BlockUpdatePacket) = other(packet)

    fun blockUpdateSynced(packet: BlockUpdateSyncedPacket) = other(packet)

    fun creativeInventory(packet: CreativeInventoryPacket) = other(packet)

    fun recipes(packet: RecipesPacket) = other(packet)

    fun entityIdentifiers(packet: EntityIdentifiersPacket) = other(packet)

    fun behaviorTree(packet: BehaviorTreePacket) = other(packet)

    fun biomeDefinitions(packet: BiomeDefinitionsPacket) = other(packet)
}
