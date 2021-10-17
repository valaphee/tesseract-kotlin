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
        Undefined,

        // sound
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

        // particle
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
        ParticleTurtleEgg,
        ParticleSculkShriek,
        ParticleSculkCatalystBloom,

        // world
        StartRaining,
        StartThunderstorm,
        StopRaining,
        StopThunderstorm,
        GlobalPause,
        SimTimeStep,
        SimTimeScale,

        // block
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

        // more particle
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
        ParticleWax,
        ParticleElectricSparkOnly,
        ParticleCandleFlameOnly,
        ParticleShriek,
        ParticleSculkSoul;

        companion object {
            private const val soundOffset = 1000
            private const val particleOffset = 2000
            private const val worldOffset = 3000
            private const val blockOffset = 3500
            private const val particleLegacyOffset = 0x4000
            val registryPre407 = Registry(Undefined).apply {
                this[soundOffset + 0] = SoundClick
                this[soundOffset + 1] = SoundClickFail
                this[soundOffset + 2] = SoundLaunch
                this[soundOffset + 3] = SoundDoorOpen
                this[soundOffset + 4] = SoundFizz
                this[soundOffset + 5] = SoundFuse
                this[soundOffset + 6] = SoundPlayRecording
                this[soundOffset + 7] = SoundGhastWarning
                this[soundOffset + 8] = SoundGhastFireball
                this[soundOffset + 9] = SoundBlazeFireball
                this[soundOffset + 10] = SoundZombieDoorBump
                this[soundOffset + 12] = SoundZombieDoorCrash
                this[soundOffset + 16] = SoundZombieInfected
                this[soundOffset + 17] = SoundZombieConverted
                this[soundOffset + 18] = SoundEndermanTeleport
                this[soundOffset + 20] = SoundAnvilBroken
                this[soundOffset + 21] = SoundAnvilUsed
                this[soundOffset + 22] = SoundAnvilLand
                this[soundOffset + 30] = SoundInfinityArrowPickup
                this[soundOffset + 32] = SoundTeleportEnderpearl
                this[soundOffset + 40] = SoundItemframeItemAdd
                this[soundOffset + 41] = SoundItemframeBreak
                this[soundOffset + 42] = SoundItemframePlace
                this[soundOffset + 43] = SoundItemframeItemRemove
                this[soundOffset + 44] = SoundItemframeItemRotate
                this[soundOffset + 51] = SoundExperienceOrbPickup
                this[soundOffset + 52] = SoundTotemUsed
                this[soundOffset + 60] = SoundArmorStandBreak
                this[soundOffset + 61] = SoundArmorStandHit
                this[soundOffset + 62] = SoundArmorStandLand
                this[soundOffset + 63] = SoundArmorStandPlace
                this[particleOffset + 0] = ParticleShoot
                this[particleOffset + 1] = ParticleDestroyBlock
                this[particleOffset + 2] = ParticlePotionSplash
                this[particleOffset + 3] = ParticleEyeOfEnderDeath
                this[particleOffset + 4] = ParticleMobBlockSpawn
                this[particleOffset + 5] = ParticleCropGrowth
                this[particleOffset + 6] = ParticleSoundGuardianGhost
                this[particleOffset + 7] = ParticleDeathSmoke
                this[particleOffset + 8] = ParticleDenyBlock
                this[particleOffset + 9] = ParticleGenericSpawn
                this[particleOffset + 10] = ParticleDragonEgg
                this[particleOffset + 11] = ParticleCropEaten
                this[particleOffset + 12] = ParticleCrit
                this[particleOffset + 13] = ParticleTeleport
                this[particleOffset + 14] = ParticleCrackBlock
                this[particleOffset + 15] = ParticleBubbles
                this[particleOffset + 16] = ParticleEvaporate
                this[particleOffset + 17] = ParticleDestroyArmorStand
                this[particleOffset + 18] = ParticleBreakingEgg
                this[particleOffset + 19] = ParticleDestroyEgg
                this[particleOffset + 20] = ParticleEvaporateWater
                this[particleOffset + 21] = ParticleDestroyBlockNoSound
                this[particleOffset + 23] = ParticleTeleportTrail
                this[particleOffset + 24] = ParticlePointCloud
                this[particleOffset + 25] = ParticleExplosion
                this[particleOffset + 26] = ParticleBlockExplosion
                this[worldOffset + 1] = StartRaining
                this[worldOffset + 2] = StartThunderstorm
                this[worldOffset + 3] = StopRaining
                this[worldOffset + 4] = StopThunderstorm
                this[worldOffset + 5] = GlobalPause
                this[worldOffset + 6] = SimTimeStep
                this[worldOffset + 7] = SimTimeScale
                this[blockOffset + 0] = ActivateBlock
                this[blockOffset + 1] = CauldronExplode
                this[blockOffset + 2] = CauldronDyeArmor
                this[blockOffset + 3] = CauldronCleanArmor
                this[blockOffset + 4] = CauldronFillPotion
                this[blockOffset + 5] = CauldronTakePotion
                this[blockOffset + 6] = CauldronFillWater
                this[blockOffset + 7] = CauldronTakeWater
                this[blockOffset + 8] = CauldronAddDye
                this[blockOffset + 9] = CauldronCleanBanner
                this[blockOffset + 10] = CauldronFlush
                this[blockOffset + 11] = AgentSpawnEffect
                this[blockOffset + 12] = CauldronFillLava
                this[blockOffset + 13] = CauldronTakeLava
                this[9810] = JumpPrevented
                this[particleLegacyOffset + 1] = ParticleBubble
                this[particleLegacyOffset + 2] = ParticleBubbleManual
                this[particleLegacyOffset + 3] = ParticleCritical
                this[particleLegacyOffset + 4] = ParticleBlockForceField
                this[particleLegacyOffset + 5] = ParticleSmoke
                this[particleLegacyOffset + 6] = ParticleExplode
                this[particleLegacyOffset + 7] = ParticleEvaporation
                this[particleLegacyOffset + 8] = ParticleFlame
                this[particleLegacyOffset + 9] = ParticleLava
                this[particleLegacyOffset + 10] = ParticleLargeSmoke
                this[particleLegacyOffset + 11] = ParticleRedstone
                this[particleLegacyOffset + 12] = ParticleRisingRedDust
                this[particleLegacyOffset + 13] = ParticleItemBreak
                this[particleLegacyOffset + 14] = ParticleSnowballPoof
                this[particleLegacyOffset + 15] = ParticleHugeExplode
                this[particleLegacyOffset + 16] = ParticleHugeExplodeSeed
                this[particleLegacyOffset + 17] = ParticleMobFlame
                this[particleLegacyOffset + 18] = ParticleHeart
                this[particleLegacyOffset + 19] = ParticleTerrain
                this[particleLegacyOffset + 20] = ParticleTownAura
                this[particleLegacyOffset + 21] = ParticlePortal
                this[particleLegacyOffset + 22] = ParticleMobPortal
                this[particleLegacyOffset + 23] = ParticleSplash
                this[particleLegacyOffset + 24] = ParticleSplashManual
                this[particleLegacyOffset + 25] = ParticleWaterWake
                this[particleLegacyOffset + 26] = ParticleDripWater
                this[particleLegacyOffset + 27] = ParticleDripLava
                this[particleLegacyOffset + 28] = ParticleDripHoney
                this[particleLegacyOffset + 29] = ParticleFallingDust
                this[particleLegacyOffset + 30] = ParticleMobSpell
                this[particleLegacyOffset + 31] = ParticleMobSpellAmbient
                this[particleLegacyOffset + 32] = ParticleMobSpellInstantaneous
                this[particleLegacyOffset + 33] = ParticleInk
                this[particleLegacyOffset + 34] = ParticleSlime
                this[particleLegacyOffset + 35] = ParticleRainSplash
                this[particleLegacyOffset + 36] = ParticleVillagerAngry
                this[particleLegacyOffset + 37] = ParticleVillagerHappy
                this[particleLegacyOffset + 38] = ParticleEnchantmentTable
                this[particleLegacyOffset + 39] = ParticleTrackingEmitter
                this[particleLegacyOffset + 40] = ParticleNote
                this[particleLegacyOffset + 41] = ParticleWitchSpell
                this[particleLegacyOffset + 42] = ParticleCarrot
                this[particleLegacyOffset + 43] = ParticleMobAppearance
                this[particleLegacyOffset + 44] = ParticleEndRod
                this[particleLegacyOffset + 45] = ParticleDragonsBreath
                this[particleLegacyOffset + 46] = ParticleSpit
                this[particleLegacyOffset + 47] = ParticleTotem
                this[particleLegacyOffset + 48] = ParticleFood
                this[particleLegacyOffset + 49] = ParticleFireworksStarter
                this[particleLegacyOffset + 50] = ParticleFireworksSpark
                this[particleLegacyOffset + 51] = ParticleFireworksOverlay
                this[particleLegacyOffset + 52] = ParticleBalloonGas
                this[particleLegacyOffset + 53] = ParticleColoredFlame
                this[particleLegacyOffset + 54] = ParticleSparkler
                this[particleLegacyOffset + 55] = ParticleConduit
                this[particleLegacyOffset + 56] = ParticleBubbleColumnUp
                this[particleLegacyOffset + 57] = ParticleBubbleColumnDown
                this[particleLegacyOffset + 58] = ParticleSneeze
                this[particleLegacyOffset + 59] = ParticleShulkerBullet
                this[particleLegacyOffset + 60] = ParticleBleach
                this[particleLegacyOffset + 61] = ParticleDragonDestroyBlock
                this[particleLegacyOffset + 62] = ParticleMyceliumDust
                this[particleLegacyOffset + 63] = ParticleFallingRedDust
                this[particleLegacyOffset + 64] = ParticleCampfireSmoke
                this[particleLegacyOffset + 65] = ParticleTallCampfireSmoke
                this[particleLegacyOffset + 66] = ParticleRisingDragonsBreath
                this[particleLegacyOffset + 67] = ParticleDragonsBreath
            }
            val registryPre428 = registryPre407.clone().apply {
                this[soundOffset + 50] = SoundCamera
                this[blockOffset + 100] = BlockStartBreak
                this[blockOffset + 101] = BlockStopBreak
                this[blockOffset + 102] = BlockUpdateBreak
                this[4000] = SetData
                this[9800] = AllPlayersSleeping
                this[particleLegacyOffset + 68] = ParticleBlueFlame
                this[particleLegacyOffset + 69] = ParticleSoul
                this[particleLegacyOffset + 70] = ParticleObsidianTear
            }
            val registryPre431 = registryPre428.clone().apply {
                this[particleOffset + 27] = ParticleVibrationSignal
                this[blockOffset + 14] = CauldronFillPowderSnow
                this[blockOffset + 15] = CauldronTakePowderSnow
            }
            val registryPre440 = registryPre431.clone().apply {
                this[soundOffset + 64] = SoundPointedDripstoneLand
                this[soundOffset + 65] = SoundDyeUsed
                this[soundOffset + 66] = SoundInkSaceUsed
                this[particleOffset + 28] = ParticleDripstoneDrip
                this[particleOffset + 29] = ParticleFizzEffect
                this[particleOffset + 30] = ParticleWaxOn
                this[particleOffset + 31] = ParticleWaxOff
                this[particleOffset + 32] = ParticleScrape
                this[particleOffset + 33] = ParticleElectricSpark
                this[particleLegacyOffset + 29] = ParticleStalactiteDripWater
                this[particleLegacyOffset + 30] = ParticleStalactiteDripLava
                this[particleLegacyOffset + 31] = ParticleFallingDust
                this[particleLegacyOffset + 32] = ParticleMobSpell
                this[particleLegacyOffset + 33] = ParticleMobSpellAmbient
                this[particleLegacyOffset + 34] = ParticleMobSpellInstantaneous
                this[particleLegacyOffset + 35] = ParticleInk
                this[particleLegacyOffset + 36] = ParticleSlime
                this[particleLegacyOffset + 37] = ParticleRainSplash
                this[particleLegacyOffset + 38] = ParticleVillagerAngry
                this[particleLegacyOffset + 39] = ParticleVillagerHappy
                this[particleLegacyOffset + 40] = ParticleEnchantmentTable
                this[particleLegacyOffset + 41] = ParticleTrackingEmitter
                this[particleLegacyOffset + 42] = ParticleNote
                this[particleLegacyOffset + 43] = ParticleWitchSpell
                this[particleLegacyOffset + 44] = ParticleCarrot
                this[particleLegacyOffset + 45] = ParticleMobAppearance
                this[particleLegacyOffset + 46] = ParticleEndRod
                this[particleLegacyOffset + 47] = ParticleDragonsBreath
                this[particleLegacyOffset + 48] = ParticleSpit
                this[particleLegacyOffset + 49] = ParticleTotem
                this[particleLegacyOffset + 50] = ParticleFood
                this[particleLegacyOffset + 51] = ParticleFireworksStarter
                this[particleLegacyOffset + 52] = ParticleFireworksSpark
                this[particleLegacyOffset + 53] = ParticleFireworksOverlay
                this[particleLegacyOffset + 54] = ParticleBalloonGas
                this[particleLegacyOffset + 55] = ParticleColoredFlame
                this[particleLegacyOffset + 56] = ParticleSparkler
                this[particleLegacyOffset + 57] = ParticleConduit
                this[particleLegacyOffset + 58] = ParticleBubbleColumnUp
                this[particleLegacyOffset + 59] = ParticleBubbleColumnDown
                this[particleLegacyOffset + 60] = ParticleSneeze
                this[particleLegacyOffset + 61] = ParticleShulkerBullet
                this[particleLegacyOffset + 62] = ParticleBleach
                this[particleLegacyOffset + 63] = ParticleDragonDestroyBlock
                this[particleLegacyOffset + 64] = ParticleMyceliumDust
                this[particleLegacyOffset + 65] = ParticleFallingRedDust
                this[particleLegacyOffset + 66] = ParticleCampfireSmoke
                this[particleLegacyOffset + 67] = ParticleTallCampfireSmoke
                this[particleLegacyOffset + 68] = ParticleRisingDragonsBreath
                this[particleLegacyOffset + 69] = ParticleDragonsBreath
                this[particleLegacyOffset + 70] = ParticleBlueFlame
                this[particleLegacyOffset + 71] = ParticleSoul
                this[particleLegacyOffset + 72] = ParticleObsidianTear
            }
            val registryPre448 = registryPre440.clone().apply {
                this[particleLegacyOffset + 73] = ParticlePortalReverse
                this[particleLegacyOffset + 74] = ParticleSnowflake
                this[particleLegacyOffset + 75] = ParticleVibrationSignal
                this[particleLegacyOffset + 76] = ParticleSculkSensorRedstone
                this[particleLegacyOffset + 77] = ParticleSporeBlossomShower
                this[particleLegacyOffset + 78] = ParticleSporeBlossomAmbient
                this[particleLegacyOffset + 79] = ParticleWax
                this[particleLegacyOffset + 80] = ParticleElectricSpark
            }
            val registryPre465 = registryPre448.clone().apply {
                this[particleLegacyOffset + 9] = ParticleCandleFlame
                this[particleLegacyOffset + 10] = ParticleLava
                this[particleLegacyOffset + 11] = ParticleLargeSmoke
                this[particleLegacyOffset + 12] = ParticleRedstone
                this[particleLegacyOffset + 13] = ParticleRisingRedDust
                this[particleLegacyOffset + 14] = ParticleItemBreak
                this[particleLegacyOffset + 15] = ParticleSnowballPoof
                this[particleLegacyOffset + 16] = ParticleHugeExplode
                this[particleLegacyOffset + 17] = ParticleHugeExplodeSeed
                this[particleLegacyOffset + 18] = ParticleMobFlame
                this[particleLegacyOffset + 19] = ParticleHeart
                this[particleLegacyOffset + 20] = ParticleTerrain
                this[particleLegacyOffset + 21] = ParticleTownAura
                this[particleLegacyOffset + 22] = ParticlePortal
                this[particleLegacyOffset + 23] = ParticleMobPortal
                this[particleLegacyOffset + 24] = ParticleSplash
                this[particleLegacyOffset + 25] = ParticleSplashManual
                this[particleLegacyOffset + 26] = ParticleWaterWake
                this[particleLegacyOffset + 27] = ParticleDripWater
                this[particleLegacyOffset + 28] = ParticleDripLava
                this[particleLegacyOffset + 29] = ParticleDripHoney
                this[particleLegacyOffset + 30] = ParticleStalactiteDripWater
                this[particleLegacyOffset + 31] = ParticleStalactiteDripLava
                this[particleLegacyOffset + 32] = ParticleFallingDust
                this[particleLegacyOffset + 33] = ParticleMobSpell
                this[particleLegacyOffset + 34] = ParticleMobSpellAmbient
                this[particleLegacyOffset + 35] = ParticleMobSpellInstantaneous
                this[particleLegacyOffset + 36] = ParticleInk
                this[particleLegacyOffset + 37] = ParticleSlime
                this[particleLegacyOffset + 38] = ParticleRainSplash
                this[particleLegacyOffset + 39] = ParticleVillagerAngry
                this[particleLegacyOffset + 40] = ParticleVillagerHappy
                this[particleLegacyOffset + 41] = ParticleEnchantmentTable
                this[particleLegacyOffset + 42] = ParticleTrackingEmitter
                this[particleLegacyOffset + 43] = ParticleNote
                this[particleLegacyOffset + 44] = ParticleWitchSpell
                this[particleLegacyOffset + 45] = ParticleCarrot
                this[particleLegacyOffset + 46] = ParticleMobAppearance
                this[particleLegacyOffset + 47] = ParticleEndRod
                this[particleLegacyOffset + 48] = ParticleDragonsBreath
                this[particleLegacyOffset + 49] = ParticleSpit
                this[particleLegacyOffset + 50] = ParticleTotem
                this[particleLegacyOffset + 51] = ParticleFood
                this[particleLegacyOffset + 52] = ParticleFireworksStarter
                this[particleLegacyOffset + 53] = ParticleFireworksSpark
                this[particleLegacyOffset + 54] = ParticleFireworksOverlay
                this[particleLegacyOffset + 55] = ParticleBalloonGas
                this[particleLegacyOffset + 56] = ParticleColoredFlame
                this[particleLegacyOffset + 57] = ParticleSparkler
                this[particleLegacyOffset + 58] = ParticleConduit
                this[particleLegacyOffset + 59] = ParticleBubbleColumnUp
                this[particleLegacyOffset + 60] = ParticleBubbleColumnDown
                this[particleLegacyOffset + 61] = ParticleSneeze
                this[particleLegacyOffset + 62] = ParticleShulkerBullet
                this[particleLegacyOffset + 63] = ParticleBleach
                this[particleLegacyOffset + 64] = ParticleDragonDestroyBlock
                this[particleLegacyOffset + 65] = ParticleMyceliumDust
                this[particleLegacyOffset + 66] = ParticleFallingRedDust
                this[particleLegacyOffset + 67] = ParticleCampfireSmoke
                this[particleLegacyOffset + 68] = ParticleTallCampfireSmoke
                this[particleLegacyOffset + 69] = ParticleRisingDragonsBreath
                this[particleLegacyOffset + 70] = ParticleDragonsBreath
                this[particleLegacyOffset + 71] = ParticleBlueFlame
                this[particleLegacyOffset + 72] = ParticleSoul
                this[particleLegacyOffset + 73] = ParticleObsidianTear
                this[particleLegacyOffset + 74] = ParticlePortalReverse
                this[particleLegacyOffset + 75] = ParticleSnowflake
                this[particleLegacyOffset + 76] = ParticleVibrationSignal
                this[particleLegacyOffset + 77] = ParticleSculkSensorRedstone
                this[particleLegacyOffset + 78] = ParticleSporeBlossomShower
                this[particleLegacyOffset + 79] = ParticleSporeBlossomAmbient
                this[particleLegacyOffset + 80] = ParticleWax
                this[particleLegacyOffset + 81] = ParticleElectricSpark
            }
            val registryPre471 = registryPre465.clone().apply {
                this[particleOffset + 34] = ParticleTurtleEgg
                this[particleOffset + 35] = ParticleSculkShriek
                this[particleLegacyOffset + 82] = ParticleShriek
            }
            val registry = registryPre471.clone().apply {
                this[particleOffset + 36] = ParticleSculkCatalystBloom
                this[particleLegacyOffset + 83] = ParticleSculkSoul
            }

            fun registryByVersion(version: Int): Registry<Event> =
                if (version >= 471) registry
                else if (version >= 465) registryPre471
                else if (version >= 448) registryPre465
                else if (version >= 440) registryPre448
                else if (version >= 431) registryPre440
                else if (version >= 428) registryPre431
                else if (version >= 407) registryPre428
                else registryPre407
        }
    }

    override val id get() = 0x19

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarInt(Event.registryByVersion(version).getId(eventOrParticleType))
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
    override fun read(buffer: PacketBuffer, version: Int) = WorldEventPacket(WorldEventPacket.Event.registryByVersion(version)[buffer.readVarInt()], buffer.readFloat3(), buffer.readVarInt())
}
