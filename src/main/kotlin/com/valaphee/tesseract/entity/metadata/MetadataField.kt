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

package com.valaphee.tesseract.entity.metadata

/**
 * @author Kevin Ludwig
 */
class MetadataField<T>(
    val id: Int,
    val type: MetadataType<T>
) {
    companion object {
        val Flags = MetadataField(0, MetadataType.Flags)
        val Health = MetadataField(1, MetadataType.Int)
        val Variant = MetadataField(2, MetadataType.Int)
        val Color = MetadataField(3, MetadataType.Byte)
        val NameTag = MetadataField(4, MetadataType.String)
        val Owner = MetadataField(5, MetadataType.Long)
        val EntityTarget = MetadataField(6, MetadataType.Long)
        val Breath = MetadataField(7, MetadataType.Short)
        val PotionColor = MetadataField(8, MetadataType.Int)
        val PotionAmbient = MetadataField(9, MetadataType.Byte)
        val JumpDuration = MetadataField(10, MetadataType.Byte)
        val HurtDuration = MetadataField(11, MetadataType.Int)
        val HurtDirection = MetadataField(12, MetadataType.Int)
        val PaddleTimeLeft = MetadataField(13, MetadataType.Float)
        val PaddleTimeRight = MetadataField(14, MetadataType.Float)
        val Experience = MetadataField(15, MetadataType.Int)
        const val Display = 16
        val DisplayYOffset = MetadataField(17, MetadataType.Int)
        val HasDisplay = MetadataField(18, MetadataType.Byte)
        val Swell = MetadataField(19, MetadataType.Byte)
        val OldSwell = MetadataField(20, MetadataType.Byte)
        val SwellDirection = MetadataField(21, MetadataType.Byte)
        val Ignited = MetadataField(22, MetadataType.Byte)
        val EndermanHoldingBlockStateId = MetadataField(23, MetadataType.Int)
        val EntityAge = MetadataField(24, MetadataType.Byte)
        val WitchUnknown = MetadataField(25, MetadataType.Byte)
        val CanStartSleep = MetadataField(26, MetadataType.Byte)
        val PlayerIndex = MetadataField(27, MetadataType.Int)
        val Bed = MetadataField(28, MetadataType.Int3)
        val FireballPowerX = MetadataField(29, MetadataType.Float)
        val FireballPowerY = MetadataField(30, MetadataType.Float)
        val FireballPowerZ = MetadataField(31, MetadataType.Float)
        val Power = MetadataField(32, MetadataType.Float)
        val FishX = MetadataField(33, MetadataType.Float)
        val FishZ = MetadataField(34, MetadataType.Float)
        val FishAngle = MetadataField(35, MetadataType.Float)
        val PotionAuxValue = MetadataField(36, MetadataType.Short)
        val LeadHolder = MetadataField(37, MetadataType.Long)
        val Scale = MetadataField(38, MetadataType.Float)
        val HasNpcComponent = MetadataField(39, MetadataType.Byte)
        val NpcSkinId = MetadataField(40, MetadataType.String)
        val UrlTag = MetadataField(41, MetadataType.String)
        val MaximumBreath = MetadataField(42, MetadataType.Short)
        val MarkVariant = MetadataField(43, MetadataType.Int)
        val ContainerType = MetadataField(44, MetadataType.Byte)
        val ContainerBaseSize = MetadataField(45, MetadataType.Int)
        val ContainerExtraSlotsPerStrength = MetadataField(46, MetadataType.Int)
        val BlockTarget = MetadataField(47, MetadataType.Int3)
        val WitherInvulnerableTicks = MetadataField(48, MetadataType.Int)
        val WitherTarget1 = MetadataField(49, MetadataType.Long)
        val WitherTarget2 = MetadataField(50, MetadataType.Long)
        val WitherTarget3 = MetadataField(51, MetadataType.Long)
        val WitherAerialAttack = MetadataField(52, MetadataType.Short)
        val BoundingBoxWidth = MetadataField(53, MetadataType.Float)
        val BoundingBoxHeight = MetadataField(54, MetadataType.Float)
        val FuseLength = MetadataField(55, MetadataType.Int)
        val Seat = MetadataField(56, MetadataType.Float3)
        val RotationLocked = MetadataField(57, MetadataType.Byte)
        val MaximumRotation = MetadataField(58, MetadataType.Float)
        val MinimumRotation = MetadataField(59, MetadataType.Float)
        val RotationOffset = MetadataField(60, MetadataType.Float)
        val AreaEffectCloudRadius = MetadataField(61, MetadataType.Float)
        val AreaEffectCloudWaiting = MetadataField(62, MetadataType.Int)
        val AreaEffectCloudParticleId = MetadataField(63, MetadataType.Int)
        val ShulkerPeakHeight = MetadataField(64, MetadataType.Int)
        val ShulkerAttachFace = MetadataField(65, MetadataType.Byte)
        val ShulkerAttached = MetadataField(66, MetadataType.Short)
        val ShulkerAttachPosition = MetadataField(67, MetadataType.Int3)
        val TradingPlayer = MetadataField(68, MetadataType.Long)
        val TradingCareer = MetadataField(69, MetadataType.Byte)
        val CommandBlockEnabled = MetadataField(70, MetadataType.Byte)
        val CommandBlockCommand = MetadataField(71, MetadataType.String)
        val CommandBlockLastOutput = MetadataField(72, MetadataType.String)
        val CommandBlockTrackOutput = MetadataField(73, MetadataType.Byte)
        val ControllingRiderSeatNumber = MetadataField(74, MetadataType.Byte)
        val Strength = MetadataField(75, MetadataType.Int)
        val MaximumStrength = MetadataField(76, MetadataType.Int)
        val EvokerSpellColor = MetadataField(77, MetadataType.Int)
        val LimitedLife = MetadataField(78, MetadataType.Int)
        val ArmorStandPoseIndex = MetadataField(79, MetadataType.Int)
        val EnderCrystalTimeOffset = MetadataField(80, MetadataType.Int)
        val AlwaysShowNameTag = MetadataField(81, MetadataType.Byte)
        val Color2 = MetadataField(82, MetadataType.Byte)
        val Designator = MetadataField(83, MetadataType.Byte)
        val ScoreTag = MetadataField(84, MetadataType.String)
        val BalloonAttachedEntity = MetadataField(85, MetadataType.Long)
        val PufferfishSize = MetadataField(86, MetadataType.Byte)
        val BoatBubbleTime = MetadataField(87, MetadataType.Int)
        val Agent = MetadataField(88, MetadataType.Long)
        val Sitting = MetadataField(89, MetadataType.Int)
        val SittingPrevious = MetadataField(90, MetadataType.Int)
        val EatCounter = MetadataField(91, MetadataType.Int)
        val Flags2 = MetadataField(92, MetadataType.Flags2)
        val Laying = MetadataField(93, MetadataType.Int)
        val LayingPrevious = MetadataField(94, MetadataType.Int)
        val AreaEffectCloudDuration = MetadataField(95, MetadataType.Int)
        val AreaEffectCloudSpawnTime = MetadataField(96, MetadataType.Int)
        val AreaEffectCloudRadiusPerTick = MetadataField(97, MetadataType.Float)
        val AreaEffectCloudRadiusChangeOnPickup = MetadataField(98, MetadataType.Float)
        val AreaEffectCloudPickupCount = MetadataField(99, MetadataType.Int)
        val InteractiveTag = MetadataField(100, MetadataType.String)
        val TradeTier = MetadataField(101, MetadataType.Int)
        val MaximumTradeTier = MetadataField(102, MetadataType.Int)
        val TradeExperience = MetadataField(103, MetadataType.Int)
        val AppearanceId = MetadataField(104, MetadataType.String)
        val SpawningFrames = MetadataField(105, MetadataType.Int)
        val CommandBlockTickDelay = MetadataField(106, MetadataType.Int)
        val CommandBlockExecuteOnFirstTick = MetadataField(107, MetadataType.Byte)
        val AmbientSoundInterval = MetadataField(108, MetadataType.Int)
        val AmbientSoundEventName = MetadataField(109, MetadataType.String)
        val FallDamageMultiplier = MetadataField(110, MetadataType.Float)
        val RawName = MetadataField(111, MetadataType.String)
        val CanRide = MetadataField(112, MetadataType.Byte)
        val LowTierCuredTradeDiscount = MetadataField(113, MetadataType.Float)
        val HighTierCuredTradeDiscount = MetadataField(114, MetadataType.Float)
        val NearbyCuredTradeDiscount = MetadataField(115, MetadataType.Float)
        val NearbyCuredDiscountTimeStamp = MetadataField(116, MetadataType.Long)
        val HitBox = MetadataField(117, MetadataType.Int3)
        val IsBuoyant = MetadataField(118, MetadataType.Byte)
        val BuoyancyData = MetadataField(119, MetadataType.String)
        val FreezingEffectStrength = MetadataField(120, MetadataType.Float)
        val GoatHornCount = MetadataField(121, MetadataType.Int)
        val BaseRuntimeId = MetadataField(122, MetadataType.Int)
        val DefineProperties = MetadataField(123, MetadataType.Int)
        val UpdateProperties = MetadataField(124, MetadataType.Int)
    }
}
