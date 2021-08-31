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

package com.valaphee.tesseract.world

import Difficulty
import Dimension
import GameMode
import Rank
import RecipesPacket
import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.actor.location.Location
import com.valaphee.tesseract.actor.location.Teleport
import com.valaphee.tesseract.actor.location.position
import com.valaphee.tesseract.actor.location.rotation
import com.valaphee.tesseract.actor.player.AuthExtra
import com.valaphee.tesseract.actor.player.InteractPacket
import com.valaphee.tesseract.actor.player.Player
import com.valaphee.tesseract.actor.player.PlayerActionPacket
import com.valaphee.tesseract.actor.player.PlayerLocationPacket
import com.valaphee.tesseract.actor.player.PlayerType
import com.valaphee.tesseract.actor.player.User
import com.valaphee.tesseract.actor.player.authExtra
import com.valaphee.tesseract.actor.player.breakBlock
import com.valaphee.tesseract.actor.player.view.ChunkPacket
import com.valaphee.tesseract.actor.player.view.View
import com.valaphee.tesseract.actor.player.view.ViewDistancePacket
import com.valaphee.tesseract.actor.player.view.ViewDistanceRequestPacket
import com.valaphee.tesseract.biomeDefinitionsPacket
import com.valaphee.tesseract.command.net.CommandPacket
import com.valaphee.tesseract.command.net.Origin
import com.valaphee.tesseract.creativeInventoryPacket
import com.valaphee.tesseract.entityIdentifiersPacket
import com.valaphee.tesseract.inventory.Inventory
import com.valaphee.tesseract.inventory.InventoryHolder
import com.valaphee.tesseract.inventory.InventoryRequestPacket
import com.valaphee.tesseract.inventory.WindowClosePacket
import com.valaphee.tesseract.inventory.WindowType
import com.valaphee.tesseract.inventory.inventory
import com.valaphee.tesseract.inventory.item.Item
import com.valaphee.tesseract.net.Connection
import com.valaphee.tesseract.net.GamePublishMode
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.Remote
import com.valaphee.tesseract.net.base.CacheBlobStatusPacket
import com.valaphee.tesseract.net.base.CacheBlobsPacket
import com.valaphee.tesseract.net.base.TextPacket
import com.valaphee.tesseract.net.init.StatusPacket
import com.valaphee.tesseract.world.chunk.Chunk
import com.valaphee.tesseract.world.chunk.ChunkRelease
import com.valaphee.tesseract.world.chunk.terrain.SectionCompact
import com.valaphee.tesseract.world.chunk.terrain.block.Block
import com.valaphee.tesseract.world.chunk.terrain.terrain
import com.valaphee.tesseract.world.entity.addEntities
import com.valaphee.tesseract.world.entity.removeEntities
import io.netty.buffer.Unpooled
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import net.jpountz.xxhash.XXHashFactory
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

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
    private var playerSpawned = false

    private val cacheBlobs = mutableMapOf<Long, ByteArray>()

    override fun initialize() {
        user.appearance.id = "0"

        player = (context.provider.loadPlayer(authExtra.userId) ?: context.entityFactory(PlayerType, setOf(
            Location(Float3(0.0f, 100.0f, 0.0f), Float2.Zero)
        ))).asMutableEntity().apply {
            addAttribute(Remote(connection))
            addAttribute(this@WorldPacketHandler.authExtra)
            addAttribute(this@WorldPacketHandler.user)
            addAttribute(InventoryHolder(Inventory(WindowType.Inventory)))
        }
        context.world.addEntities(context, null, player)

        val environment = context.world.environment
        connection.write(
            WorldPacket(
                player.id,
                player.id,
                GameMode.Default,
                player.position,
                player.rotation,
                0,
                WorldPacket.BiomeType.Default,
                "",
                Dimension.Overworld,
                WorldPacket.Overworld,
                GameMode.Creative,
                Difficulty.Normal,
                Int3.Zero,
                true,
                environment.time,
                0,
                false,
                "",
                WorldPacket.EducationEditionOffer.None,
                environment.rainLevel,
                environment.thunderLevel,
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
                context.cycle,
                0,
                null,
                null,
                null,
                Block.all.toTypedArray(),
                null,
                Item.all.toTypedArray(),
                "",
                true,
                0,
                true,
                "Tesseract"
            )
        )

        connection.write(biomeDefinitionsPacket)
        connection.write(entityIdentifiersPacket)
        connection.write(creativeInventoryPacket)
        connection.write(RecipesPacket(emptyArray(), emptyArray(), emptyArray(), true))

        player.sendMessage(Teleport(context, player, player, player.position, player.rotation)) // notify view
    }

    override fun destroy() {
        context.world.sendMessage(ChunkRelease(context, player, player.findFacet(View::class).acquiredChunks))
        context.world.removeEntities(context, null, player.id)
        context.provider.savePlayer(authExtra.userId, player)
    }

    override fun other(packet: Packet) = Unit

    override fun text(packet: TextPacket) {
        if (packet.type != TextPacket.Type.Chat || packet.xboxUserId != player.authExtra.xboxUserId) return

        context.world.broadcast(packet)
        chatLog.info("{}: {}", player.authExtra.userName, packet.message)
    }

    override fun playerLocation(packet: PlayerLocationPacket) {
        if (packet.player != player) return

        player.sendMessage(Teleport(context, player, player, packet.position, packet.rotation))
    }

    override fun interact(packet: InteractPacket) {
        when (packet.action) {
            InteractPacket.Action.OpenInventory -> {
                packet.actor?.let {
                    if (it != player) return

                    it.inventory.open(player)
                }
            }
        }
    }

    override fun playerAction(packet: PlayerActionPacket) {
        when (packet.action) {
            PlayerActionPacket.Action.DimensionChangeRequestOrCreativeBlockDestroy -> context.world.breakBlock(context, player, packet.blockPosition)
        }
    }

    override fun windowClose(packet: WindowClosePacket) {}

    override fun viewDistanceRequest(packet: ViewDistanceRequestPacket) {
        connection.write(ViewDistancePacket(player.findFacet(View::class).apply { distance = packet.distance }.distance))
    }

    override fun command(packet: CommandPacket) {
        if (packet.origin.where != Origin.Where.Player) return
    }

    override fun cacheBlobStatus(packet: CacheBlobStatusPacket) {
        val blobs = Long2ObjectOpenHashMap<ByteArray>()
        packet.misses.forEach { blobId -> this.cacheBlobs.remove(blobId)?.let { blobs[blobId] = it } }
        packet.hits.forEach { this.cacheBlobs.remove(it) }
        if (blobs.isNotEmpty()) connection.write(CacheBlobsPacket(blobs))
    }

    override fun inventoryRequest(packet: InventoryRequestPacket) {
    }

    fun writeChunks(chunks: Array<Chunk>) {
        chunks.forEach {
            if (caching) {
                val blockStorage = it.terrain.blockStorage
                var sectionCount = blockStorage.sections.size - 1
                while (sectionCount >= 0 && blockStorage.sections[sectionCount].empty) sectionCount--
                sectionCount++
                val blobIds = LongArray(sectionCount + 1)
                PacketBuffer(Unpooled.buffer()).use {
                    repeat(sectionCount) { i ->
                        val section = blockStorage.sections[i]
                        if (section is SectionCompact) section.writeToBuffer(it, false)
                        else section.writeToBuffer(it)
                        val blob = it.array().clone()
                        it.clear()
                        val blobId = xxHash64.hash(blob, 0, blob.size, 0)
                        cacheBlobs[blobId] = blob
                        blobIds[i] = blobId
                    }
                }
                connection.write(ChunkPacket(it, blobIds))
            } else connection.write(ChunkPacket(it))
        }

        if (!playerSpawned) {
            connection.write(StatusPacket(StatusPacket.Status.PlayerSpawn))
            playerSpawned = true
        }
    }

    companion object {
        private val chatLog: Logger = LogManager.getLogger("Chat")
        private val xxHash64 = XXHashFactory.fastestInstance().hash64()
    }
}
