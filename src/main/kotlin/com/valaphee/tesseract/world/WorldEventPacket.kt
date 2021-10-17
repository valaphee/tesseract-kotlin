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
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import com.valaphee.tesseract.util.Registry

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
class WorldEventPacket(
    val eventOrParticleType: Event,
    val position: Float3,
    val data: Int = 0
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
        ParticleWax;

        companion object {
            val registryPre407 = Registry<Event>().apply {
                this[0] = Unknown
                this[1000] = SoundClick
                this[1001] = SoundClickFail
                this[1002] = SoundLaunch
                this[1003] = SoundDoorOpen
                this[1004] = SoundFizz
                this[1005] = SoundFuse
                this[1006] = SoundPlayRecording
                this[1007] = SoundGhastWarning
                this[1008] = SoundGhastFireball
                this[1009] = SoundBlazeFireball
                this[1010] = SoundZombieDoorBump
                this[1012] = SoundZombieDoorCrash
                this[1016] = SoundZombieInfected
                this[1017] = SoundZombieConverted
                this[1018] = SoundEndermanTeleport
                this[1020] = SoundAnvilBroken
                this[1021] = SoundAnvilUsed
                this[1022] = SoundAnvilLand
                this[1030] = SoundInfinityArrowPickup
                this[1032] = SoundTeleportEnderpearl
                this[1040] = SoundItemframeItemAdd
                this[1041] = SoundItemframeBreak
                this[1042] = SoundItemframePlace
                this[1043] = SoundItemframeItemRemove
                this[1044] = SoundItemframeItemRotate
                this[1051] = SoundExperienceOrbPickup
                this[1052] = SoundTotemUsed
                this[1060] = SoundArmorStandBreak
                this[1061] = SoundArmorStandHit
                this[1062] = SoundArmorStandLand
                this[1063] = SoundArmorStandPlace
                this[2000] = ParticleShoot
                this[2001] = ParticleDestroyBlock
                this[2002] = ParticlePotionSplash
                this[2003] = ParticleEyeOfEnderDeath
                this[2004] = ParticleMobBlockSpawn
                this[2005] = ParticleCropGrowth
                this[2006] = ParticleSoundGuardianGhost
                this[2007] = ParticleDeathSmoke
                this[2008] = ParticleDenyBlock
                this[2009] = ParticleGenericSpawn
                this[2010] = ParticleDragonEgg
                this[2011] = ParticleCropEaten
                this[2012] = ParticleCrit
                this[2013] = ParticleTeleport
                this[2014] = ParticleCrackBlock
                this[2015] = ParticleBubbles
                this[2016] = ParticleEvaporate
                this[2017] = ParticleDestroyArmorStand
                this[2018] = ParticleBreakingEgg
                this[2019] = ParticleDestroyEgg
                this[2020] = ParticleEvaporateWater
                this[2021] = ParticleDestroyBlockNoSound
                this[2023] = ParticleTeleportTrail
                this[2024] = ParticlePointCloud
                this[2025] = ParticleExplosion
                this[2026] = ParticleBlockExplosion
                this[3001] = StartRaining
                this[3002] = StartThunderstorm
                this[3003] = StopRaining
                this[3004] = StopThunderstorm
                this[3005] = GlobalPause
                this[3006] = SimTimeStep
                this[3007] = SimTimeScale
                this[3500] = ActivateBlock
                this[3501] = CauldronExplode
                this[3502] = CauldronDyeArmor
                this[3503] = CauldronCleanArmor
                this[3504] = CauldronFillPotion
                this[3505] = CauldronTakePotion
                this[3506] = CauldronFillWater
                this[3507] = CauldronTakeWater
                this[3508] = CauldronAddDye
                this[3509] = CauldronCleanBanner
                this[3510] = CauldronFlush
                this[3511] = AgentSpawnEffect
                this[3512] = CauldronFillLava
                this[3513] = CauldronTakeLava
                this[0x4000 + 1] = ParticleBubble
                this[0x4000 + 2] = ParticleBubbleManual
                this[0x4000 + 3] = ParticleCritical
                this[0x4000 + 4] = ParticleBlockForceField
                this[0x4000 + 5] = ParticleSmoke
                this[0x4000 + 6] = ParticleExplode
                this[0x4000 + 7] = ParticleEvaporation
                this[0x4000 + 8] = ParticleFlame
                this[0x4000 + 9] = ParticleLava
                this[0x4000 + 10] = ParticleLargeSmoke
                this[0x4000 + 11] = ParticleRedstone
                this[0x4000 + 12] = ParticleRisingRedDust
                this[0x4000 + 13] = ParticleItemBreak
                this[0x4000 + 14] = ParticleSnowballPoof
                this[0x4000 + 15] = ParticleHugeExplode
                this[0x4000 + 16] = ParticleHugeExplodeSeed
                this[0x4000 + 17] = ParticleMobFlame
                this[0x4000 + 18] = ParticleHeart
                this[0x4000 + 19] = ParticleTerrain
                this[0x4000 + 20] = ParticleTownAura
                this[0x4000 + 21] = ParticlePortal
                this[0x4000 + 22] = ParticleMobPortal
                this[0x4000 + 23] = ParticleSplash
                this[0x4000 + 24] = ParticleSplashManual
                this[0x4000 + 25] = ParticleWaterWake
                this[0x4000 + 26] = ParticleDripWater
                this[0x4000 + 27] = ParticleDripLava
                this[0x4000 + 28] = ParticleDripHoney
                this[0x4000 + 29] = ParticleFallingDust
                this[0x4000 + 30] = ParticleMobSpell
                this[0x4000 + 31] = ParticleMobSpellAmbient
                this[0x4000 + 32] = ParticleMobSpellInstantaneous
                this[0x4000 + 33] = ParticleInk
                this[0x4000 + 34] = ParticleSlime
                this[0x4000 + 35] = ParticleRainSplash
                this[0x4000 + 36] = ParticleVillagerAngry
                this[0x4000 + 37] = ParticleVillagerHappy
                this[0x4000 + 38] = ParticleEnchantmentTable
                this[0x4000 + 39] = ParticleTrackingEmitter
                this[0x4000 + 40] = ParticleNote
                this[0x4000 + 41] = ParticleWitchSpell
                this[0x4000 + 42] = ParticleCarrot
                this[0x4000 + 43] = ParticleMobAppearance
                this[0x4000 + 44] = ParticleEndRod
                this[0x4000 + 45] = ParticleDragonsBreath
                this[0x4000 + 46] = ParticleSpit
                this[0x4000 + 47] = ParticleTotem
                this[0x4000 + 48] = ParticleFood
                this[0x4000 + 49] = ParticleFireworksStarter
                this[0x4000 + 50] = ParticleFireworksSpark
                this[0x4000 + 51] = ParticleFireworksOverlay
                this[0x4000 + 52] = ParticleBalloonGas
                this[0x4000 + 53] = ParticleColoredFlame
                this[0x4000 + 54] = ParticleSparkler
                this[0x4000 + 55] = ParticleConduit
                this[0x4000 + 56] = ParticleBubbleColumnUp
                this[0x4000 + 57] = ParticleBubbleColumnDown
                this[0x4000 + 58] = ParticleSneeze
                this[0x4000 + 59] = ParticleShulkerBullet
                this[0x4000 + 60] = ParticleBleach
                this[0x4000 + 61] = ParticleDragonDestroyBlock
                this[0x4000 + 62] = ParticleMyceliumDust
                this[0x4000 + 63] = ParticleFallingRedDust
                this[0x4000 + 64] = ParticleCampfireSmoke
                this[0x4000 + 65] = ParticleTallCampfireSmoke
                this[0x4000 + 66] = ParticleRisingDragonsBreath
                this[0x4000 + 67] = ParticleDragonsBreath
            }
            val registryPre428 = registryPre407.clone().apply {
                this[1050] = SoundCamera
                this[3600] = BlockStartBreak
                this[3601] = BlockStopBreak
                this[3602] = BlockUpdateBreak
                this[4000] = SetData
                this[9800] = AllPlayersSleeping
                this[0x4000 + 68] = ParticleBlueFlame
                this[0x4000 + 69] = ParticleSoul
                this[0x4000 + 70] = ParticleObsidianTear
            }
            val registryPre431 = registryPre428.clone().apply {
                this[2027] = ParticleVibrationSignal
                this[3514] = CauldronFillPowderSnow
                this[3515] = CauldronTakePowderSnow
            }
            val registryPre440 = registryPre431.clone().apply {
                this[1064] = SoundPointedDripstoneLand
                this[1065] = SoundDyeUsed
                this[1066] = SoundInkSaceUsed
                this[2028] = ParticleDripstoneDrip
                this[2029] = ParticleFizzEffect
                this[2030] = ParticleWaxOn
                this[2031] = ParticleWaxOff
                this[2032] = ParticleScrape
                this[2033] = ParticleElectricSpark
                this[0x4000 + 29] = ParticleStalactiteDripWater
                this[0x4000 + 30] = ParticleStalactiteDripLava
                this[0x4000 + 31] = ParticleFallingDust
                this[0x4000 + 32] = ParticleMobSpell
                this[0x4000 + 33] = ParticleMobSpellAmbient
                this[0x4000 + 34] = ParticleMobSpellInstantaneous
                this[0x4000 + 35] = ParticleInk
                this[0x4000 + 36] = ParticleSlime
                this[0x4000 + 37] = ParticleRainSplash
                this[0x4000 + 38] = ParticleVillagerAngry
                this[0x4000 + 39] = ParticleVillagerHappy
                this[0x4000 + 40] = ParticleEnchantmentTable
                this[0x4000 + 41] = ParticleTrackingEmitter
                this[0x4000 + 42] = ParticleNote
                this[0x4000 + 43] = ParticleWitchSpell
                this[0x4000 + 44] = ParticleCarrot
                this[0x4000 + 45] = ParticleMobAppearance
                this[0x4000 + 46] = ParticleEndRod
                this[0x4000 + 47] = ParticleDragonsBreath
                this[0x4000 + 48] = ParticleSpit
                this[0x4000 + 49] = ParticleTotem
                this[0x4000 + 50] = ParticleFood
                this[0x4000 + 51] = ParticleFireworksStarter
                this[0x4000 + 52] = ParticleFireworksSpark
                this[0x4000 + 53] = ParticleFireworksOverlay
                this[0x4000 + 54] = ParticleBalloonGas
                this[0x4000 + 55] = ParticleColoredFlame
                this[0x4000 + 56] = ParticleSparkler
                this[0x4000 + 57] = ParticleConduit
                this[0x4000 + 58] = ParticleBubbleColumnUp
                this[0x4000 + 59] = ParticleBubbleColumnDown
                this[0x4000 + 60] = ParticleSneeze
                this[0x4000 + 61] = ParticleShulkerBullet
                this[0x4000 + 62] = ParticleBleach
                this[0x4000 + 63] = ParticleDragonDestroyBlock
                this[0x4000 + 64] = ParticleMyceliumDust
                this[0x4000 + 65] = ParticleFallingRedDust
                this[0x4000 + 66] = ParticleCampfireSmoke
                this[0x4000 + 67] = ParticleTallCampfireSmoke
                this[0x4000 + 68] = ParticleRisingDragonsBreath
                this[0x4000 + 69] = ParticleDragonsBreath
                this[0x4000 + 70] = ParticleBlueFlame
                this[0x4000 + 71] = ParticleSoul
                this[0x4000 + 72] = ParticleObsidianTear
            }
            val registryPre448 = registryPre440.clone().apply {
                this[0x4000 + 73] = ParticlePortalReverse
                this[0x4000 + 74] = ParticleSnowflake
                this[0x4000 + 75] = ParticleVibrationSignal
                this[0x4000 + 76] = ParticleSculkSensorRedstone
                this[0x4000 + 77] = ParticleSporeBlossomShower
                this[0x4000 + 78] = ParticleSporeBlossomAmbient
                this[0x4000 + 79] = ParticleWax
                this[0x4000 + 80] = ParticleElectricSpark
            }
            val registry = registryPre448.clone().apply {
                this[0x4000 + 9] = ParticleCandleFlame
                this[0x4000 + 10] = ParticleLava
                this[0x4000 + 11] = ParticleLargeSmoke
                this[0x4000 + 12] = ParticleRedstone
                this[0x4000 + 13] = ParticleRisingRedDust
                this[0x4000 + 14] = ParticleItemBreak
                this[0x4000 + 15] = ParticleSnowballPoof
                this[0x4000 + 16] = ParticleHugeExplode
                this[0x4000 + 17] = ParticleHugeExplodeSeed
                this[0x4000 + 18] = ParticleMobFlame
                this[0x4000 + 19] = ParticleHeart
                this[0x4000 + 20] = ParticleTerrain
                this[0x4000 + 21] = ParticleTownAura
                this[0x4000 + 22] = ParticlePortal
                this[0x4000 + 23] = ParticleMobPortal
                this[0x4000 + 24] = ParticleSplash
                this[0x4000 + 25] = ParticleSplashManual
                this[0x4000 + 26] = ParticleWaterWake
                this[0x4000 + 27] = ParticleDripWater
                this[0x4000 + 28] = ParticleDripLava
                this[0x4000 + 29] = ParticleDripHoney
                this[0x4000 + 30] = ParticleStalactiteDripWater
                this[0x4000 + 31] = ParticleStalactiteDripLava
                this[0x4000 + 32] = ParticleFallingDust
                this[0x4000 + 33] = ParticleMobSpell
                this[0x4000 + 34] = ParticleMobSpellAmbient
                this[0x4000 + 35] = ParticleMobSpellInstantaneous
                this[0x4000 + 36] = ParticleInk
                this[0x4000 + 37] = ParticleSlime
                this[0x4000 + 38] = ParticleRainSplash
                this[0x4000 + 39] = ParticleVillagerAngry
                this[0x4000 + 40] = ParticleVillagerHappy
                this[0x4000 + 41] = ParticleEnchantmentTable
                this[0x4000 + 42] = ParticleTrackingEmitter
                this[0x4000 + 43] = ParticleNote
                this[0x4000 + 44] = ParticleWitchSpell
                this[0x4000 + 45] = ParticleCarrot
                this[0x4000 + 46] = ParticleMobAppearance
                this[0x4000 + 47] = ParticleEndRod
                this[0x4000 + 48] = ParticleDragonsBreath
                this[0x4000 + 49] = ParticleSpit
                this[0x4000 + 50] = ParticleTotem
                this[0x4000 + 51] = ParticleFood
                this[0x4000 + 52] = ParticleFireworksStarter
                this[0x4000 + 53] = ParticleFireworksSpark
                this[0x4000 + 54] = ParticleFireworksOverlay
                this[0x4000 + 55] = ParticleBalloonGas
                this[0x4000 + 56] = ParticleColoredFlame
                this[0x4000 + 57] = ParticleSparkler
                this[0x4000 + 58] = ParticleConduit
                this[0x4000 + 59] = ParticleBubbleColumnUp
                this[0x4000 + 60] = ParticleBubbleColumnDown
                this[0x4000 + 61] = ParticleSneeze
                this[0x4000 + 62] = ParticleShulkerBullet
                this[0x4000 + 63] = ParticleBleach
                this[0x4000 + 64] = ParticleDragonDestroyBlock
                this[0x4000 + 65] = ParticleMyceliumDust
                this[0x4000 + 66] = ParticleFallingRedDust
                this[0x4000 + 67] = ParticleCampfireSmoke
                this[0x4000 + 68] = ParticleTallCampfireSmoke
                this[0x4000 + 69] = ParticleRisingDragonsBreath
                this[0x4000 + 70] = ParticleDragonsBreath
                this[0x4000 + 71] = ParticleBlueFlame
                this[0x4000 + 72] = ParticleSoul
                this[0x4000 + 73] = ParticleObsidianTear
                this[0x4000 + 74] = ParticlePortalReverse
                this[0x4000 + 75] = ParticleSnowflake
                this[0x4000 + 76] = ParticleVibrationSignal
                this[0x4000 + 77] = ParticleSculkSensorRedstone
                this[0x4000 + 78] = ParticleSporeBlossomShower
                this[0x4000 + 79] = ParticleSporeBlossomAmbient
                this[0x4000 + 80] = ParticleWax
                this[0x4000 + 81] = ParticleElectricSpark
            }
        }
    }

    override val id get() = 0x19

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarInt((if (version >= 448) Event.registry else if (version >= 440) Event.registryPre448 else if (version >= 431) Event.registryPre440 else if (version >= 428) Event.registryPre431 else if (version >= 407) Event.registryPre428 else Event.registryPre407).getId(eventOrParticleType))
        buffer.writeFloat3(position)
        buffer.writeVarInt(data)
    }

    override fun handle(handler: PacketHandler) = handler.worldEvent(this)

    override fun toString() = "WorldEventPacket(eventOrParticleType=$eventOrParticleType, position=$position, data=$data)"
}

/**
 * @author Kevin Ludwig
 */
object WorldEventPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = WorldEventPacket(
        (if (version >= 448) WorldEventPacket.Event.registry else if (version >= 440) WorldEventPacket.Event.registryPre448 else if (version >= 431) WorldEventPacket.Event.registryPre440 else if (version >= 428) WorldEventPacket.Event.registryPre431 else if (version >= 407) WorldEventPacket.Event.registryPre428 else WorldEventPacket.Event.registryPre407)[buffer.readVarInt()],
        buffer.readFloat3(),
        buffer.readVarInt()
    )
}
