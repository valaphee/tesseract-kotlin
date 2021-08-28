/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world

import ConnectionAttribute
import Difficulty
import Dimension
import GameMode
import Rank
import RecipesPacket
import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.actor.Actor
import com.valaphee.tesseract.actor.location.Location
import com.valaphee.tesseract.actor.location.Teleport
import com.valaphee.tesseract.actor.location.position
import com.valaphee.tesseract.actor.location.rotation
import com.valaphee.tesseract.actor.player.AuthExtra
import com.valaphee.tesseract.actor.player.Player
import com.valaphee.tesseract.actor.player.PlayerLocationPacket
import com.valaphee.tesseract.actor.player.PlayerType
import com.valaphee.tesseract.actor.player.User
import com.valaphee.tesseract.actor.player.View
import com.valaphee.tesseract.actor.player.ViewDistancePacket
import com.valaphee.tesseract.actor.player.ViewDistanceRequestPacket
import com.valaphee.tesseract.biomeDefinitionsPacket
import com.valaphee.tesseract.creativeInventoryPacket
import com.valaphee.tesseract.entityIdentifiersPacket
import com.valaphee.tesseract.item.Item
import com.valaphee.tesseract.net.Connection
import com.valaphee.tesseract.net.GamePublishMode
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.base.StatusPacket
import com.valaphee.tesseract.world.chunk.Chunk
import com.valaphee.tesseract.world.chunk.ChunkAddPacket
import com.valaphee.tesseract.net.base.CacheBlobStatusPacket
import com.valaphee.tesseract.net.base.CacheBlobsPacket
import com.valaphee.tesseract.world.chunk.terrain.block.Block
import com.valaphee.tesseract.world.chunk.terrain.terrain
import com.valaphee.tesseract.world.entity.addEntities
import com.valaphee.tesseract.world.entity.removeEntities
import io.netty.buffer.Unpooled
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import net.jpountz.xxhash.XXHashFactory

/**
 * @author Kevin Ludwig
 */
class WorldPacketHandler(
    private val context: WorldContext,
    private val connection: Connection,
    private val authExtra: AuthExtra,
    private val user: User,
    private val caching: Boolean
) : PacketHandler {
    lateinit var player: Player

    private val cacheBlobs = mutableMapOf<Long, ByteArray>()

    override fun initialize() {
        player = context.entityFactory(PlayerType, setOf(Location(Float3(0.0f, 200.0f, 0.0f), Float2.Zero), ConnectionAttribute(connection)))
        context.world.addEntities(context, null, player)

        @Suppress("UNCHECKED_CAST") val actor = player as Actor
        connection.write(
            WorldPacket(
                player.id,
                player.id,
                GameMode.Default,
                actor.position,
                actor.rotation,
                0,
                WorldPacket.BiomeType.Default,
                "",
                Dimension.Overworld,
                WorldPacket.Overworld,
                GameMode.Creative,
                Difficulty.Normal,
                Int3.Zero,
                true,
                -1,
                0,
                false,
                "",
                WorldPacket.EducationEditionOffer.None,
                0.0f,
                0.0f,
                false,
                true,
                true,
                GamePublishMode.Public,
                GamePublishMode.Public,
                true,
                false,
                arrayOf(),
                arrayOf(),
                false,
                false,
                false,
                Rank.Operator,
                4,
                false,
                false,
                false,
                true,
                false,
                false,
                false,
                "*",
                16,
                16,
                true,
                false,
                "",
                "Tesseract",
                "",
                false,
                WorldPacket.AuthoritativeMovement.Client,
                context.engine.cycle,
                0,
                null,
                null,
                null,
                Block.all.toTypedArray(),
                null,
                Item.all.toTypedArray(),
                "",
                false,
                0,
                false,
                "Tesseract"
            )
        )
        connection.write(biomeDefinitionsPacket)
        connection.write(entityIdentifiersPacket)
        connection.write(creativeInventoryPacket)
        connection.write(RecipesPacket(emptyArray(), emptyArray(), emptyArray(), true))
        connection.write(StatusPacket(StatusPacket.Status.PlayerSpawn))
    }

    override fun destroy() {
        context.world.removeEntities(context, null, player.id)
    }

    override fun other(packet: Packet) = Unit

    override fun playerLocation(packet: PlayerLocationPacket) {
        /*@Suppress("UNCHECKED_CAST") val actor = player as Actor
        val position = actor.position
        val newPosition = packet.position
        val positionalMovement = position != newPosition
        val newRotation = packet.rotation
        val rotationalMovement = actor.rotation != newRotation
        if (positionalMovement && rotationalMovement) player.sendMessage(MoveRotate(context, player, newPosition.toMutableFloat3().sub(position), newRotation))
        else if (positionalMovement) player.sendMessage(Move(context, player, newPosition.toMutableFloat3().sub(position)))
        else if (rotationalMovement) player.sendMessage(Rotate(context, player, newRotation))*/
        player.sendMessage(Teleport(context, player, player, packet.position, packet.rotation))
    }

    override fun viewDistanceRequest(packet: ViewDistanceRequestPacket) {
        connection.write(ViewDistancePacket(player.findFacet(View::class).apply { distance = packet.distance }.distance))
    }

    override fun cacheBlobStatus(packet: CacheBlobStatusPacket) {
        val blobs = Long2ObjectOpenHashMap<ByteArray>()
        packet.misses.forEach { blobId -> this.cacheBlobs.remove(blobId)?.let { blobs[blobId] = it } }
        packet.hits.forEach { this.cacheBlobs.remove(it) }
        if (blobs.isNotEmpty()) connection.write(CacheBlobsPacket(blobs))
    }

    fun cacheChunk(chunk: Chunk) {
        if (caching) {
            val blockStorage = chunk.terrain.blockStorage
            var sectionCount = blockStorage.sections.size - 1
            while (sectionCount >= 0 && blockStorage.sections[sectionCount].empty) sectionCount--
            sectionCount++
            val blobIds = LongArray(sectionCount + 1)
            PacketBuffer(Unpooled.buffer()).use {
                repeat(sectionCount) { i ->
                    blockStorage.sections[i].writeToBuffer(it)
                    val blob = it.array().clone()
                    it.clear()
                    val blobId = xxHash64.hash(blob, 0, blob.size, 0)
                    cacheBlobs[blobId] = blob
                    blobIds[i] = blobId
                }
            }
            connection.write(ChunkAddPacket(chunk, blobIds))
        } else connection.write(ChunkAddPacket(chunk))
    }

    companion object {
        private val xxHash64 = XXHashFactory.fastestInstance().hash64()
    }
}
