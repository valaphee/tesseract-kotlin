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
import com.valaphee.tesseract.actor.ActorEquipmentPacket
import com.valaphee.tesseract.actor.ActorEventPacket
import com.valaphee.tesseract.actor.ActorRemovePacket
import com.valaphee.tesseract.actor.ArmorPacket
import com.valaphee.tesseract.actor.ExperienceOrbAddPacket
import com.valaphee.tesseract.actor.HealthPacket
import com.valaphee.tesseract.actor.LinkPacket
import com.valaphee.tesseract.actor.PaintingAddPacket
import com.valaphee.tesseract.actor.attribute.AttributesPacket
import com.valaphee.tesseract.actor.effect.EffectPacket
import com.valaphee.tesseract.actor.location.MoveRotatePacket
import com.valaphee.tesseract.actor.location.TeleportPacket
import com.valaphee.tesseract.actor.location.VelocityPacket
import com.valaphee.tesseract.actor.metadata.MetadataPacket
import com.valaphee.tesseract.actor.player.ActorPickPacket
import com.valaphee.tesseract.actor.player.AdventureSettingsPacket
import com.valaphee.tesseract.actor.player.BlockPickPacket
import com.valaphee.tesseract.actor.player.EmotePacket
import com.valaphee.tesseract.actor.player.EmotesPacket
import com.valaphee.tesseract.actor.player.InputCorrectPacket
import com.valaphee.tesseract.actor.player.InputPacket
import com.valaphee.tesseract.actor.player.InteractPacket
import com.valaphee.tesseract.actor.player.PlayerActionPacket
import com.valaphee.tesseract.actor.player.PlayerAddPacket
import com.valaphee.tesseract.actor.player.PlayerLocationPacket
import com.valaphee.tesseract.actor.player.RiderJumpPacket
import com.valaphee.tesseract.actor.player.SteerPacket
import com.valaphee.tesseract.actor.player.VelocityPredictionPacket
import com.valaphee.tesseract.actor.player.appearance.AppearancePacket
import com.valaphee.tesseract.actor.player.view.ViewDistancePacket
import com.valaphee.tesseract.actor.player.view.ViewDistanceRequestPacket
import com.valaphee.tesseract.actor.stack.StackAddPacket
import com.valaphee.tesseract.actor.stack.StackTakePacket
import com.valaphee.tesseract.command.net.CommandPacket
import com.valaphee.tesseract.command.net.CommandResponsePacket
import com.valaphee.tesseract.command.net.CommandSettingsPacket
import com.valaphee.tesseract.command.net.CommandSoftEnumerationPacket
import com.valaphee.tesseract.command.net.CommandsPacket
import com.valaphee.tesseract.command.net.LocalPlayerAsInitializedPacket
import com.valaphee.tesseract.form.FormPacket
import com.valaphee.tesseract.form.FormResponsePacket
import com.valaphee.tesseract.form.ServerSettingsPacket
import com.valaphee.tesseract.form.ServerSettingsRequestPacket
import com.valaphee.tesseract.inventory.CreativeInventoryPacket
import com.valaphee.tesseract.inventory.EnchantOptionsPacket
import com.valaphee.tesseract.inventory.EquipmentPacket
import com.valaphee.tesseract.inventory.HotbarPacket
import com.valaphee.tesseract.inventory.InventoryContentPacket
import com.valaphee.tesseract.inventory.InventoryRequestPacket
import com.valaphee.tesseract.inventory.InventoryResponsePacket
import com.valaphee.tesseract.inventory.InventorySlotPacket
import com.valaphee.tesseract.inventory.InventoryTransactionPacket
import com.valaphee.tesseract.inventory.TradePacket
import com.valaphee.tesseract.inventory.WindowClosePacket
import com.valaphee.tesseract.inventory.WindowOpenPacket
import com.valaphee.tesseract.inventory.WindowPropertyPacket
import com.valaphee.tesseract.inventory.item.craft.CraftingEventPacket
import com.valaphee.tesseract.inventory.item.craft.RecipesPacket
import com.valaphee.tesseract.net.base.CacheBlobStatusPacket
import com.valaphee.tesseract.net.base.CacheBlobsPacket
import com.valaphee.tesseract.net.base.CacheStatusPacket
import com.valaphee.tesseract.net.base.DisconnectPacket
import com.valaphee.tesseract.net.base.FilterPacket
import com.valaphee.tesseract.net.base.LatencyPacket
import com.valaphee.tesseract.net.base.PositionTrackingDbClientRequestPacket
import com.valaphee.tesseract.net.base.PositionTrackingDbServerBroadcastPacket
import com.valaphee.tesseract.net.base.TransferPacket
import com.valaphee.tesseract.net.base.ViolationPacket
import com.valaphee.tesseract.net.init.BehaviorTreePacket
import com.valaphee.tesseract.net.init.BiomeDefinitionsPacket
import com.valaphee.tesseract.net.init.ClientToServerHandshakePacket
import com.valaphee.tesseract.net.init.EntityIdentifiersPacket
import com.valaphee.tesseract.net.init.LoginPacket
import com.valaphee.tesseract.net.init.ServerToClientHandshakePacket
import com.valaphee.tesseract.net.init.StatusPacket
import com.valaphee.tesseract.net.init.SubLoginPacket
import com.valaphee.tesseract.net.init.pack.PackDataChunkPacket
import com.valaphee.tesseract.net.init.pack.PackDataChunkRequestPacket
import com.valaphee.tesseract.net.init.pack.PackDataPacket
import com.valaphee.tesseract.net.init.pack.PacksPacket
import com.valaphee.tesseract.net.init.pack.PacksResponsePacket
import com.valaphee.tesseract.net.init.pack.PacksStackPacket
import com.valaphee.tesseract.world.BossBarPacket
import com.valaphee.tesseract.world.DifficultyPacket
import com.valaphee.tesseract.world.DimensionPacket
import com.valaphee.tesseract.world.FogPacket
import com.valaphee.tesseract.world.GameModePacket
import com.valaphee.tesseract.world.GameRulesPacket
import com.valaphee.tesseract.world.PlayerListPacket
import com.valaphee.tesseract.world.RespawnPacket
import com.valaphee.tesseract.world.ShowCreditsPacket
import com.valaphee.tesseract.world.SoundEventPacket
import com.valaphee.tesseract.world.SoundEventPacketV1
import com.valaphee.tesseract.world.SoundEventPacketV2
import com.valaphee.tesseract.world.SoundPacket
import com.valaphee.tesseract.world.SoundStopPacket
import com.valaphee.tesseract.world.SpawnPositionPacket
import com.valaphee.tesseract.world.TextPacket
import com.valaphee.tesseract.world.TickSyncPacket
import com.valaphee.tesseract.world.TimePacket
import com.valaphee.tesseract.world.TitlePacket
import com.valaphee.tesseract.world.WorldEventPacket
import com.valaphee.tesseract.world.WorldPacket
import com.valaphee.tesseract.world.chunk.BlockUpdatePacket
import com.valaphee.tesseract.world.chunk.BlockUpdateSyncedPacket
import com.valaphee.tesseract.world.chunk.BlockUpdatesSyncedPacket
import com.valaphee.tesseract.world.chunk.ChunkPacket
import com.valaphee.tesseract.world.chunk.ChunkPublishPacket
import com.valaphee.tesseract.world.map.MapCreateLockedCopyPacket
import com.valaphee.tesseract.world.map.MapPacket
import com.valaphee.tesseract.world.map.MapRequestPacket
import com.valaphee.tesseract.world.scoreboard.ObjectiveRemovePacket
import com.valaphee.tesseract.world.scoreboard.ObjectiveSetPacket
import com.valaphee.tesseract.world.scoreboard.ScoreboardIdentityPacket
import com.valaphee.tesseract.world.scoreboard.ScoresPacket

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

    fun time(packet: TimePacket) = other(packet)

    fun world(packet: WorldPacket) = other(packet)

    fun playerAdd(packet: PlayerAddPacket) = other(packet)

    fun actorAdd(packet: ActorAddPacket) = other(packet)

    fun actorRemove(packet: ActorRemovePacket) = other(packet)

    fun stackAdd(packet: StackAddPacket) = other(packet)

    fun stackTake(packet: StackTakePacket) = other(packet)

    fun teleport(packet: TeleportPacket) = other(packet)

    fun playerLocation(packet: PlayerLocationPacket) = other(packet)

    fun riderJump(packet: RiderJumpPacket) = other(packet)

    fun blockUpdate(packet: BlockUpdatePacket) = other(packet)

    fun paintingAdd(packet: PaintingAddPacket) = other(packet)

    fun tickSync(packet: TickSyncPacket) = other(packet)

    fun soundEventV1(packet: SoundEventPacketV1) = other(packet)

    fun worldEvent(packet: WorldEventPacket) = other(packet)

    fun actorEvent(packet: ActorEventPacket) = other(packet)

    fun effect(packet: EffectPacket) = other(packet)

    fun attributes(packet: AttributesPacket) = other(packet)

    fun inventoryTransaction(packet: InventoryTransactionPacket) = other(packet)

    fun actorEquipment(packet: ActorEquipmentPacket) = other(packet)

    fun armor(packet: ArmorPacket) = other(packet)

    fun interact(packet: InteractPacket) = other(packet)

    fun blockPick(packet: BlockPickPacket) = other(packet)

    fun actorPick(packet: ActorPickPacket) = other(packet)

    fun playerAction(packet: PlayerActionPacket) = other(packet)

    fun metadata(packet: MetadataPacket) = other(packet)

    fun velocity(packet: VelocityPacket) = other(packet)

    fun link(packet: LinkPacket) = other(packet)

    fun health(packet: HealthPacket) = other(packet)

    fun spawnPosition(packet: SpawnPositionPacket) = other(packet)

    fun respawn(packet: RespawnPacket) = other(packet)

    fun windowOpen(packet: WindowOpenPacket) = other(packet)

    fun windowClose(packet: WindowClosePacket) = other(packet)

    fun hotbar(packet: HotbarPacket) = other(packet)

    fun inventoryContent(packet: InventoryContentPacket) = other(packet)

    fun inventorySlot(packet: InventorySlotPacket) = other(packet)

    fun windowProperty(packet: WindowPropertyPacket) = other(packet)

    fun recipes(packet: RecipesPacket) = other(packet)

    fun craftingEvent(packet: CraftingEventPacket) = other(packet)

    fun adventureSettings(packet: AdventureSettingsPacket) = other(packet)

    fun steer(packet: SteerPacket) = other(packet)

    fun chunk(packet: ChunkPacket) = other(packet)

    fun commandSettings(packet: CommandSettingsPacket) = other(packet)

    fun difficulty(packet: DifficultyPacket) = other(packet)

    fun dimension(packet: DimensionPacket) = other(packet)

    fun gameMode(packet: GameModePacket) = other(packet)

    fun playerList(packet: PlayerListPacket) = other(packet)

    fun experienceOrbAdd(packet: ExperienceOrbAddPacket) = other(packet)

    fun map(packet: MapPacket) = other(packet)

    fun mapRequest(packet: MapRequestPacket) = other(packet)

    fun viewDistanceRequest(packet: ViewDistanceRequestPacket) = other(packet)

    fun viewDistance(packet: ViewDistancePacket) = other(packet)

    fun gameRules(packet: GameRulesPacket) = other(packet)

    fun bossBar(packet: BossBarPacket) = other(packet)

    fun showCredits(packet: ShowCreditsPacket) = other(packet)

    fun commands(packet: CommandsPacket) = other(packet)

    fun command(packet: CommandPacket) = other(packet)

    fun commandResponse(packet: CommandResponsePacket) = other(packet)

    fun trade(packet: TradePacket) = other(packet)

    fun equipment(packet: EquipmentPacket) = other(packet)

    fun packData(packet: PackDataPacket) = other(packet)

    fun packDataChunk(packet: PackDataChunkPacket) = other(packet)

    fun packDataChunkRequest(packet: PackDataChunkRequestPacket) = other(packet)

    fun transfer(packet: TransferPacket) = other(packet)

    fun sound(packet: SoundPacket) = other(packet)

    fun soundStop(packet: SoundStopPacket) = other(packet)

    fun title(packet: TitlePacket) = other(packet)

    fun behaviorTree(packet: BehaviorTreePacket) = other(packet)

    fun appearance(packet: AppearancePacket) = other(packet)

    fun subLogin(packet: SubLoginPacket) = other(packet)

    fun form(packet: FormPacket) = other(packet)

    fun formResponse(packet: FormResponsePacket) = other(packet)

    fun serverSettingsRequest(packet: ServerSettingsRequestPacket) = other(packet)

    fun serverSettings(packet: ServerSettingsPacket) = other(packet)

    fun objectiveRemove(packet: ObjectiveRemovePacket) = other(packet)

    fun objectiveSet(packet: ObjectiveSetPacket) = other(packet)

    fun scores(packet: ScoresPacket) = other(packet)

    fun blockUpdateSynced(packet: BlockUpdateSyncedPacket) = other(packet)

    fun moveRotate(packet: MoveRotatePacket) = other(packet)

    fun scoreboardIdentity(packet: ScoreboardIdentityPacket) = other(packet)

    fun localPlayerAsInitialized(packet: LocalPlayerAsInitializedPacket) = other(packet)

    fun commandSoftEnumeration(packet: CommandSoftEnumerationPacket) = other(packet)

    fun latency(packet: LatencyPacket) = other(packet)

    fun entityIdentifiers(packet: EntityIdentifiersPacket) = other(packet)

    fun soundEventV2(packet: SoundEventPacketV2) = other(packet)

    fun chunkPublish(packet: ChunkPublishPacket) = other(packet)

    fun biomeDefinitions(packet: BiomeDefinitionsPacket) = other(packet)

    fun soundEvent(packet: SoundEventPacket) = other(packet)

    fun cacheStatus(packet: CacheStatusPacket) = other(packet)

    fun mapCreateLockedCopy(packet: MapCreateLockedCopyPacket) = other(packet)

    fun cacheBlobStatus(packet: CacheBlobStatusPacket) = other(packet)

    fun cacheBlobs(packet: CacheBlobsPacket) = other(packet)

    fun emote(packet: EmotePacket) = other(packet)

    fun input(packet: InputPacket) = other(packet)

    fun creativeInventory(packet: CreativeInventoryPacket) = other(packet)

    fun enchantOptions(packet: EnchantOptionsPacket) = other(packet)

    fun inventoryRequest(packet: InventoryRequestPacket) = other(packet)

    fun inventoryResponse(packet: InventoryResponsePacket) = other(packet)

    fun emotes(packet: EmotesPacket) = other(packet)

    fun positionTrackingDbClientRequest(packet: PositionTrackingDbClientRequestPacket) = other(packet)

    fun positionTrackingDbServerBroadcast(packet: PositionTrackingDbServerBroadcastPacket) = other(packet)

    fun violation(packet: ViolationPacket) = other(packet)

    fun velocityPrediction(packet: VelocityPredictionPacket) = other(packet)

    fun fog(packet: FogPacket) = other(packet)

    fun inputCorrect(packet: InputCorrectPacket) = other(packet)

    fun filter(packet: FilterPacket) = other(packet)

    fun blockUpdatesSynced(packet: BlockUpdatesSyncedPacket) = other(packet)
}
