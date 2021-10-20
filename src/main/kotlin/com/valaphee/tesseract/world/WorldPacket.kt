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

import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.data.block.Block
import com.valaphee.tesseract.data.item.Item
import com.valaphee.tesseract.entity.player.Rank
import com.valaphee.tesseract.net.GamePublishMode
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import com.valaphee.tesseract.util.nbt.ListTag
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
class WorldPacket(
    val uniqueEntityId: Long,
    val runtimeEntityId: Long,
    val gameMode: GameMode,
    var position: Float3?, // needed for je-be protocol translation
    var rotation: Float2?, // needed for je-be protocol translation
    val seed: Int,
    val biomeType: BiomeType,
    val biomeName: String,
    val dimension: Dimension,
    val generatorId: Int,
    val defaultGameMode: GameMode,
    val difficulty: Difficulty,
    var defaultSpawn: Int3?, // needed for je-be protocol translation
    val achievementsDisabled: Boolean,
    val time: Int,
    val educationEditionOffer: EducationEditionOffer,
    val educationModeId: Int,
    val educationFeaturesEnabled: Boolean,
    val educationProductId: String,
    val rainLevel: Float,
    val thunderLevel: Float,
    val platformLockedContentConfirmed: Boolean,
    val multiplayerGame: Boolean,
    val broadcastingToLan: Boolean,
    val xboxLiveBroadcastMode: GamePublishMode,
    val platformBroadcastMode: GamePublishMode,
    val commandsEnabled: Boolean,
    val resourcePacksRequired: Boolean,
    val gameRules: Array<GameRule<*>>,
    val experiments: Array<Experiment>,
    val experimentsPreviouslyToggled: Boolean,
    val bonusChestEnabled: Boolean,
    val startingWithMap: Boolean,
    val defaultRank: Rank,
    val serverChunkTickRange: Int,
    val behaviorPackLocked: Boolean,
    val resourcePackLocked: Boolean,
    val fromLockedWorldTemplate: Boolean,
    val usingMsaGamerTagsOnly: Boolean,
    val fromWorldTemplate: Boolean,
    val worldTemplateOptionLocked: Boolean,
    val onlySpawningV1Villagers: Boolean,
    val version: String,
    val limitedWorldRadius: Int,
    val limitedWorldHeight: Int,
    val v2Nether: Boolean,
    val experimentalGameplay: Boolean,
    val worldId: String,
    val worldName: String,
    val premiumWorldTemplateId: String,
    val trial: Boolean,
    val movementAuthoritative: AuthoritativeMovement,
    val movementRewindHistory: Int,
    val blockBreakingServerAuthoritative: Boolean,
    val tick: Long,
    val enchantmentSeed: Int,
    private val blocksData: ByteArray?,
    val blocksTag: ListTag?,
    private val blocks2Data: ByteArray?,
    val blocks2: Array<Block>?,
    private val itemsData: ByteArray?,
    val items: Int2ObjectMap<Item>?,
    val multiplayerCorrelationId: String,
    val inventoriesServerAuthoritative: Boolean,
    val engineVersion: String
) : Packet {
    enum class BiomeType {
        Default, UserDefined
    }

    companion object GeneratorId {
        const val FiniteOverworld = 0
        const val Overworld = 1
        const val Superflat = 2
        const val TheNether = 3
        const val TheEnd = 4
    }

    enum class EducationEditionOffer {
        None, EverywhereExceptChina, China
    }

    enum class AuthoritativeMovement {
        Client, Server, ServerWithRewind
    }

    override val id get() = 0x0B

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarLong(uniqueEntityId)
        buffer.writeVarULong(runtimeEntityId)
        buffer.writeVarInt(gameMode.ordinal)
        buffer.writeFloat3(position!!)
        buffer.writeFloat2(rotation!!)
        buffer.writeVarInt(seed)
        if (version >= 407) {
            buffer.writeShortLE(biomeType.ordinal)
            buffer.writeString(biomeName)
        }
        buffer.writeVarInt(dimension.ordinal)
        buffer.writeVarInt(generatorId)
        buffer.writeVarInt(defaultGameMode.ordinal)
        buffer.writeVarInt(difficulty.ordinal)
        buffer.writeInt3UnsignedY(defaultSpawn!!)
        buffer.writeBoolean(achievementsDisabled)
        buffer.writeVarInt(time)
        if (version >= 407) {
            buffer.writeVarInt(educationEditionOffer.ordinal)
            if (version < 419) buffer.writeByte(educationModeId)
            buffer.writeBoolean(educationFeaturesEnabled)
            buffer.writeString(educationProductId)
        } else {
            buffer.writeBoolean(educationFeaturesEnabled)
            buffer.writeVarInt(educationEditionOffer.ordinal)
        }
        buffer.writeFloatLE(rainLevel)
        buffer.writeFloatLE(thunderLevel)
        buffer.writeBoolean(platformLockedContentConfirmed)
        buffer.writeBoolean(multiplayerGame)
        buffer.writeBoolean(broadcastingToLan)
        buffer.writeVarInt(xboxLiveBroadcastMode.ordinal)
        buffer.writeVarInt(platformBroadcastMode.ordinal)
        buffer.writeBoolean(commandsEnabled)
        buffer.writeBoolean(resourcePacksRequired)
        buffer.writeVarUInt(gameRules.size)
        if (version >= 440) gameRules.forEach(buffer::writeGameRule) else gameRules.forEach(buffer::writeGameRulePre440)
        if (version >= 419) {
            experiments.let {
                buffer.writeIntLE(it.size)
                it.forEach(buffer::writeExperiment)
            }
            buffer.writeBoolean(experimentsPreviouslyToggled)
        }
        buffer.writeBoolean(bonusChestEnabled)
        buffer.writeBoolean(startingWithMap)
        buffer.writeVarInt(defaultRank.ordinal)
        buffer.writeIntLE(serverChunkTickRange)
        buffer.writeBoolean(behaviorPackLocked)
        buffer.writeBoolean(resourcePackLocked)
        buffer.writeBoolean(fromLockedWorldTemplate)
        buffer.writeBoolean(usingMsaGamerTagsOnly)
        buffer.writeBoolean(fromWorldTemplate)
        buffer.writeBoolean(worldTemplateOptionLocked)
        buffer.writeBoolean(onlySpawningV1Villagers)
        buffer.writeString(this.version)
        if (version >= 407) {
            buffer.writeIntLE(limitedWorldRadius)
            buffer.writeIntLE(limitedWorldHeight)
            if (version >= 465) {
                buffer.writeString("")
                buffer.writeString("")
            }
            buffer.writeBoolean(v2Nether)
            buffer.writeBoolean(experimentalGameplay)
            if (version >= 419 && experimentalGameplay) buffer.writeBoolean(true)
        }
        buffer.writeString(worldId)
        buffer.writeString(worldName)
        buffer.writeString(premiumWorldTemplateId)
        buffer.writeBoolean(trial)
        if (version >= 419) buffer.writeVarInt(movementAuthoritative.ordinal) else buffer.writeBoolean(movementAuthoritative == AuthoritativeMovement.Server)
        if (version >= 428) {
            buffer.writeVarInt(movementRewindHistory)
            buffer.writeBoolean(blockBreakingServerAuthoritative)
        }
        buffer.writeLongLE(tick)
        buffer.writeVarInt(enchantmentSeed)
        if (version >= 419) blocks2Data?.let { buffer.writeBytes(it) } ?: run {
            blocks2!!.let {
                buffer.writeVarUInt(it.size)
                it.forEach {
                    buffer.writeString(it.key)
                    buffer.toNbtOutputStream().use { stream -> stream.writeTag(null) }
                }
            }
        } else blocksData?.let { buffer.writeBytes(it) } ?: buffer.toNbtOutputStream().use { it.writeTag(blocksTag) }
        itemsData?.let { buffer.writeBytes(it) } ?: run {
            items!!.let {
                buffer.writeVarUInt(it.size)
                it.forEach { (id, item) ->
                    buffer.writeString(item.key)
                    buffer.writeShortLE(id)
                    if (version >= 419) buffer.writeBoolean(item.component != null)
                }
            }
        }
        buffer.writeString(multiplayerCorrelationId)
        if (version >= 407) buffer.writeBoolean(inventoriesServerAuthoritative)
        if (version >= 440) buffer.writeString(engineVersion)
    }

    override fun handle(handler: PacketHandler) = handler.world(this)

    override fun toString() = "WorldPacket(uniqueEntityId=$uniqueEntityId, runtimeEntityId=$runtimeEntityId, gameMode=$gameMode, position=$position, rotation=$rotation, seed=$seed, biomeType=$biomeType, biomeName='$biomeName', dimension=$dimension, generatorId=$generatorId, defaultGameMode=$defaultGameMode, difficulty=$difficulty, defaultSpawn=$defaultSpawn, achievementsDisabled=$achievementsDisabled, time=$time, educationEditionOffer=$educationEditionOffer, educationModeId=$educationModeId, educationFeaturesEnabled=$educationFeaturesEnabled, educationProductId='$educationProductId', rainLevel=$rainLevel, thunderLevel=$thunderLevel, platformLockedContentConfirmed=$platformLockedContentConfirmed, multiplayerGame=$multiplayerGame, broadcastingToLan=$broadcastingToLan, xboxLiveBroadcastMode=$xboxLiveBroadcastMode, platformBroadcastMode=$platformBroadcastMode, commandsEnabled=$commandsEnabled, resourcePacksRequired=$resourcePacksRequired, gameRules=${gameRules.contentToString()}, experiments=${experiments.contentToString()}, experimentsPreviouslyToggled=$experimentsPreviouslyToggled, bonusChestEnabled=$bonusChestEnabled, startingWithMap=$startingWithMap, defaultRank=$defaultRank, serverChunkTickRange=$serverChunkTickRange, behaviorPackLocked=$behaviorPackLocked, resourcePackLocked=$resourcePackLocked, fromLockedWorldTemplate=$fromLockedWorldTemplate, usingMsaGamerTagsOnly=$usingMsaGamerTagsOnly, fromWorldTemplate=$fromWorldTemplate, worldTemplateOptionLocked=$worldTemplateOptionLocked, onlySpawningV1Villagers=$onlySpawningV1Villagers, version='$version', limitedWorldRadius=$limitedWorldRadius, limitedWorldHeight=$limitedWorldHeight, v2Nether=$v2Nether, experimentalGameplay=$experimentalGameplay, worldId='$worldId', worldName='$worldName', premiumWorldTemplateId='$premiumWorldTemplateId', trial=$trial, movementAuthoritative=$movementAuthoritative, movementRewindHistory=$movementRewindHistory, blockBreakingServerAuthoritative=$blockBreakingServerAuthoritative, tick=$tick, enchantmentSeed=$enchantmentSeed, blocksData=${blocksData?.contentToString()}, blocksTag=$blocksTag, blocks2Data=${blocks2Data?.contentToString()}, blocks2=${blocks2?.contentToString()}, itemsData=${itemsData?.contentToString()}, items=$items, multiplayerCorrelationId='$multiplayerCorrelationId', inventoriesServerAuthoritative=$inventoriesServerAuthoritative, engine='$engineVersion')"
}

