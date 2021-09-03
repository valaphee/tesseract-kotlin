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
import com.valaphee.tesseract.util.Int2ObjectOpenHashBiMap

/**
 * @author Kevin Ludwig
 */
data class SoundEventPacket(
    var soundEvent: SoundEvent,
    var position: Float3,
    var extraData: Int,
    var identifier: String,
    var babySound: Boolean,
    var relativeVolumeDisabled: Boolean
) : Packet {
    override val id get() = 0x7B

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarUInt(soundEvents.getKey(soundEvent))
        buffer.writeFloat3(position)
        buffer.writeVarInt(extraData)
        buffer.writeString(identifier)
        buffer.writeBoolean(babySound)
        buffer.writeBoolean(relativeVolumeDisabled)
    }

    override fun handle(handler: PacketHandler) = handler.soundEvent(this)

    companion object {
        internal val soundEvents = Int2ObjectOpenHashBiMap<SoundEvent>().apply {
            this[0x000] = SoundEvent.ItemUseOn
            this[0x001] = SoundEvent.Hit
            this[0x002] = SoundEvent.Step
            this[0x003] = SoundEvent.Fly
            this[0x004] = SoundEvent.Jump
            this[0x005] = SoundEvent.Break
            this[0x006] = SoundEvent.Place
            this[0x007] = SoundEvent.HeavyStep
            this[0x008] = SoundEvent.Gallop
            this[0x009] = SoundEvent.Fall
            this[0x00A] = SoundEvent.Ambient
            this[0x00B] = SoundEvent.AmbientBaby
            this[0x00C] = SoundEvent.AmbientInWater
            this[0x00D] = SoundEvent.Breath
            this[0x00E] = SoundEvent.Death
            this[0x00F] = SoundEvent.DeathInWater
            this[0x010] = SoundEvent.DeathToZombie
            this[0x011] = SoundEvent.Hurt
            this[0x012] = SoundEvent.HurtInWater
            this[0x013] = SoundEvent.Mad
            this[0x014] = SoundEvent.Boost
            this[0x015] = SoundEvent.Bow
            this[0x016] = SoundEvent.SquishBig
            this[0x017] = SoundEvent.SquishSmall
            this[0x018] = SoundEvent.FallBig
            this[0x019] = SoundEvent.FallSmall
            this[0x01A] = SoundEvent.Splash
            this[0x01B] = SoundEvent.Fizz
            this[0x01C] = SoundEvent.Flap
            this[0x01D] = SoundEvent.Swim
            this[0x01E] = SoundEvent.Drink
            this[0x01F] = SoundEvent.Eat
            this[0x020] = SoundEvent.TakeOff
            this[0x021] = SoundEvent.Shake
            this[0x022] = SoundEvent.Plop
            this[0x023] = SoundEvent.Land
            this[0x024] = SoundEvent.Saddle
            this[0x025] = SoundEvent.Armor
            this[0x026] = SoundEvent.MobArmorStandPlace
            this[0x027] = SoundEvent.AddChest
            this[0x028] = SoundEvent.Throw
            this[0x029] = SoundEvent.Attack
            this[0x02A] = SoundEvent.AttackNoDamage
            this[0x02B] = SoundEvent.AttackStrong
            this[0x02C] = SoundEvent.Warn
            this[0x02D] = SoundEvent.Shear
            this[0x02E] = SoundEvent.Milk
            this[0x02F] = SoundEvent.Thunder
            this[0x030] = SoundEvent.Explode
            this[0x031] = SoundEvent.Fire
            this[0x032] = SoundEvent.Ignite
            this[0x033] = SoundEvent.Fuse
            this[0x034] = SoundEvent.Stare
            this[0x035] = SoundEvent.Spawn
            this[0x036] = SoundEvent.Shoot
            this[0x037] = SoundEvent.BreakBlock
            this[0x038] = SoundEvent.Launch
            this[0x039] = SoundEvent.Blast
            this[0x03A] = SoundEvent.LargeBlast
            this[0x03B] = SoundEvent.Twinkle
            this[0x03C] = SoundEvent.Remedy
            this[0x03D] = SoundEvent.Unfect
            this[0x03E] = SoundEvent.LevelUp
            this[0x03F] = SoundEvent.BowHit
            this[0x040] = SoundEvent.BulletHit
            this[0x041] = SoundEvent.ExtinguishFire
            this[0x042] = SoundEvent.ItemFizz
            this[0x043] = SoundEvent.ChestOpen
            this[0x044] = SoundEvent.ChestClosed
            this[0x045] = SoundEvent.ShulkerBoxOpen
            this[0x046] = SoundEvent.ShulkerBoxClosed
            this[0x047] = SoundEvent.EnderChestOpen
            this[0x048] = SoundEvent.EnderChestClosed
            this[0x049] = SoundEvent.PowerOn
            this[0x04A] = SoundEvent.PowerOff
            this[0x04B] = SoundEvent.Attach
            this[0x04C] = SoundEvent.Detach
            this[0x04D] = SoundEvent.Deny
            this[0x04E] = SoundEvent.Tripod
            this[0x04F] = SoundEvent.Pop
            this[0x050] = SoundEvent.DropSlot
            this[0x051] = SoundEvent.Note
            this[0x052] = SoundEvent.Thorns
            this[0x053] = SoundEvent.PistonIn
            this[0x054] = SoundEvent.PistonOut
            this[0x055] = SoundEvent.Portal
            this[0x056] = SoundEvent.Water
            this[0x057] = SoundEvent.LavaPop
            this[0x058] = SoundEvent.Lava
            this[0x059] = SoundEvent.Burp
            this[0x05A] = SoundEvent.BucketFillWater
            this[0x05B] = SoundEvent.BucketFillLava
            this[0x05C] = SoundEvent.BucketEmptyWater
            this[0x05D] = SoundEvent.BucketEmptyLava
            this[0x05E] = SoundEvent.ArmorEquipChain
            this[0x05F] = SoundEvent.ArmorEquipDiamond
            this[0x060] = SoundEvent.ArmorEquipGeneric
            this[0x061] = SoundEvent.ArmorEquipGold
            this[0x062] = SoundEvent.ArmorEquipIron
            this[0x063] = SoundEvent.ArmorEquipLeather
            this[0x064] = SoundEvent.ArmorEquipElytra
            this[0x065] = SoundEvent.Record13
            this[0x066] = SoundEvent.RecordCat
            this[0x067] = SoundEvent.RecordBlocks
            this[0x068] = SoundEvent.RecordChirp
            this[0x069] = SoundEvent.RecordFar
            this[0x06A] = SoundEvent.RecordMall
            this[0x06B] = SoundEvent.RecordMellohi
            this[0x06C] = SoundEvent.RecordStal
            this[0x06D] = SoundEvent.RecordStrad
            this[0x06E] = SoundEvent.RecordWard
            this[0x06F] = SoundEvent.Record11
            this[0x070] = SoundEvent.RecordWait
            this[0x071] = SoundEvent.StopRecord
            this[0x072] = SoundEvent.Flop
            this[0x073] = SoundEvent.ElderGuardianCurse
            this[0x074] = SoundEvent.MobWarning
            this[0x075] = SoundEvent.MobWarningBaby
            this[0x076] = SoundEvent.Teleport
            this[0x077] = SoundEvent.ShulkerOpen
            this[0x078] = SoundEvent.ShulkerClose
            this[0x079] = SoundEvent.Haggle
            this[0x07A] = SoundEvent.HaggleYes
            this[0x07B] = SoundEvent.HaggleNo
            this[0x07C] = SoundEvent.HaggleIdle
            this[0x07D] = SoundEvent.ChorusGrow
            this[0x07E] = SoundEvent.ChorusDeath
            this[0x07F] = SoundEvent.Glass
            this[0x080] = SoundEvent.PotionBrewed
            this[0x081] = SoundEvent.CastSpell
            this[0x082] = SoundEvent.PrepareAttack
            this[0x083] = SoundEvent.PrepareSummon
            this[0x084] = SoundEvent.PrepareWololo
            this[0x085] = SoundEvent.Fang
            this[0x086] = SoundEvent.Charge
            this[0x087] = SoundEvent.CameraTakePicture
            this[0x088] = SoundEvent.LeashKnotPlace
            this[0x089] = SoundEvent.LeashKnotBreak
            this[0x08A] = SoundEvent.Growl
            this[0x08B] = SoundEvent.Whine
            this[0x08C] = SoundEvent.Pant
            this[0x08D] = SoundEvent.Purr
            this[0x08E] = SoundEvent.Purreow
            this[0x08F] = SoundEvent.DeathMinVolume
            this[0x090] = SoundEvent.DeathMidVolume
            this[0x091] = SoundEvent.ImitateBlaze
            this[0x092] = SoundEvent.ImitateCaveSpider
            this[0x093] = SoundEvent.ImitateCreeper
            this[0x094] = SoundEvent.ImitateElderGuardian
            this[0x095] = SoundEvent.ImitateEnderDragon
            this[0x096] = SoundEvent.ImitateEnderman
            this[0x098] = SoundEvent.ImitateEvocationIllager
            this[0x099] = SoundEvent.ImitateGhast
            this[0x09A] = SoundEvent.ImitateHusk
            this[0x09B] = SoundEvent.ImitateIllusionIllager
            this[0x09C] = SoundEvent.ImitateMagmaCube
            this[0x09D] = SoundEvent.ImitatePolarBear
            this[0x09E] = SoundEvent.ImitateShulker
            this[0x09F] = SoundEvent.ImitateSilverfish
            this[0x0A0] = SoundEvent.ImitateSkeleton
            this[0x0A1] = SoundEvent.ImitateSlime
            this[0x0A2] = SoundEvent.ImitateSpider
            this[0x0A3] = SoundEvent.ImitateStray
            this[0x0A4] = SoundEvent.ImitateVex
            this[0x0A5] = SoundEvent.ImitateVindicationIllager
            this[0x0A6] = SoundEvent.ImitateWitch
            this[0x0A7] = SoundEvent.ImitateWither
            this[0x0A8] = SoundEvent.ImitateWitherSkeleton
            this[0x0A9] = SoundEvent.ImitateWolf
            this[0x0AA] = SoundEvent.ImitateZombie
            this[0x0AB] = SoundEvent.ImitateZombiePigman
            this[0x0AC] = SoundEvent.ImitateZombieVillager
            this[0x0AD] = SoundEvent.BlockEndPortalFrameFill
            this[0x0AE] = SoundEvent.BlockEndPortalSpawn
            this[0x0AF] = SoundEvent.RandomAnvilUse
            this[0x0B0] = SoundEvent.BottleDragonBreath
            this[0x0B1] = SoundEvent.PortalTravel
            this[0x0B2] = SoundEvent.ItemTridentHit
            this[0x0B3] = SoundEvent.ItemTridentReturn
            this[0x0B4] = SoundEvent.ItemTridentRiptide1
            this[0x0B5] = SoundEvent.ItemTridentRiptide2
            this[0x0B6] = SoundEvent.ItemTridentRiptide3
            this[0x0B7] = SoundEvent.ItemTridentThrow
            this[0x0B8] = SoundEvent.ItemTridentThunder
            this[0x0B9] = SoundEvent.ItemTridentHitGround
            this[0x0BA] = SoundEvent.Default
            this[0x0BB] = SoundEvent.BlockFletchingTableUse
            this[0x0BC] = SoundEvent.ElementConstructorOpen
            this[0x0BD] = SoundEvent.IceBombHit
            this[0x0BE] = SoundEvent.BalloonPop
            this[0x0BF] = SoundEvent.LabTableReactionIceBomb
            this[0x0C0] = SoundEvent.LabTableReactionBleach
            this[0x0C1] = SoundEvent.LabTableReactionEpaste
            this[0x0C2] = SoundEvent.LabTableReactionEpaste2
            this[0x0C7] = SoundEvent.LabTableReactionFertilizer
            this[0x0C8] = SoundEvent.LabTableReactionFireball
            this[0x0C9] = SoundEvent.LabTableReactionMagnesiumSalt
            this[0x0CA] = SoundEvent.LabTableReactionMiscFire
            this[0x0CB] = SoundEvent.LabTableReactionFire
            this[0x0CC] = SoundEvent.LabTableReactionMiscExplosion
            this[0x0CD] = SoundEvent.LabTableReactionMiscMystical
            this[0x0CE] = SoundEvent.LabTableReactionMiscMystical2
            this[0x0CF] = SoundEvent.LabTableReactionProduct
            this[0x0D0] = SoundEvent.SparklerUse
            this[0x0D1] = SoundEvent.GlowStickUse
            this[0x0D2] = SoundEvent.SparklerActive
            this[0x0D3] = SoundEvent.ConvertToDrowned
            this[0x0D4] = SoundEvent.BucketFillFish
            this[0x0D5] = SoundEvent.BucketEmptyFish
            this[0x0D6] = SoundEvent.BubbleUp
            this[0x0D7] = SoundEvent.BubbleDown
            this[0x0D8] = SoundEvent.BubblePop
            this[0x0D9] = SoundEvent.BubbleUpInside
            this[0x0DA] = SoundEvent.BubbleDownInside
            this[0x0DB] = SoundEvent.HurtBaby
            this[0x0DC] = SoundEvent.DeathBaby
            this[0x0DD] = SoundEvent.StepBaby
            this[0x0DF] = SoundEvent.Born
            this[0x0E0] = SoundEvent.BlockTurtleEggBreak
            this[0x0E1] = SoundEvent.BlockTurtleEggCrack
            this[0x0E2] = SoundEvent.BlockTurtleEggHatch
            this[0x0E4] = SoundEvent.BlockTurtleEggAttack
            this[0x0E5] = SoundEvent.BeaconActivate
            this[0x0E6] = SoundEvent.BeaconAmbient
            this[0x0E7] = SoundEvent.BeaconDeactivate
            this[0x0E8] = SoundEvent.BeaconPower
            this[0x0E9] = SoundEvent.ConduitActivate
            this[0x0EA] = SoundEvent.ConduitAmbient
            this[0x0EB] = SoundEvent.ConduitAttack
            this[0x0EC] = SoundEvent.ConduitDeactivate
            this[0x0ED] = SoundEvent.ConduitShort
            this[0x0EE] = SoundEvent.Swoop
            this[0x0EF] = SoundEvent.BlockBambooSaplingPlace
            this[0x0F0] = SoundEvent.PreSneeze
            this[0x0F1] = SoundEvent.Sneeze
            this[0x0F2] = SoundEvent.AmbientTame
            this[0x0F3] = SoundEvent.Scared
            this[0x0F4] = SoundEvent.BlockScaffoldingClimb
            this[0x0F5] = SoundEvent.CrossbowLoadingStart
            this[0x0F6] = SoundEvent.CrossbowLoadingMiddle
            this[0x0F7] = SoundEvent.CrossbowLoadingEnd
            this[0x0F8] = SoundEvent.CrossbowShoot
            this[0x0F9] = SoundEvent.CrossbowQuickChargeStart
            this[0x0FA] = SoundEvent.CrossbowQuickChargeMiddle
            this[0x0FB] = SoundEvent.CrossbowQuickChargeEnd
            this[0x0FC] = SoundEvent.AmbientAggressive
            this[0x0FD] = SoundEvent.AmbientWorried
            this[0x0FE] = SoundEvent.CannotBreed
            this[0x0FF] = SoundEvent.ItemShieldBlock
            this[0x100] = SoundEvent.ItemBookPut
            this[0x101] = SoundEvent.BlockGrindstoneUse
            this[0x102] = SoundEvent.BlockBellHit
            this[0x103] = SoundEvent.BlockCampfireCrackle
            this[0x104] = SoundEvent.Roar
            this[0x105] = SoundEvent.Stun
            this[0x106] = SoundEvent.BlockSweetBerryBushHurt
            this[0x107] = SoundEvent.BlockSweetBerryBushPick
            this[0x108] = SoundEvent.BlockCartographyTableUse
            this[0x109] = SoundEvent.BlockStoneCutterUse
            this[0x10A] = SoundEvent.BlockComposterEmpty
            this[0x10B] = SoundEvent.BlockComposterFill
            this[0x10C] = SoundEvent.BlockComposterFillSuccess
            this[0x10D] = SoundEvent.BlockComposterReady
            this[0x10E] = SoundEvent.BlockBarrelOpen
            this[0x10F] = SoundEvent.BlockBarrelClose
            this[0x110] = SoundEvent.RaidHorn
            this[0x111] = SoundEvent.BlockLoomUse
            this[0x112] = SoundEvent.AmbientInRaid
            this[0x113] = SoundEvent.UiCartographyTableTakeResult
            this[0x114] = SoundEvent.UiStoneCutterTakeResult
            this[0x115] = SoundEvent.UiLoomTakeResult
            this[0x116] = SoundEvent.BlockSmokerSmoke
            this[0x117] = SoundEvent.BlockBlastFurnaceFireCrackle
            this[0x118] = SoundEvent.BlockSmithingTableUse
            this[0x119] = SoundEvent.Screech
            this[0x11A] = SoundEvent.Sleep
            this[0x11B] = SoundEvent.BlockFurnaceLit
            this[0x11C] = SoundEvent.ConvertMooshroom
            this[0x11D] = SoundEvent.MilkSuspiciously
            this[0x11E] = SoundEvent.Celebrate
            this[0x11F] = SoundEvent.JumpPrevent
            this[0x120] = SoundEvent.AmbientPollinate
            this[0x121] = SoundEvent.BlockBeehiveDrip
            this[0x122] = SoundEvent.BlockBeehiveEnter
            this[0x123] = SoundEvent.BlockBeehiveExit
            this[0x124] = SoundEvent.BlockBeehiveWork
            this[0x125] = SoundEvent.BlockBeehiveShear
            this[0x126] = SoundEvent.HoneyBottleDrink
            this[0x127] = SoundEvent.AmbientCave
            this[0x128] = SoundEvent.Retreat
            this[0x129] = SoundEvent.ConvertToZombified
            this[0x12A] = SoundEvent.Admire
            this[0x12B] = SoundEvent.StepLava
            this[0x12C] = SoundEvent.Tempt
            this[0x12D] = SoundEvent.Panic
            this[0x12E] = SoundEvent.Angry
            this[0x12F] = SoundEvent.AmbientWarpedForest
            this[0x130] = SoundEvent.AmbientSoulSandValley
            this[0x131] = SoundEvent.AmbientNetherWastes
            this[0x132] = SoundEvent.AmbientBasaltDeltas
            this[0x133] = SoundEvent.AmbientCrimsonForest
            this[0x134] = SoundEvent.RespawnAnchorCharge
            this[0x135] = SoundEvent.RespawnAnchorDeplete
            this[0x136] = SoundEvent.RespawnAnchorSetSpawn
            this[0x137] = SoundEvent.RespawnAnchorAmbient
            this[0x138] = SoundEvent.SoulEscapeQuiet
            this[0x139] = SoundEvent.SoulEscapeLoud
            this[0x13A] = SoundEvent.RecordPigStep
            this[0x13B] = SoundEvent.LinkCompassToLodestone
            this[0x13C] = SoundEvent.UseSmithingTable
            this[0x13D] = SoundEvent.EquipNetherite
            this[0x13E] = SoundEvent.AmbientLoopWarpedForest
            this[0x13F] = SoundEvent.AmbientLoopSoulsandValley
            this[0x140] = SoundEvent.AmbientLoopNetherWastes
            this[0x141] = SoundEvent.AmbientLoopBasaltDeltas
            this[0x142] = SoundEvent.AmbientLoopCrimsonForest
            this[0x143] = SoundEvent.AmbientAdditionWarpedForest
            this[0x144] = SoundEvent.AmbientAdditionSoulsandValley
            this[0x145] = SoundEvent.AmbientAdditionNetherWastes
            this[0x146] = SoundEvent.AmbientAdditionBasaltDeltas
            this[0x147] = SoundEvent.AmbientAdditionCrimsonForest
            this[0x148] = SoundEvent.SculkSensorPowerOn
            this[0x149] = SoundEvent.SculkSensorPowerOff
            this[0x14A] = SoundEvent.BucketFillPowderSnow
            this[0x14B] = SoundEvent.BucketEmptyPowderSnow
            this[0x14C] = SoundEvent.PointedDripstoneCauldronDripLava
            this[0x14D] = SoundEvent.PointedDripstoneCauldronDripWater
            this[0x14E] = SoundEvent.PointedDripstoneDripLava
            this[0x14F] = SoundEvent.PointedDripstonDripWater
            this[0x150] = SoundEvent.CaveVinesPickBerries
            this[0x151] = SoundEvent.BigDripleafTiltDown
            this[0x152] = SoundEvent.BigDripleafTiltUp
            this[0x153] = SoundEvent.CopperWaxOn
            this[0x154] = SoundEvent.CopperWaxOff
            this[0x155] = SoundEvent.Scrape
            this[0x156] = SoundEvent.PlayerHurtDrown
            this[0x157] = SoundEvent.PlayerHurtOnFire
            this[0x158] = SoundEvent.PlayerHurtFreeze
            this[0x159] = SoundEvent.UseSpyglass
            this[0x15A] = SoundEvent.StopUsingSpyglass
            this[0x15B] = SoundEvent.AmethystBlockChime
            this[0x15C] = SoundEvent.AmbientScreamer
            this[0x15D] = SoundEvent.HurtScreamer
            this[0x15E] = SoundEvent.DeathScreamer
            this[0x15F] = SoundEvent.MilkScreamer
            this[0x160] = SoundEvent.JumpToBlock
            this[0x161] = SoundEvent.PreRam
            this[0x162] = SoundEvent.PreRamScreamer
            this[0x163] = SoundEvent.RamImpact
            this[0x164] = SoundEvent.RamImpactScreamer
            this[0x165] = SoundEvent.SquidInkSquirt
            this[0x166] = SoundEvent.GlowSquidInkSquirt
            this[0x167] = SoundEvent.ConvertToStray
            this[0x168] = SoundEvent.CakeAddCandle
            this[0x169] = SoundEvent.ExtinguishCandle
            this[0x170] = SoundEvent.AmbientCandle
            defaultReturnValue(SoundEvent.Undefined)
        }
    }
}

/**
 * @author Kevin Ludwig
 */
object SoundEventPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = SoundEventPacket(SoundEventPacket.soundEvents[buffer.readVarUInt()], buffer.readFloat3(), buffer.readVarInt(), buffer.readString(), buffer.readBoolean(), buffer.readBoolean())
}
