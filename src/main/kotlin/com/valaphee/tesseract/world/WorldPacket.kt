/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world

import Difficulty
import Dimension
import Experiment
import GameMode
import GameRule
import Rank
import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.item.Item
import com.valaphee.tesseract.util.nbt.ListTag
import com.valaphee.tesseract.util.nbt.NbtOutputStream
import com.valaphee.tesseract.net.GamePublishMode
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import com.valaphee.tesseract.util.LittleEndianVarIntByteBufOutputStream
import com.valaphee.tesseract.world.chunk.terrain.block.Block
import writeExperiment
import writeGameRule
import writeGameRulePre440

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.Clientbound)
data class WorldPacket(
    var uniqueEntityId: Long,
    var runtimeEntityId: Long,
    var gameMode: GameMode,
    var position: Float3,
    var rotation: Float2,
    var seed: Int = 0,
    var biomeType: BiomeType,
    var biomeName: String,
    var dimension: Dimension,
    var generatorId: Int,
    var defaultGameMode: GameMode,
    var difficulty: Difficulty,
    var defaultSpawn: Int3,
    var achievementsDisabled: Boolean,
    var time: Int,
    var educationModeId: Int,
    var educationFeaturesEnabled: Boolean,
    var educationProductId: String,
    var educationEditionOffer: EducationEditionOffer,
    var rainLevel: Float,
    var thunderLevel: Float,
    var platformLockedContentConfirmed: Boolean,
    var multiplayerGame: Boolean,
    var broadcastingToLan: Boolean,
    var xboxLiveBroadcastMode: GamePublishMode,
    var platformBroadcastMode: GamePublishMode,
    var commandsEnabled: Boolean,
    var resourcePacksRequired: Boolean,
    var gameRules: Array<GameRule<*>>,
    var experiments: Array<Experiment>,
    var experimentsPreviouslyToggled: Boolean,
    var bonusChestEnabled: Boolean,
    var startingWithMap: Boolean,
    var defaultRank: Rank,
    var serverChunkTickRange: Int,
    var behaviorPackLocked: Boolean,
    var resourcePackLocked: Boolean,
    var fromLockedWorldTemplate: Boolean,
    var usingMsaGamerTagsOnly: Boolean,
    var fromWorldTemplate: Boolean,
    var worldTemplateOptionLocked: Boolean,
    var onlySpawningV1Villagers: Boolean,
    var version: String,
    var limitedWorldRadius: Int,
    var limitedWorldHeight: Int,
    var v2Nether: Boolean,
    var experimentalGameplay: Boolean,
    var worldId: String,
    var worldName: String,
    var premiumWorldTemplateId: String,
    var trial: Boolean,
    var movementAuthoritative: AuthoritativeMovement,
    var tick: Long,
    var enchantmentSeed: Int,
    private val blocksData: ByteArray?,
    var blocksTag: ListTag?,
    private val blocksComponentData: ByteArray?,
    var blocksComponent: Array<Block>?,
    private val itemsData: ByteArray?,
    var items: Array<Item<*>>?,
    var multiplayerCorrelationId: String,
    var inventoriesServerAuthoritative: Boolean,
    var movementRewindHistory: Int,
    var blockBreakingServerAuthoritative: Boolean,
    var engine: String
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
        buffer.writeFloat3(position)
        buffer.writeFloat2(rotation)
        buffer.writeVarInt(seed)
        if (version >= 407) {
            buffer.writeShortLE(biomeType.ordinal)
            buffer.writeString(biomeName)
        }
        buffer.writeVarInt(dimension.ordinal)
        buffer.writeVarInt(generatorId)
        buffer.writeVarInt(defaultGameMode.ordinal)
        buffer.writeVarInt(difficulty.ordinal)
        buffer.writeInt3UnsignedY(defaultSpawn)
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
        if (version >= 440) gameRules.forEach { buffer.writeGameRule(it) } else gameRules.forEach { buffer.writeGameRulePre440(it) }
        if (version >= 419) {
            buffer.writeIntLE(experiments.size)
            experiments.forEach { buffer.writeExperiment(it) }
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
            buffer.writeBoolean(v2Nether)
            buffer.writeBoolean(experimentalGameplay)
            if (version >= 419 && experimentalGameplay) buffer.writeBoolean(true)
        }
        buffer.writeString(worldId)
        buffer.writeString(worldName)
        buffer.writeString(premiumWorldTemplateId)
        buffer.writeBoolean(trial)
        if (version >= 419) buffer.writeVarUInt(movementAuthoritative.ordinal) else buffer.writeBoolean(movementAuthoritative == AuthoritativeMovement.Server)
        if (version >= 428) {
            buffer.writeVarInt(movementRewindHistory)
            buffer.writeBoolean(blockBreakingServerAuthoritative)
        }
        buffer.writeLongLE(tick)
        buffer.writeVarInt(enchantmentSeed)
        if (version >= 419) blocksComponentData?.let { buffer.writeBytes(it) } ?: run {
            blocksComponent!!.let {
                buffer.writeVarUInt(it.size)
                it.forEach {
                    buffer.writeString(it.key)
                    NbtOutputStream(LittleEndianVarIntByteBufOutputStream(buffer)).use { stream -> stream.writeTag(it.component) }
                }
            }
        } else blocksData?.let { buffer.writeBytes(it) } ?: NbtOutputStream(LittleEndianVarIntByteBufOutputStream(buffer)).use { it.writeTag(blocksTag) }
        itemsData?.let { buffer.writeBytes(it) } ?: run {
            items!!.let {
                buffer.writeVarUInt(it.size)
                it.forEach {
                    buffer.writeString(it.key)
                    buffer.writeShortLE(it.id)
                    if (version >= 419) buffer.writeBoolean(it.component != null)
                }
            }
        }
        buffer.writeString(multiplayerCorrelationId)
        if (version >= 407) buffer.writeBoolean(inventoriesServerAuthoritative)
        if (version >= 440) buffer.writeString(engine)
    }

    override fun handle(handler: PacketHandler) = handler.world(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WorldPacket

        if (uniqueEntityId != other.uniqueEntityId) return false
        if (runtimeEntityId != other.runtimeEntityId) return false
        if (gameMode != other.gameMode) return false
        if (position != other.position) return false
        if (rotation != other.rotation) return false
        if (seed != other.seed) return false
        if (biomeType != other.biomeType) return false
        if (biomeName != other.biomeName) return false
        if (dimension != other.dimension) return false
        if (generatorId != other.generatorId) return false
        if (defaultGameMode != other.defaultGameMode) return false
        if (difficulty != other.difficulty) return false
        if (defaultSpawn != other.defaultSpawn) return false
        if (achievementsDisabled != other.achievementsDisabled) return false
        if (time != other.time) return false
        if (educationModeId != other.educationModeId) return false
        if (educationFeaturesEnabled != other.educationFeaturesEnabled) return false
        if (educationProductId != other.educationProductId) return false
        if (educationEditionOffer != other.educationEditionOffer) return false
        if (rainLevel != other.rainLevel) return false
        if (thunderLevel != other.thunderLevel) return false
        if (platformLockedContentConfirmed != other.platformLockedContentConfirmed) return false
        if (multiplayerGame != other.multiplayerGame) return false
        if (broadcastingToLan != other.broadcastingToLan) return false
        if (xboxLiveBroadcastMode != other.xboxLiveBroadcastMode) return false
        if (platformBroadcastMode != other.platformBroadcastMode) return false
        if (commandsEnabled != other.commandsEnabled) return false
        if (resourcePacksRequired != other.resourcePacksRequired) return false
        if (!gameRules.contentEquals(other.gameRules)) return false
        if (!experiments.contentEquals(other.experiments)) return false
        if (experimentsPreviouslyToggled != other.experimentsPreviouslyToggled) return false
        if (bonusChestEnabled != other.bonusChestEnabled) return false
        if (startingWithMap != other.startingWithMap) return false
        if (defaultRank != other.defaultRank) return false
        if (serverChunkTickRange != other.serverChunkTickRange) return false
        if (behaviorPackLocked != other.behaviorPackLocked) return false
        if (resourcePackLocked != other.resourcePackLocked) return false
        if (fromLockedWorldTemplate != other.fromLockedWorldTemplate) return false
        if (usingMsaGamerTagsOnly != other.usingMsaGamerTagsOnly) return false
        if (fromWorldTemplate != other.fromWorldTemplate) return false
        if (worldTemplateOptionLocked != other.worldTemplateOptionLocked) return false
        if (onlySpawningV1Villagers != other.onlySpawningV1Villagers) return false
        if (version != other.version) return false
        if (limitedWorldRadius != other.limitedWorldRadius) return false
        if (limitedWorldHeight != other.limitedWorldHeight) return false
        if (v2Nether != other.v2Nether) return false
        if (experimentalGameplay != other.experimentalGameplay) return false
        if (worldId != other.worldId) return false
        if (worldName != other.worldName) return false
        if (premiumWorldTemplateId != other.premiumWorldTemplateId) return false
        if (trial != other.trial) return false
        if (movementAuthoritative != other.movementAuthoritative) return false
        if (tick != other.tick) return false
        if (enchantmentSeed != other.enchantmentSeed) return false
        if (blocksData != null) {
            if (other.blocksData == null) return false
            if (!blocksData.contentEquals(other.blocksData)) return false
        } else if (other.blocksData != null) return false
        if (blocksTag != other.blocksTag) return false
        if (blocksComponentData != null) {
            if (other.blocksComponentData == null) return false
            if (!blocksComponentData.contentEquals(other.blocksComponentData)) return false
        } else if (other.blocksComponentData != null) return false
        if (blocksComponent != null) {
            if (other.blocksComponent == null) return false
            if (!blocksComponent.contentEquals(other.blocksComponent)) return false
        } else if (other.blocksComponent != null) return false
        if (itemsData != null) {
            if (other.itemsData == null) return false
            if (!itemsData.contentEquals(other.itemsData)) return false
        } else if (other.itemsData != null) return false
        if (items != null) {
            if (other.items == null) return false
            if (!items.contentEquals(other.items)) return false
        } else if (other.items != null) return false
        if (multiplayerCorrelationId != other.multiplayerCorrelationId) return false
        if (inventoriesServerAuthoritative != other.inventoriesServerAuthoritative) return false
        if (movementRewindHistory != other.movementRewindHistory) return false
        if (blockBreakingServerAuthoritative != other.blockBreakingServerAuthoritative) return false
        if (engine != other.engine) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uniqueEntityId.hashCode()
        result = 31 * result + runtimeEntityId.hashCode()
        result = 31 * result + gameMode.hashCode()
        result = 31 * result + position.hashCode()
        result = 31 * result + rotation.hashCode()
        result = 31 * result + seed
        result = 31 * result + biomeType.hashCode()
        result = 31 * result + biomeName.hashCode()
        result = 31 * result + dimension.hashCode()
        result = 31 * result + generatorId
        result = 31 * result + defaultGameMode.hashCode()
        result = 31 * result + difficulty.hashCode()
        result = 31 * result + defaultSpawn.hashCode()
        result = 31 * result + achievementsDisabled.hashCode()
        result = 31 * result + time
        result = 31 * result + educationModeId
        result = 31 * result + educationFeaturesEnabled.hashCode()
        result = 31 * result + educationProductId.hashCode()
        result = 31 * result + educationEditionOffer.hashCode()
        result = 31 * result + rainLevel.hashCode()
        result = 31 * result + thunderLevel.hashCode()
        result = 31 * result + platformLockedContentConfirmed.hashCode()
        result = 31 * result + multiplayerGame.hashCode()
        result = 31 * result + broadcastingToLan.hashCode()
        result = 31 * result + xboxLiveBroadcastMode.hashCode()
        result = 31 * result + platformBroadcastMode.hashCode()
        result = 31 * result + commandsEnabled.hashCode()
        result = 31 * result + resourcePacksRequired.hashCode()
        result = 31 * result + gameRules.contentHashCode()
        result = 31 * result + experiments.contentHashCode()
        result = 31 * result + experimentsPreviouslyToggled.hashCode()
        result = 31 * result + bonusChestEnabled.hashCode()
        result = 31 * result + startingWithMap.hashCode()
        result = 31 * result + defaultRank.hashCode()
        result = 31 * result + serverChunkTickRange
        result = 31 * result + behaviorPackLocked.hashCode()
        result = 31 * result + resourcePackLocked.hashCode()
        result = 31 * result + fromLockedWorldTemplate.hashCode()
        result = 31 * result + usingMsaGamerTagsOnly.hashCode()
        result = 31 * result + fromWorldTemplate.hashCode()
        result = 31 * result + worldTemplateOptionLocked.hashCode()
        result = 31 * result + onlySpawningV1Villagers.hashCode()
        result = 31 * result + version.hashCode()
        result = 31 * result + limitedWorldRadius
        result = 31 * result + limitedWorldHeight
        result = 31 * result + v2Nether.hashCode()
        result = 31 * result + experimentalGameplay.hashCode()
        result = 31 * result + worldId.hashCode()
        result = 31 * result + worldName.hashCode()
        result = 31 * result + premiumWorldTemplateId.hashCode()
        result = 31 * result + trial.hashCode()
        result = 31 * result + movementAuthoritative.hashCode()
        result = 31 * result + tick.hashCode()
        result = 31 * result + enchantmentSeed
        result = 31 * result + (blocksData?.contentHashCode() ?: 0)
        result = 31 * result + (blocksTag?.hashCode() ?: 0)
        result = 31 * result + (blocksComponentData?.contentHashCode() ?: 0)
        result = 31 * result + (blocksComponent?.contentHashCode() ?: 0)
        result = 31 * result + (itemsData?.contentHashCode() ?: 0)
        result = 31 * result + (items?.contentHashCode() ?: 0)
        result = 31 * result + multiplayerCorrelationId.hashCode()
        result = 31 * result + inventoriesServerAuthoritative.hashCode()
        result = 31 * result + movementRewindHistory
        result = 31 * result + blockBreakingServerAuthoritative.hashCode()
        result = 31 * result + engine.hashCode()
        return result
    }

    override fun toString() = "WorldPacket(uniqueEntityId=$uniqueEntityId, runtimeEntityId=$runtimeEntityId, gameMode=$gameMode, position=$position, rotation=$rotation, seed=$seed, biomeType=$biomeType, biomeName='$biomeName', dimension=$dimension, generatorId=$generatorId, defaultGameMode=$defaultGameMode, difficulty=$difficulty, defaultSpawn=$defaultSpawn, achievementsDisabled=$achievementsDisabled, time=$time, educationModeId=$educationModeId, educationFeaturesEnabled=$educationFeaturesEnabled, educationProductId='$educationProductId', educationEditionOffer=$educationEditionOffer, rainLevel=$rainLevel, thunderLevel=$thunderLevel, platformLockedContentConfirmed=$platformLockedContentConfirmed, multiplayerGame=$multiplayerGame, broadcastingToLan=$broadcastingToLan, xboxLiveBroadcastMode=$xboxLiveBroadcastMode, platformBroadcastMode=$platformBroadcastMode, commandsEnabled=$commandsEnabled, resourcePacksRequired=$resourcePacksRequired, gameRules=${gameRules.contentToString()}, experiments=${experiments.contentToString()}, experimentsPreviouslyToggled=$experimentsPreviouslyToggled, bonusChestEnabled=$bonusChestEnabled, startingWithMap=$startingWithMap, defaultRank=$defaultRank, serverChunkTickRange=$serverChunkTickRange, behaviorPackLocked=$behaviorPackLocked, resourcePackLocked=$resourcePackLocked, fromLockedWorldTemplate=$fromLockedWorldTemplate, usingMsaGamerTagsOnly=$usingMsaGamerTagsOnly, fromWorldTemplate=$fromWorldTemplate, worldTemplateOptionLocked=$worldTemplateOptionLocked, onlySpawningV1Villagers=$onlySpawningV1Villagers, version='$version', limitedWorldRadius=$limitedWorldRadius, limitedWorldHeight=$limitedWorldHeight, v2Nether=$v2Nether, experimentalGameplay=$experimentalGameplay, worldId='$worldId', worldName='$worldName', premiumWorldTemplateId='$premiumWorldTemplateId', trial=$trial, movementAuthoritative=$movementAuthoritative, tick=$tick, enchantmentSeed=$enchantmentSeed, blocksTag=$blocksTag, blocksComponent=${blocksComponent?.contentToString()}, items=${items?.contentToString()}, multiplayerCorrelationId='$multiplayerCorrelationId', inventoriesServerAuthoritative=$inventoriesServerAuthoritative, movementRewindHistory=$movementRewindHistory, blockBreakingServerAuthoritative=$blockBreakingServerAuthoritative, engine='$engine')"
}