/**
 * @author Kevin Ludwig
 */
object WorldPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): WorldPacket {
        val uniqueEntityId = buffer.readVarLong()
        val runtimeEntityId = buffer.readVarULong()
        val gameMode = GameMode.values()[buffer.readVarInt()]
        val position = buffer.readFloat3()
        val rotation = buffer.readFloat2()
        val seed = buffer.readVarInt()
        val biomeType: WorldPacket.BiomeType
        val biomeName: String
        if (version >= 407) {
            biomeType = WorldPacket.BiomeType.values()[buffer.readShortLE().toInt()]
            biomeName = buffer.readString()
        } else {
            biomeType = WorldPacket.BiomeType.Default
            biomeName = ""
        }
        val dimension = Dimension.values()[buffer.readVarInt()]
        val generatorId = buffer.readVarInt()
        val defaultGameMode = GameMode.values()[buffer.readVarInt()]
        val difficulty = Difficulty.values()[buffer.readVarInt()]
        val defaultSpawn = buffer.readInt3UnsignedY()
        val achievementsDisabled = buffer.readBoolean()
        val time = buffer.readVarInt()
        val educationEditionOffer: WorldPacket.EducationEditionOffer
        val educationModeId: Int
        val educationFeaturesEnabled: Boolean
        val educationProductId: String?
        if (version >= 407) {
            educationEditionOffer = WorldPacket.EducationEditionOffer.values()[buffer.readVarInt()]
            educationModeId = if (version < 419) buffer.readByte().toInt() else 0
            educationFeaturesEnabled = buffer.readBoolean()
            educationProductId = buffer.readString()
        } else {
            educationFeaturesEnabled = buffer.readBoolean()
            educationModeId = 0
            educationEditionOffer = WorldPacket.EducationEditionOffer.values()[buffer.readVarInt()]
            educationProductId = ""
        }
        val rainLevel = buffer.readFloatLE()
        val thunderLevel = buffer.readFloatLE()
        val platformLockedContentConfirmed = buffer.readBoolean()
        val multiplayerGame = buffer.readBoolean()
        val broadcastingToLan = buffer.readBoolean()
        val xboxLiveBroadcastMode = GamePublishMode.values()[buffer.readVarInt()]
        val platformBroadcastMode = GamePublishMode.values()[buffer.readVarInt()]
        val commandsEnabled = buffer.readBoolean()
        val resourcePacksRequired = buffer.readBoolean()
        val gameRules = if (version >= 440) Array(buffer.readVarUInt()) { buffer.readGameRule() } else Array(buffer.readVarUInt()) { buffer.readGameRulePre440() }
        val experiments: Array<Experiment>
        val experimentsPreviouslyToggled: Boolean
        if (version >= 419) {
            experiments = Array(buffer.readIntLE()) { buffer.readExperiment() }
            experimentsPreviouslyToggled = buffer.readBoolean()
        } else {
            experiments = emptyArray()
            experimentsPreviouslyToggled = false
        }
        val bonusChestEnabled = buffer.readBoolean()
        val startingWithMap = buffer.readBoolean()
        val defaultPlayerPermission = Rank.values()[buffer.readVarInt()]
        val serverChunkTickRange = buffer.readIntLE()
        val behaviorPackLocked = buffer.readBoolean()
        val resourcePackLocked = buffer.readBoolean()
        val fromLockedWorldTemplate = buffer.readBoolean()
        val usingMsaGamerTagsOnly = buffer.readBoolean()
        val fromWorldTemplate = buffer.readBoolean()
        val worldTemplateOptionLocked = buffer.readBoolean()
        val onlySpawningV1Villagers = buffer.readBoolean()
        val version0 = buffer.readString()
        val limitedWorldRadius: Int
        val limitedWorldHeight: Int
        val v2Nether: Boolean
        val experimentalGameplay: Boolean
        if (version >= 407) {
            limitedWorldRadius = buffer.readIntLE()
            limitedWorldHeight = buffer.readIntLE()
            v2Nether = buffer.readBoolean()
            if (version >= 465) {
                buffer.readString()
                buffer.readString()
            }
            experimentalGameplay = if (version >= 419) {
                if (buffer.readBoolean()) buffer.readBoolean() else false
            } else buffer.readBoolean()
        } else {
            limitedWorldRadius = 0
            limitedWorldHeight = 0
            v2Nether = false
            experimentalGameplay = false
        }
        val levelId = buffer.readString()
        val worldName = buffer.readString()
        val premiumWorldTemplateId = buffer.readString()
        val trial = buffer.readBoolean()
        val movementAuthoritative = when {
            version >= 419 -> WorldPacket.AuthoritativeMovement.values()[buffer.readVarInt()]
            buffer.readBoolean() -> WorldPacket.AuthoritativeMovement.Server
            else -> WorldPacket.AuthoritativeMovement.Client
        }
        val movementRewindHistory: Int
        val blockBreakingServerAuthoritative: Boolean
        if (version >= 428) {
            movementRewindHistory = buffer.readVarInt()
            blockBreakingServerAuthoritative = buffer.readBoolean()
        } else {
            movementRewindHistory = 0
            blockBreakingServerAuthoritative = false
        }
        val tick = buffer.readLongLE()
        val enchantmentSeed = buffer.readVarInt()
        val blocks: ListTag?
        val block2: Array<Block>?
        if (version >= 419) {
            blocks = null
            block2 = buffer.toNbtInputStream().use { stream -> Array(buffer.readVarUInt()) { Block(buffer.readString()).also { stream.readTag()?.asCompoundTag() } } }
        } else {
            blocks = buffer.toNbtInputStream().use { it.readTag()!!.asListTag()!! }
            block2 = null
        }
        val itemCount = buffer.readVarUInt()
        val items = Int2ObjectOpenHashMap<Item>(itemCount).apply {
            repeat(itemCount) {
                val key = buffer.readString()
                val id = buffer.readShortLE()
                if (version >= 419 && buffer.readBoolean()) Unit
                this[id.toInt()] = Item(key, null)
            }
        }
        val multiplayerCorrelationId = buffer.readString()
        val inventoriesServerAuthoritative = buffer.readBoolean()
        val engineVersion = if (version >= 440) buffer.readString() else ""
        return WorldPacket(
            uniqueEntityId,
            runtimeEntityId,
            gameMode,
            position,
            rotation,
            seed,
            biomeType,
            biomeName,
            dimension,
            generatorId,
            defaultGameMode,
            difficulty,
            defaultSpawn,
            achievementsDisabled,
            time,
            educationEditionOffer,
            educationModeId,
            educationFeaturesEnabled,
            educationProductId,
            rainLevel,
            thunderLevel,
            platformLockedContentConfirmed,
            multiplayerGame,
            broadcastingToLan,
            xboxLiveBroadcastMode,
            platformBroadcastMode,
            commandsEnabled,
            resourcePacksRequired,
            gameRules,
            experiments,
            experimentsPreviouslyToggled,
            bonusChestEnabled,
            startingWithMap,
            defaultPlayerPermission,
            serverChunkTickRange,
            behaviorPackLocked,
            resourcePackLocked,
            fromLockedWorldTemplate,
            usingMsaGamerTagsOnly,
            fromWorldTemplate,
            worldTemplateOptionLocked,
            onlySpawningV1Villagers,
            version0,
            limitedWorldRadius,
            limitedWorldHeight,
            v2Nether,
            experimentalGameplay,
            levelId,
            worldName,
            premiumWorldTemplateId,
            trial,
            movementAuthoritative,
            movementRewindHistory,
            blockBreakingServerAuthoritative,
            tick,
            enchantmentSeed,
            null,
            blocks,
            null,
            block2,
            null,
            items,
            multiplayerCorrelationId,
            inventoriesServerAuthoritative,
            engineVersion
        )
    }
}
