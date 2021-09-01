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

import com.valaphee.tesseract.actor.ActorAddPacket
import com.valaphee.tesseract.actor.RemoveEntityPacket
import com.valaphee.tesseract.actor.attribute.AttributesPacket
import com.valaphee.tesseract.actor.location.MoveRotatePacket
import com.valaphee.tesseract.actor.location.TeleportPacket
import com.valaphee.tesseract.actor.metadata.MetadataPacket
import com.valaphee.tesseract.actor.player.InputPacket
import com.valaphee.tesseract.actor.player.InteractPacket
import com.valaphee.tesseract.actor.player.PlayerActionPacket
import com.valaphee.tesseract.actor.player.PlayerAddPacket
import com.valaphee.tesseract.actor.player.PlayerLocationPacket
import com.valaphee.tesseract.actor.player.view.ChunkPacket
import com.valaphee.tesseract.actor.player.view.ChunkPublishPacket
import com.valaphee.tesseract.actor.player.view.ViewDistancePacket
import com.valaphee.tesseract.actor.player.view.ViewDistanceRequestPacket
import com.valaphee.tesseract.command.net.CommandPacket
import com.valaphee.tesseract.command.net.CommandResponsePacket
import com.valaphee.tesseract.inventory.CreativeInventoryPacket
import com.valaphee.tesseract.inventory.InventoryContentPacket
import com.valaphee.tesseract.inventory.InventoryRequestPacket
import com.valaphee.tesseract.inventory.InventoryResponsePacket
import com.valaphee.tesseract.inventory.InventorySlotPacket
import com.valaphee.tesseract.inventory.WindowClosePacket
import com.valaphee.tesseract.inventory.WindowOpenPacket
import com.valaphee.tesseract.inventory.WindowPropertyPacket
import com.valaphee.tesseract.inventory.recipe.RecipesPacket
import com.valaphee.tesseract.net.base.CacheBlobStatusPacket
import com.valaphee.tesseract.net.base.CacheBlobsPacket
import com.valaphee.tesseract.net.base.CacheStatusPacket
import com.valaphee.tesseract.net.base.DisconnectPacket
import com.valaphee.tesseract.net.base.LocalPlayerAsInitializedPacket
import com.valaphee.tesseract.net.base.TextPacket
import com.valaphee.tesseract.net.init.BehaviorTreePacket
import com.valaphee.tesseract.net.init.BiomeDefinitionsPacket
import com.valaphee.tesseract.net.init.ClientToServerHandshakePacket
import com.valaphee.tesseract.net.init.EntityIdentifiersPacket
import com.valaphee.tesseract.net.init.LoginPacket
import com.valaphee.tesseract.net.init.PacksPacket
import com.valaphee.tesseract.net.init.PacksResponsePacket
import com.valaphee.tesseract.net.init.PacksStackPacket
import com.valaphee.tesseract.net.init.ServerToClientHandshakePacket
import com.valaphee.tesseract.net.init.StatusPacket
import com.valaphee.tesseract.world.PlayerListPacket
import com.valaphee.tesseract.world.SoundEventPacket
import com.valaphee.tesseract.world.SoundEventPacketV1
import com.valaphee.tesseract.world.SoundEventPacketV2
import com.valaphee.tesseract.world.SoundPacket
import com.valaphee.tesseract.world.SoundStopPacket
import com.valaphee.tesseract.world.WorldEventPacket
import com.valaphee.tesseract.world.WorldPacket
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

    fun playerAdd(packet: PlayerAddPacket) = other(packet)

    fun actorAdd(packet: ActorAddPacket) = other(packet)

    fun actorRemove(packet: RemoveEntityPacket) = other(packet)

    fun teleport(packet: TeleportPacket) = other(packet)

    fun playerLocation(packet: PlayerLocationPacket) = other(packet)

    fun blockUpdate(packet: BlockUpdatePacket) = other(packet)

    fun soundEventV1(packet: SoundEventPacketV1) = other(packet)

    fun worldEvent(packet: WorldEventPacket) = other(packet)

    fun attributes(packet: AttributesPacket) = other(packet)

    fun interact(packet: InteractPacket) = other(packet)

    fun playerAction(packet: PlayerActionPacket) = other(packet)

    fun metadata(packet: MetadataPacket) = other(packet)

    fun windowOpen(packet: WindowOpenPacket) = other(packet)

    fun windowClose(packet: WindowClosePacket) = other(packet)

    fun inventoryContent(packet: InventoryContentPacket) = other(packet)

    fun inventorySlot(packet: InventorySlotPacket) = other(packet)

    fun windowProperty(packet: WindowPropertyPacket) = other(packet)

    fun recipes(packet: RecipesPacket) = other(packet)

    fun chunk(packet: ChunkPacket) = other(packet)

    fun playerList(packet: PlayerListPacket) = other(packet)

    fun viewDistanceRequest(packet: ViewDistanceRequestPacket) = other(packet)

    fun viewDistance(packet: ViewDistancePacket) = other(packet)

    fun command(packet: CommandPacket) = other(packet)

    fun commandResponse(packet: CommandResponsePacket) = other(packet)

    fun sound(packet: SoundPacket) = other(packet)

    fun soundStop(packet: SoundStopPacket) = other(packet)

    fun behaviorTree(packet: BehaviorTreePacket) = other(packet)

    fun blockUpdateSynced(packet: BlockUpdateSyncedPacket) = other(packet)

    fun moveRotate(packet: MoveRotatePacket) = other(packet)

    fun localPlayerAsInitialized(packet: LocalPlayerAsInitializedPacket) = other(packet)

    fun entityIdentifiers(packet: EntityIdentifiersPacket) = other(packet)

    fun soundEventV2(packet: SoundEventPacketV2) = other(packet)

    fun chunkPublish(packet: ChunkPublishPacket) = other(packet)

    fun biomeDefinitions(packet: BiomeDefinitionsPacket) = other(packet)

    fun soundEvent(packet: SoundEventPacket) = other(packet)

    fun cacheStatus(packet: CacheStatusPacket) = other(packet)

    fun cacheBlobStatus(packet: CacheBlobStatusPacket) = other(packet)

    fun cacheBlobs(packet: CacheBlobsPacket) = other(packet)

    fun input(packet: InputPacket) = other(packet)

    fun creativeInventory(packet: CreativeInventoryPacket) = other(packet)

    fun inventoryRequest(packet: InventoryRequestPacket) = other(packet)

    fun inventoryResponse(packet: InventoryResponsePacket) = other(packet)
}
