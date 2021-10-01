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

import com.valaphee.tesseract.command.net.CommandPacketReader
import com.valaphee.tesseract.command.net.CommandResponsePacketReader
import com.valaphee.tesseract.command.net.CommandSettingsPacketReader
import com.valaphee.tesseract.command.net.CommandSoftEnumerationPacketReader
import com.valaphee.tesseract.command.net.CommandsPacketReader
import com.valaphee.tesseract.command.net.LocalPlayerAsInitializedPacketReader
import com.valaphee.tesseract.entity.EntityAddPacketReader
import com.valaphee.tesseract.entity.EntityAnimationPacketReader
import com.valaphee.tesseract.entity.EntityArmorPacketReader
import com.valaphee.tesseract.entity.EntityEquipmentPacketReader
import com.valaphee.tesseract.entity.EntityEventPacketReader
import com.valaphee.tesseract.entity.EntityLinkPacketReader
import com.valaphee.tesseract.entity.EntityRemovePacketReader
import com.valaphee.tesseract.entity.ExperienceOrbAddPacketReader
import com.valaphee.tesseract.entity.PaintingAddPacketReader
import com.valaphee.tesseract.entity.attribute.EntityAttributesPacketReader
import com.valaphee.tesseract.entity.effect.EntityEffectPacketReader
import com.valaphee.tesseract.entity.location.EntityMoveRotatePacketReader
import com.valaphee.tesseract.entity.location.EntityTeleportPacketReader
import com.valaphee.tesseract.entity.location.EntityVelocityPacketReader
import com.valaphee.tesseract.entity.metadata.EntityMetadataPacketReader
import com.valaphee.tesseract.entity.player.AdventureSettingsPacketReader
import com.valaphee.tesseract.entity.player.BlockPickPacketReader
import com.valaphee.tesseract.entity.player.EmotePacketReader
import com.valaphee.tesseract.entity.player.EmotesPacketReader
import com.valaphee.tesseract.entity.player.EntityPickPacketReader
import com.valaphee.tesseract.entity.player.HealthPacketReader
import com.valaphee.tesseract.entity.player.InputCorrectPacketReader
import com.valaphee.tesseract.entity.player.InputPacketReader
import com.valaphee.tesseract.entity.player.InteractPacketReader
import com.valaphee.tesseract.entity.player.PlayerActionPacketReader
import com.valaphee.tesseract.entity.player.PlayerAddPacketReader
import com.valaphee.tesseract.entity.player.PlayerLocationPacketReader
import com.valaphee.tesseract.entity.player.RiderJumpPacketReader
import com.valaphee.tesseract.entity.player.SteerPacketReader
import com.valaphee.tesseract.entity.player.VelocityPredictionPacketReader
import com.valaphee.tesseract.entity.player.appearance.AppearancePacketReader
import com.valaphee.tesseract.entity.player.view.ViewDistancePacketReader
import com.valaphee.tesseract.entity.player.view.ViewDistanceRequestPacketReader
import com.valaphee.tesseract.entity.stack.StackAddPacketReader
import com.valaphee.tesseract.entity.stack.StackTakePacketReader
import com.valaphee.tesseract.form.FormPacketReader
import com.valaphee.tesseract.form.FormResponsePacketReader
import com.valaphee.tesseract.form.ServerSettingsPacketReader
import com.valaphee.tesseract.form.ServerSettingsRequestPacketReader
import com.valaphee.tesseract.inventory.CreativeInventoryPacketReader
import com.valaphee.tesseract.inventory.EnchantOptionsPacketReader
import com.valaphee.tesseract.inventory.EquipmentPacketReader
import com.valaphee.tesseract.inventory.HotbarPacketReader
import com.valaphee.tesseract.inventory.InventoryContentPacketReader
import com.valaphee.tesseract.inventory.InventoryRequestPacketReader
import com.valaphee.tesseract.inventory.InventorySlotPacketReader
import com.valaphee.tesseract.inventory.InventoryTransactionPacketReader
import com.valaphee.tesseract.inventory.TradePacketReader
import com.valaphee.tesseract.inventory.WindowClosePacketReader
import com.valaphee.tesseract.inventory.WindowOpenPacketReader
import com.valaphee.tesseract.inventory.WindowPropertyPacketReader
import com.valaphee.tesseract.inventory.item.craft.CraftingEventPacketReader
import com.valaphee.tesseract.inventory.item.craft.RecipesPacketReader
import com.valaphee.tesseract.net.base.BehaviorTreePacketReader
import com.valaphee.tesseract.net.base.BiomeDefinitionsPacketReader
import com.valaphee.tesseract.net.base.CacheBlobStatusPacketReader
import com.valaphee.tesseract.net.base.CacheBlobsPacketReader
import com.valaphee.tesseract.net.base.CacheStatusPacketReader
import com.valaphee.tesseract.net.base.ClientToServerHandshakePacketReader
import com.valaphee.tesseract.net.base.DisconnectPacketReader
import com.valaphee.tesseract.net.base.EntityIdentifiersPacketReader
import com.valaphee.tesseract.net.base.FilterPacketReader
import com.valaphee.tesseract.net.base.ItemComponentPacketReader
import com.valaphee.tesseract.net.base.LatencyPacketReader
import com.valaphee.tesseract.net.base.LoginPacketReader
import com.valaphee.tesseract.net.base.NetworkSettingsPacketReader
import com.valaphee.tesseract.net.base.PositionTrackingDbClientRequestPacketReader
import com.valaphee.tesseract.net.base.PositionTrackingDbServerBroadcastPacketReader
import com.valaphee.tesseract.net.base.ServerToClientHandshakePacketReader
import com.valaphee.tesseract.net.base.StatusPacketReader
import com.valaphee.tesseract.net.base.SubLoginPacketReader
import com.valaphee.tesseract.net.base.TransferPacketReader
import com.valaphee.tesseract.net.base.ViolationPacketReader
import com.valaphee.tesseract.net.base.pack.PackDataChunkPacketReader
import com.valaphee.tesseract.net.base.pack.PackDataChunkRequestPacketReader
import com.valaphee.tesseract.net.base.pack.PackDataPacketReader
import com.valaphee.tesseract.net.base.pack.PacksPacketReader
import com.valaphee.tesseract.net.base.pack.PacksResponsePacketReader
import com.valaphee.tesseract.net.base.pack.PacksStackPacketReader
import com.valaphee.tesseract.util.Int2ObjectOpenHashBiMap
import com.valaphee.tesseract.world.BossBarPacketReader
import com.valaphee.tesseract.world.DifficultyPacketReader
import com.valaphee.tesseract.world.DimensionPacketReader
import com.valaphee.tesseract.world.FogPacketReader
import com.valaphee.tesseract.world.GameModePacketReader
import com.valaphee.tesseract.world.GameRulesPacketReader
import com.valaphee.tesseract.world.ParticlePacketReader
import com.valaphee.tesseract.world.PlayerListPacketReader
import com.valaphee.tesseract.world.RespawnPacketReader
import com.valaphee.tesseract.world.ShowCreditsPacketReader
import com.valaphee.tesseract.world.SoundEventPacketReader
import com.valaphee.tesseract.world.SoundEventPacketV1Reader
import com.valaphee.tesseract.world.SoundEventPacketV2Reader
import com.valaphee.tesseract.world.SoundPacketReader
import com.valaphee.tesseract.world.SoundStopPacketReader
import com.valaphee.tesseract.world.SpawnPositionPacketReader
import com.valaphee.tesseract.world.TextPacketReader
import com.valaphee.tesseract.world.TickSyncPacketReader
import com.valaphee.tesseract.world.TimePacketReader
import com.valaphee.tesseract.world.TitlePacketReader
import com.valaphee.tesseract.world.WorldEventPacketReader
import com.valaphee.tesseract.world.WorldPacketReader
import com.valaphee.tesseract.world.chunk.BlockEntityPacketReader
import com.valaphee.tesseract.world.chunk.BlockEventPacketReader
import com.valaphee.tesseract.world.chunk.BlockUpdatePacketReader
import com.valaphee.tesseract.world.chunk.BlockUpdateSyncedPacketReader
import com.valaphee.tesseract.world.chunk.ChunkPacketReader
import com.valaphee.tesseract.world.chunk.ChunkPublishPacketReader
import com.valaphee.tesseract.world.map.MapCreateLockedCopyPacketReader
import com.valaphee.tesseract.world.map.MapRequestPacketReader
import com.valaphee.tesseract.world.scoreboard.ObjectiveRemovePacketReader
import com.valaphee.tesseract.world.scoreboard.ObjectiveSetPacketReader
import com.valaphee.tesseract.world.scoreboard.ScoreboardIdentityPacketReader
import com.valaphee.tesseract.world.scoreboard.ScoresPacketReader
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.DecoderException
import io.netty.handler.codec.MessageToMessageDecoder
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap

/**
 * @author Kevin Ludwig
 */
class PacketDecoder(
    var version: Int = -1,
    var blockStates: Int2ObjectOpenHashBiMap<String>? = null,
    var items: Int2ObjectOpenHashBiMap<String>? = null
) : MessageToMessageDecoder<ByteBuf>() {
    override fun decode(context: ChannelHandlerContext, `in`: ByteBuf, out: MutableList<Any>) {
        val buffer = PacketBuffer(`in`, false, blockStates, items)
        val header = buffer.readVarUInt()
        val id = header and Packet.idMask
        readers[id]?.let { out.add(it.read(buffer, version)) } ?: throw DecoderException("Unknown packet: 0x${id.toString(16).uppercase()}")
    }

    companion object {
        private val readers = Int2ObjectOpenHashMap<PacketReader>().apply {
            this[0x01] = LoginPacketReader
            this[0x02] = StatusPacketReader
            this[0x03] = ServerToClientHandshakePacketReader
            this[0x04] = ClientToServerHandshakePacketReader
            this[0x05] = DisconnectPacketReader
            this[0x06] = PacksPacketReader
            this[0x07] = PacksStackPacketReader
            this[0x08] = PacksResponsePacketReader
            this[0x09] = TextPacketReader
            this[0x0A] = TimePacketReader
            this[0x0B] = WorldPacketReader
            this[0x0C] = PlayerAddPacketReader
            this[0x0D] = EntityAddPacketReader
            this[0x0E] = EntityRemovePacketReader
            this[0x0F] = StackAddPacketReader
            this[0x11] = StackTakePacketReader
            this[0x12] = EntityTeleportPacketReader
            this[0x13] = PlayerLocationPacketReader
            this[0x14] = RiderJumpPacketReader
            this[0x15] = BlockUpdatePacketReader
            this[0x16] = PaintingAddPacketReader
            this[0x17] = TickSyncPacketReader
            this[0x18] = SoundEventPacketV1Reader
            this[0x19] = WorldEventPacketReader
            this[0x1A] = BlockEventPacketReader
            this[0x1B] = EntityEventPacketReader
            this[0x1C] = EntityEffectPacketReader
            this[0x1D] = EntityAttributesPacketReader
            this[0x1E] = InventoryTransactionPacketReader
            this[0x1F] = EntityEquipmentPacketReader
            this[0x20] = EntityArmorPacketReader
            this[0x21] = InteractPacketReader
            this[0x22] = BlockPickPacketReader
            this[0x23] = EntityPickPacketReader
            this[0x24] = PlayerActionPacketReader
            //this[0x25] =
            //this[0x26] =
            this[0x27] = EntityMetadataPacketReader
            this[0x28] = EntityVelocityPacketReader
            this[0x29] = EntityLinkPacketReader
            this[0x2A] = HealthPacketReader
            this[0x2B] = SpawnPositionPacketReader
            this[0x2C] = EntityAnimationPacketReader
            this[0x2D] = RespawnPacketReader
            this[0x2E] = WindowOpenPacketReader
            this[0x2F] = WindowClosePacketReader
            this[0x30] = HotbarPacketReader
            this[0x31] = InventoryContentPacketReader
            this[0x32] = InventorySlotPacketReader
            this[0x33] = WindowPropertyPacketReader
            this[0x34] = RecipesPacketReader
            this[0x35] = CraftingEventPacketReader
            //this[0x36] =
            this[0x37] = AdventureSettingsPacketReader
            this[0x38] = BlockEntityPacketReader
            this[0x39] = SteerPacketReader
            this[0x3A] = ChunkPacketReader
            this[0x3B] = CommandSettingsPacketReader
            this[0x3C] = DifficultyPacketReader
            this[0x3D] = DimensionPacketReader
            this[0x3E] = GameModePacketReader
            this[0x3F] = PlayerListPacketReader
            //this[0x40] =
            //this[0x41] =
            this[0x42] = ExperienceOrbAddPacketReader
            //this[0x43] = MapPacketReader
            this[0x44] = MapRequestPacketReader
            this[0x45] = ViewDistanceRequestPacketReader
            this[0x46] = ViewDistancePacketReader
            //this[0x47] =
            this[0x48] = GameRulesPacketReader
            //this[0x49] =
            this[0x4A] = BossBarPacketReader
            this[0x4B] = ShowCreditsPacketReader
            this[0x4C] = CommandsPacketReader
            this[0x4D] = CommandPacketReader
            //this[0x4E] =
            this[0x4F] = CommandResponsePacketReader
            this[0x50] = TradePacketReader
            this[0x51] = EquipmentPacketReader
            this[0x52] = PackDataPacketReader
            this[0x53] = PackDataChunkPacketReader
            this[0x54] = PackDataChunkRequestPacketReader
            this[0x55] = TransferPacketReader
            this[0x56] = SoundPacketReader
            this[0x57] = SoundStopPacketReader
            this[0x58] = TitlePacketReader
            this[0x59] = BehaviorTreePacketReader
            //this[0x5A] =
            //this[0x5B] =
            //this[0x5C] =
            this[0x5D] = AppearancePacketReader
            this[0x5E] = SubLoginPacketReader
            //this[0x5F] =
            //this[0x60] =
            //this[0x61] =
            //this[0x62] =
            //this[0x63] =
            this[0x64] = FormPacketReader
            this[0x65] = FormResponsePacketReader
            this[0x66] = ServerSettingsRequestPacketReader
            this[0x67] = ServerSettingsPacketReader
            //this[0x68] =
            //this[0x69] =
            this[0x6A] = ObjectiveRemovePacketReader
            this[0x6B] = ObjectiveSetPacketReader
            this[0x6C] = ScoresPacketReader
            //this[0x6D] =
            this[0x6E] = BlockUpdateSyncedPacketReader
            this[0x6F] = EntityMoveRotatePacketReader
            this[0x70] = ScoreboardIdentityPacketReader
            this[0x71] = LocalPlayerAsInitializedPacketReader
            this[0x72] = CommandSoftEnumerationPacketReader
            this[0x73] = LatencyPacketReader
            //this[0x74] =
            //this[0x75] =
            this[0x76] = ParticlePacketReader
            this[0x77] = EntityIdentifiersPacketReader
            this[0x78] = SoundEventPacketV2Reader
            this[0x79] = ChunkPublishPacketReader
            this[0x7A] = BiomeDefinitionsPacketReader
            this[0x7B] = SoundEventPacketReader
            //this[0x7C] =
            //this[0x7D] =
            //this[0x7E] =
            //this[0x7F] =
            //this[0x80] =
            this[0x81] = CacheStatusPacketReader
            //this[0x82] =
            this[0x83] = MapCreateLockedCopyPacketReader
            //this[0x84] =
            //this[0x85] =
            //this[0x86] =
            this[0x87] = CacheBlobStatusPacketReader
            this[0x88] = CacheBlobsPacketReader
            //this[0x89] =
            this[0x8A] = EmotePacketReader
            //this[0x8B] =
            //this[0x8C] =
            //this[0x8D] =
            //this[0x8E] =
            this[0x8F] = NetworkSettingsPacketReader
            this[0x90] = InputPacketReader
            this[0x91] = CreativeInventoryPacketReader
            this[0x92] = EnchantOptionsPacketReader
            this[0x93] = InventoryRequestPacketReader
            //this[0x94] = InventoryResponsePacketReader
            //this[0x95] =
            //this[0x96] =
            //this[0x97] =
            this[0x98] = EmotesPacketReader
            this[0x99] = PositionTrackingDbServerBroadcastPacketReader
            this[0x9A] = PositionTrackingDbClientRequestPacketReader
            //this[0x9B] =
            this[0x9C] = ViolationPacketReader
            this[0x9D] = VelocityPredictionPacketReader
            //this[0x9E] =
            //this[0x9F] =
            this[0xA0] = FogPacketReader
            this[0xA1] = InputCorrectPacketReader
            this[0xA2] = ItemComponentPacketReader
            this[0xA3] = FilterPacketReader
            //this[0xA4] =
            //this[0xA5] =
            //this[0xA6] =
            //this[0xA7] =
            //this[0xA8] =
            //this[0xA9] =
            //this[0xAA] =
            //this[0xAB] =
            this[0xAC] = BlockUpdateSyncedPacketReader
        }

        const val NAME = "ta-packet-decoder"
    }
}
