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

import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import com.valaphee.tesseract.util.Int2ObjectOpenHashBiMap

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
data class WorldEventPacket(
    val eventOrParticleType: Event,
    val position: Float3,
    val data: Int
) : Packet {
    enum class Event {
        Unknown,
        SoundClick,
        SoundClickFail,
        SoundLaunch,
        SoundDoorOpen,
        SoundFizz,
        SoundFuse,
        SoundPlayRecording,
        SoundGhastWarning,
        SoundGhastFireball,
        SoundBlazeFireball,
        SoundZombieDoorBump,
        SoundZombieDoorCrash,
        SoundZombieInfected,
        SoundZombieConverted,
        SoundEndermanTeleport,
        SoundAnvilBroken,
        SoundAnvilUsed,
        SoundAnvilLand,
        SoundInfinityArrowPickup,
        SoundTeleportEnderpearl,
        SoundItemframeItemAdd,
        SoundItemframeBreak,
        SoundItemframePlace,
        SoundItemframeItemRemove,
        SoundItemframeItemRotate,
        SoundCamera,
        SoundExperienceOrbPickup,
        SoundTotemUsed,
        SoundArmorStandBreak,
        SoundArmorStandHit,
        SoundArmorStandLand,
        SoundArmorStandPlace,
        SoundPointedDripstoneLand,
        SoundDyeUsed,
        SoundInkSaceUsed,
        ParticleShoot,
        ParticleDestroyBlock,
        ParticlePotionSplash,
        ParticleEyeOfEnderDeath,
        ParticleMobBlockSpawn,
        ParticleCropGrowth,
        ParticleSoundGuardianGhost,
        ParticleDeathSmoke,
        ParticleDenyBlock,
        ParticleGenericSpawn,
        ParticleDragonEgg,
        ParticleCropEaten,
        ParticleCrit,
        ParticleTeleport,
        ParticleCrackBlock,
        ParticleBubbles,
        ParticleEvaporate,
        ParticleDestroyArmorStand,
        ParticleBreakingEgg,
        ParticleDestroyEgg,
        ParticleEvaporateWater,
        ParticleDestroyBlockNoSound,
        ParticleKnockbackRoar,
        ParticleTeleportTrail,
        ParticlePointCloud,
        ParticleExplosion,
        ParticleBlockExplosion,
        ParticleVibrationSignal,
        ParticleDripstoneDrip,
        ParticleFizzEffect,
        ParticleWaxOn,
        ParticleWaxOff,
        ParticleScrape,
        ParticleElectricSpark,
        StartRaining,
        StartThunderstorm,
        StopRaining,
        StopThunderstorm,
        GlobalPause,
        SimTimeStep,
        SimTimeScale,
        ActivateBlock,
        CauldronExplode,
        CauldronDyeArmor,
        CauldronCleanArmor,
        CauldronFillPotion,
        CauldronTakePotion,
        CauldronFillWater,
        CauldronTakeWater,
        CauldronAddDye,
        CauldronCleanBanner,
        CauldronFlush,
        AgentSpawnEffect,
        CauldronFillLava,
        CauldronTakeLava,
        CauldronFillPowderSnow,
        CauldronTakePowderSnow,
        BlockStartBreak,
        BlockStopBreak,
        BlockUpdateBreak,
        SetData,
        AllPlayersSleeping,
        JumpPrevented,
        ParticleBubble,
        ParticleBubbleManual,
        ParticleCritical,
        ParticleBlockForceField,
        ParticleSmoke,
        ParticleExplode,
        ParticleEvaporation,
        ParticleFlame,
        ParticleCandleFlame,
        ParticleLava,
        ParticleLargeSmoke,
        ParticleRedstone,
        ParticleRisingRedDust,
        ParticleItemBreak,
        ParticleSnowballPoof,
        ParticleHugeExplode,
        ParticleHugeExplodeSeed,
        ParticleMobFlame,
        ParticleHeart,
        ParticleTerrain,
        ParticleTownAura,
        ParticlePortal,
        ParticleMobPortal,
        ParticleSplash,
        ParticleSplashManual,
        ParticleWaterWake,
        ParticleDripWater,
        ParticleDripLava,
        ParticleDripHoney,
        ParticleFallingDust,
        ParticleMobSpell,
        ParticleMobSpellAmbient,
        ParticleMobSpellInstantaneous,
        ParticleInk,
        ParticleSlime,
        ParticleRainSplash,
        ParticleVillagerAngry,
        ParticleVillagerHappy,
        ParticleEnchantmentTable,
        ParticleTrackingEmitter,
        ParticleNote,
        ParticleWitchSpell,
        ParticleCarrot,
        ParticleMobAppearance,
        ParticleEndRod,
        ParticleRisingDragonsBreath,
        ParticleSpit,
        ParticleTotem,
        ParticleFood,
        ParticleFireworksStarter,
        ParticleFireworksSpark,
        ParticleFireworksOverlay,
        ParticleBalloonGas,
        ParticleColoredFlame,
        ParticleSparkler,
        ParticleConduit,
        ParticleBubbleColumnUp,
        ParticleBubbleColumnDown,
        ParticleSneeze,
        ParticleShulkerBullet,
        ParticleBleach,
        ParticleDragonDestroyBlock,
        ParticleMyceliumDust,
        ParticleFallingRedDust,
        ParticleCampfireSmoke,
        ParticleTallCampfireSmoke,
        ParticleFallingDragonsBreath,
        ParticleDragonsBreath,
        ParticleBlueFlame,
        ParticleSoul,
        ParticleObsidianTear,
        ParticleStalactiteDripWater,
        ParticleStalactiteDripLava,
        ParticlePortalReverse,
        ParticleSnowflake,
        ParticleSculkSensorRedstone,
        ParticleSporeBlossomShower,
        ParticleSporeBlossomAmbient,
        ParticleWax
    }

    override val id get() = 0x19

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarInt((if (version >= 448) events else if (version >= 440) eventsPre448 else if (version >= 431) eventsPre440 else if (version >= 428) eventsPre431 else if (version >= 407) eventsPre428 else eventsPre407).getKey(eventOrParticleType))
        buffer.writeFloat3(position)
        buffer.writeVarInt(data)
    }

    override fun handle(handler: PacketHandler) = handler.worldEvent(this)

    companion object {
        private val eventsPre407 = Int2ObjectOpenHashBiMap<Event>().apply {
            this[0] = Event.Unknown
            this[1000] = Event.SoundClick
            this[1001] = Event.SoundClickFail
            this[1002] = Event.SoundLaunch
            this[1003] = Event.SoundDoorOpen
            this[1004] = Event.SoundFizz
            this[1005] = Event.SoundFuse
            this[1006] = Event.SoundPlayRecording
            this[1007] = Event.SoundGhastWarning
            this[1008] = Event.SoundGhastFireball
            this[1009] = Event.SoundBlazeFireball
            this[1010] = Event.SoundZombieDoorBump
            this[1012] = Event.SoundZombieDoorCrash
            this[1016] = Event.SoundZombieInfected
            this[1017] = Event.SoundZombieConverted
            this[1018] = Event.SoundEndermanTeleport
            this[1020] = Event.SoundAnvilBroken
            this[1021] = Event.SoundAnvilUsed
            this[1022] = Event.SoundAnvilLand
            this[1030] = Event.SoundInfinityArrowPickup
            this[1032] = Event.SoundTeleportEnderpearl
            this[1040] = Event.SoundItemframeItemAdd
            this[1041] = Event.SoundItemframeBreak
            this[1042] = Event.SoundItemframePlace
            this[1043] = Event.SoundItemframeItemRemove
            this[1044] = Event.SoundItemframeItemRotate
            this[1051] = Event.SoundExperienceOrbPickup
            this[1052] = Event.SoundTotemUsed
            this[1060] = Event.SoundArmorStandBreak
            this[1061] = Event.SoundArmorStandHit
            this[1062] = Event.SoundArmorStandLand
            this[1063] = Event.SoundArmorStandPlace
            this[2000] = Event.ParticleShoot
            this[2001] = Event.ParticleDestroyBlock
            this[2002] = Event.ParticlePotionSplash
            this[2003] = Event.ParticleEyeOfEnderDeath
            this[2004] = Event.ParticleMobBlockSpawn
            this[2005] = Event.ParticleCropGrowth
            this[2006] = Event.ParticleSoundGuardianGhost
            this[2007] = Event.ParticleDeathSmoke
            this[2008] = Event.ParticleDenyBlock
            this[2009] = Event.ParticleGenericSpawn
            this[2010] = Event.ParticleDragonEgg
            this[2011] = Event.ParticleCropEaten
            this[2012] = Event.ParticleCrit
            this[2013] = Event.ParticleTeleport
            this[2014] = Event.ParticleCrackBlock
            this[2015] = Event.ParticleBubbles
            this[2016] = Event.ParticleEvaporate
            this[2017] = Event.ParticleDestroyArmorStand
            this[2018] = Event.ParticleBreakingEgg
            this[2019] = Event.ParticleDestroyEgg
            this[2020] = Event.ParticleEvaporateWater
            this[2021] = Event.ParticleDestroyBlockNoSound
            this[2023] = Event.ParticleTeleportTrail
            this[2024] = Event.ParticlePointCloud
            this[2025] = Event.ParticleExplosion
            this[2026] = Event.ParticleBlockExplosion
            this[3001] = Event.StartRaining
            this[3002] = Event.StartThunderstorm
            this[3003] = Event.StopRaining
            this[3004] = Event.StopThunderstorm
            this[3005] = Event.GlobalPause
            this[3006] = Event.SimTimeStep
            this[3007] = Event.SimTimeScale
            this[3500] = Event.ActivateBlock
            this[3501] = Event.CauldronExplode
            this[3502] = Event.CauldronDyeArmor
            this[3503] = Event.CauldronCleanArmor
            this[3504] = Event.CauldronFillPotion
            this[3505] = Event.CauldronTakePotion
            this[3506] = Event.CauldronFillWater
            this[3507] = Event.CauldronTakeWater
            this[3508] = Event.CauldronAddDye
            this[3509] = Event.CauldronCleanBanner
            this[3510] = Event.CauldronFlush
            this[3511] = Event.AgentSpawnEffect
            this[3512] = Event.CauldronFillLava
            this[3513] = Event.CauldronTakeLava
            this[0x4000 + 1] = Event.ParticleBubble
            this[0x4000 + 2] = Event.ParticleBubbleManual
            this[0x4000 + 3] = Event.ParticleCritical
            this[0x4000 + 4] = Event.ParticleBlockForceField
            this[0x4000 + 5] = Event.ParticleSmoke
            this[0x4000 + 6] = Event.ParticleExplode
            this[0x4000 + 7] = Event.ParticleEvaporation
            this[0x4000 + 8] = Event.ParticleFlame
            this[0x4000 + 9] = Event.ParticleLava
            this[0x4000 + 10] = Event.ParticleLargeSmoke
            this[0x4000 + 11] = Event.ParticleRedstone
            this[0x4000 + 12] = Event.ParticleRisingRedDust
            this[0x4000 + 13] = Event.ParticleItemBreak
            this[0x4000 + 14] = Event.ParticleSnowballPoof
            this[0x4000 + 15] = Event.ParticleHugeExplode
            this[0x4000 + 16] = Event.ParticleHugeExplodeSeed
            this[0x4000 + 17] = Event.ParticleMobFlame
            this[0x4000 + 18] = Event.ParticleHeart
            this[0x4000 + 19] = Event.ParticleTerrain
            this[0x4000 + 20] = Event.ParticleTownAura
            this[0x4000 + 21] = Event.ParticlePortal
            this[0x4000 + 22] = Event.ParticleMobPortal
            this[0x4000 + 23] = Event.ParticleSplash
            this[0x4000 + 24] = Event.ParticleSplashManual
            this[0x4000 + 25] = Event.ParticleWaterWake
            this[0x4000 + 26] = Event.ParticleDripWater
            this[0x4000 + 27] = Event.ParticleDripLava
            this[0x4000 + 28] = Event.ParticleDripHoney
            this[0x4000 + 29] = Event.ParticleFallingDust
            this[0x4000 + 30] = Event.ParticleMobSpell
            this[0x4000 + 31] = Event.ParticleMobSpellAmbient
            this[0x4000 + 32] = Event.ParticleMobSpellInstantaneous
            this[0x4000 + 33] = Event.ParticleInk
            this[0x4000 + 34] = Event.ParticleSlime
            this[0x4000 + 35] = Event.ParticleRainSplash
            this[0x4000 + 36] = Event.ParticleVillagerAngry
            this[0x4000 + 37] = Event.ParticleVillagerHappy
            this[0x4000 + 38] = Event.ParticleEnchantmentTable
            this[0x4000 + 39] = Event.ParticleTrackingEmitter
            this[0x4000 + 40] = Event.ParticleNote
            this[0x4000 + 41] = Event.ParticleWitchSpell
            this[0x4000 + 42] = Event.ParticleCarrot
            this[0x4000 + 43] = Event.ParticleMobAppearance
            this[0x4000 + 44] = Event.ParticleEndRod
            this[0x4000 + 45] = Event.ParticleDragonsBreath
            this[0x4000 + 46] = Event.ParticleSpit
            this[0x4000 + 47] = Event.ParticleTotem
            this[0x4000 + 48] = Event.ParticleFood
            this[0x4000 + 49] = Event.ParticleFireworksStarter
            this[0x4000 + 50] = Event.ParticleFireworksSpark
            this[0x4000 + 51] = Event.ParticleFireworksOverlay
            this[0x4000 + 52] = Event.ParticleBalloonGas
            this[0x4000 + 53] = Event.ParticleColoredFlame
            this[0x4000 + 54] = Event.ParticleSparkler
            this[0x4000 + 55] = Event.ParticleConduit
            this[0x4000 + 56] = Event.ParticleBubbleColumnUp
            this[0x4000 + 57] = Event.ParticleBubbleColumnDown
            this[0x4000 + 58] = Event.ParticleSneeze
            this[0x4000 + 59] = Event.ParticleShulkerBullet
            this[0x4000 + 60] = Event.ParticleBleach
            this[0x4000 + 61] = Event.ParticleDragonDestroyBlock
            this[0x4000 + 62] = Event.ParticleMyceliumDust
            this[0x4000 + 63] = Event.ParticleFallingRedDust
            this[0x4000 + 64] = Event.ParticleCampfireSmoke
            this[0x4000 + 65] = Event.ParticleTallCampfireSmoke
            this[0x4000 + 66] = Event.ParticleRisingDragonsBreath
            this[0x4000 + 67] = Event.ParticleDragonsBreath
        }
        private val eventsPre428 = eventsPre407.clone().apply {
            this[1050] = Event.SoundCamera
            this[3600] = Event.BlockStartBreak
            this[3601] = Event.BlockStopBreak
            this[3602] = Event.BlockUpdateBreak
            this[4000] = Event.SetData
            this[9800] = Event.AllPlayersSleeping
            this[0x4000 + 68] = Event.ParticleBlueFlame
            this[0x4000 + 69] = Event.ParticleSoul
            this[0x4000 + 70] = Event.ParticleObsidianTear
        }
        private val eventsPre431 = eventsPre428.clone().apply {
            this[2027] = Event.ParticleVibrationSignal
            this[3514] = Event.CauldronFillPowderSnow
            this[3515] = Event.CauldronTakePowderSnow
        }
        private val eventsPre440 = eventsPre431.clone().apply {
            this[1064] = Event.SoundPointedDripstoneLand
            this[1065] = Event.SoundDyeUsed
            this[1066] = Event.SoundInkSaceUsed
            this[2028] = Event.ParticleDripstoneDrip
            this[2029] = Event.ParticleFizzEffect
            this[2030] = Event.ParticleWaxOn
            this[2031] = Event.ParticleWaxOff
            this[2032] = Event.ParticleScrape
            this[2033] = Event.ParticleElectricSpark
            this[0x4000 + 29] = Event.ParticleStalactiteDripWater
            this[0x4000 + 30] = Event.ParticleStalactiteDripLava
            this[0x4000 + 31] = Event.ParticleFallingDust
            this[0x4000 + 32] = Event.ParticleMobSpell
            this[0x4000 + 33] = Event.ParticleMobSpellAmbient
            this[0x4000 + 34] = Event.ParticleMobSpellInstantaneous
            this[0x4000 + 35] = Event.ParticleInk
            this[0x4000 + 36] = Event.ParticleSlime
            this[0x4000 + 37] = Event.ParticleRainSplash
            this[0x4000 + 38] = Event.ParticleVillagerAngry
            this[0x4000 + 39] = Event.ParticleVillagerHappy
            this[0x4000 + 40] = Event.ParticleEnchantmentTable
            this[0x4000 + 41] = Event.ParticleTrackingEmitter
            this[0x4000 + 42] = Event.ParticleNote
            this[0x4000 + 43] = Event.ParticleWitchSpell
            this[0x4000 + 44] = Event.ParticleCarrot
            this[0x4000 + 45] = Event.ParticleMobAppearance
            this[0x4000 + 46] = Event.ParticleEndRod
            this[0x4000 + 47] = Event.ParticleDragonsBreath
            this[0x4000 + 48] = Event.ParticleSpit
            this[0x4000 + 49] = Event.ParticleTotem
            this[0x4000 + 50] = Event.ParticleFood
            this[0x4000 + 51] = Event.ParticleFireworksStarter
            this[0x4000 + 52] = Event.ParticleFireworksSpark
            this[0x4000 + 53] = Event.ParticleFireworksOverlay
            this[0x4000 + 54] = Event.ParticleBalloonGas
            this[0x4000 + 55] = Event.ParticleColoredFlame
            this[0x4000 + 56] = Event.ParticleSparkler
            this[0x4000 + 57] = Event.ParticleConduit
            this[0x4000 + 58] = Event.ParticleBubbleColumnUp
            this[0x4000 + 59] = Event.ParticleBubbleColumnDown
            this[0x4000 + 60] = Event.ParticleSneeze
            this[0x4000 + 61] = Event.ParticleShulkerBullet
            this[0x4000 + 62] = Event.ParticleBleach
            this[0x4000 + 63] = Event.ParticleDragonDestroyBlock
            this[0x4000 + 64] = Event.ParticleMyceliumDust
            this[0x4000 + 65] = Event.ParticleFallingRedDust
            this[0x4000 + 66] = Event.ParticleCampfireSmoke
            this[0x4000 + 67] = Event.ParticleTallCampfireSmoke
            this[0x4000 + 68] = Event.ParticleRisingDragonsBreath
            this[0x4000 + 69] = Event.ParticleDragonsBreath
            this[0x4000 + 70] = Event.ParticleBlueFlame
            this[0x4000 + 71] = Event.ParticleSoul
            this[0x4000 + 72] = Event.ParticleObsidianTear
        }
        private val eventsPre448 = eventsPre440.clone().apply {
            this[0x4000 + 73] = Event.ParticlePortalReverse
            this[0x4000 + 74] = Event.ParticleSnowflake
            this[0x4000 + 75] = Event.ParticleVibrationSignal
            this[0x4000 + 76] = Event.ParticleSculkSensorRedstone
            this[0x4000 + 77] = Event.ParticleSporeBlossomShower
            this[0x4000 + 78] = Event.ParticleSporeBlossomAmbient
            this[0x4000 + 79] = Event.ParticleWax
            this[0x4000 + 80] = Event.ParticleElectricSpark
        }
        private val events = eventsPre440.clone().apply {
            this[0x4000 + 9] = Event.ParticleCandleFlame
            this[0x4000 + 10] = Event.ParticleLava
            this[0x4000 + 11] = Event.ParticleLargeSmoke
            this[0x4000 + 12] = Event.ParticleRedstone
            this[0x4000 + 13] = Event.ParticleRisingRedDust
            this[0x4000 + 14] = Event.ParticleItemBreak
            this[0x4000 + 15] = Event.ParticleSnowballPoof
            this[0x4000 + 16] = Event.ParticleHugeExplode
            this[0x4000 + 17] = Event.ParticleHugeExplodeSeed
            this[0x4000 + 18] = Event.ParticleMobFlame
            this[0x4000 + 19] = Event.ParticleHeart
            this[0x4000 + 20] = Event.ParticleTerrain
            this[0x4000 + 21] = Event.ParticleTownAura
            this[0x4000 + 22] = Event.ParticlePortal
            this[0x4000 + 23] = Event.ParticleMobPortal
            this[0x4000 + 24] = Event.ParticleSplash
            this[0x4000 + 25] = Event.ParticleSplashManual
            this[0x4000 + 26] = Event.ParticleWaterWake
            this[0x4000 + 27] = Event.ParticleDripWater
            this[0x4000 + 28] = Event.ParticleDripLava
            this[0x4000 + 29] = Event.ParticleDripHoney
            this[0x4000 + 30] = Event.ParticleStalactiteDripWater
            this[0x4000 + 31] = Event.ParticleStalactiteDripLava
            this[0x4000 + 32] = Event.ParticleFallingDust
            this[0x4000 + 33] = Event.ParticleMobSpell
            this[0x4000 + 34] = Event.ParticleMobSpellAmbient
            this[0x4000 + 35] = Event.ParticleMobSpellInstantaneous
            this[0x4000 + 36] = Event.ParticleInk
            this[0x4000 + 37] = Event.ParticleSlime
            this[0x4000 + 38] = Event.ParticleRainSplash
            this[0x4000 + 39] = Event.ParticleVillagerAngry
            this[0x4000 + 40] = Event.ParticleVillagerHappy
            this[0x4000 + 41] = Event.ParticleEnchantmentTable
            this[0x4000 + 42] = Event.ParticleTrackingEmitter
            this[0x4000 + 43] = Event.ParticleNote
            this[0x4000 + 44] = Event.ParticleWitchSpell
            this[0x4000 + 45] = Event.ParticleCarrot
            this[0x4000 + 46] = Event.ParticleMobAppearance
            this[0x4000 + 47] = Event.ParticleEndRod
            this[0x4000 + 48] = Event.ParticleDragonsBreath
            this[0x4000 + 49] = Event.ParticleSpit
            this[0x4000 + 50] = Event.ParticleTotem
            this[0x4000 + 51] = Event.ParticleFood
            this[0x4000 + 52] = Event.ParticleFireworksStarter
            this[0x4000 + 53] = Event.ParticleFireworksSpark
            this[0x4000 + 54] = Event.ParticleFireworksOverlay
            this[0x4000 + 55] = Event.ParticleBalloonGas
            this[0x4000 + 56] = Event.ParticleColoredFlame
            this[0x4000 + 57] = Event.ParticleSparkler
            this[0x4000 + 58] = Event.ParticleConduit
            this[0x4000 + 59] = Event.ParticleBubbleColumnUp
            this[0x4000 + 60] = Event.ParticleBubbleColumnDown
            this[0x4000 + 61] = Event.ParticleSneeze
            this[0x4000 + 62] = Event.ParticleShulkerBullet
            this[0x4000 + 63] = Event.ParticleBleach
            this[0x4000 + 64] = Event.ParticleDragonDestroyBlock
            this[0x4000 + 65] = Event.ParticleMyceliumDust
            this[0x4000 + 66] = Event.ParticleFallingRedDust
            this[0x4000 + 67] = Event.ParticleCampfireSmoke
            this[0x4000 + 68] = Event.ParticleTallCampfireSmoke
            this[0x4000 + 69] = Event.ParticleRisingDragonsBreath
            this[0x4000 + 70] = Event.ParticleDragonsBreath
            this[0x4000 + 71] = Event.ParticleBlueFlame
            this[0x4000 + 72] = Event.ParticleSoul
            this[0x4000 + 73] = Event.ParticleObsidianTear
            this[0x4000 + 74] = Event.ParticlePortalReverse
            this[0x4000 + 75] = Event.ParticleSnowflake
            this[0x4000 + 76] = Event.ParticleVibrationSignal
            this[0x4000 + 77] = Event.ParticleSculkSensorRedstone
            this[0x4000 + 78] = Event.ParticleSporeBlossomShower
            this[0x4000 + 79] = Event.ParticleSporeBlossomAmbient
            this[0x4000 + 80] = Event.ParticleWax
            this[0x4000 + 81] = Event.ParticleElectricSpark
        }
    }
}
