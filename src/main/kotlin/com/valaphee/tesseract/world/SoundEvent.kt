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

import com.valaphee.tesseract.util.Registry

/**
 * @author Kevin Ludwig
 */
enum class SoundEvent {
    ItemUseOn,
    Hit,
    Step,
    Fly,
    Jump,
    Break,
    Place,
    HeavyStep,
    Gallop,
    Fall,
    Ambient,
    AmbientBaby,
    AmbientInWater,
    Breath,
    Death,
    DeathInWater,
    DeathToZombie,
    Hurt,
    HurtInWater,
    Mad,
    Boost,
    Bow,
    SquishBig,
    SquishSmall,
    FallBig,
    FallSmall,
    Splash,
    Fizz,
    Flap,
    Swim,
    Drink,
    Eat,
    TakeOff,
    Shake,
    Plop,
    Land,
    Saddle,
    Armor,
    MobArmorStandPlace,
    AddChest,
    Throw,
    Attack,
    AttackNoDamage,
    AttackStrong,
    Warn,
    Shear,
    Milk,
    Thunder,
    Explode,
    Fire,
    Ignite,
    Fuse,
    Stare,
    Spawn,
    Shoot,
    BreakBlock,
    Launch,
    Blast,
    LargeBlast,
    Twinkle,
    Remedy,
    Unfect,
    LevelUp,
    BowHit,
    BulletHit,
    ExtinguishFire,
    ItemFizz,
    ChestOpen,
    ChestClosed,
    ShulkerBoxOpen,
    ShulkerBoxClosed,
    EnderChestOpen,
    EnderChestClosed,
    PowerOn,
    PowerOff,
    Attach,
    Detach,
    Deny,
    Tripod,
    Pop,
    DropSlot,
    Note,
    Thorns,
    PistonIn,
    PistonOut,
    Portal,
    Water,
    LavaPop,
    Lava,
    Burp,
    BucketFillWater,
    BucketFillLava,
    BucketEmptyWater,
    BucketEmptyLava,
    ArmorEquipChain,
    ArmorEquipDiamond,
    ArmorEquipGeneric,
    ArmorEquipGold,
    ArmorEquipIron,
    ArmorEquipLeather,
    ArmorEquipElytra,
    Record13,
    RecordCat,
    RecordBlocks,
    RecordChirp,
    RecordFar,
    RecordMall,
    RecordMellohi,
    RecordStal,
    RecordStrad,
    RecordWard,
    Record11,
    RecordWait,
    StopRecord,
    Flop,
    ElderGuardianCurse,
    MobWarning,
    MobWarningBaby,
    Teleport,
    ShulkerOpen,
    ShulkerClose,
    Haggle,
    HaggleYes,
    HaggleNo,
    HaggleIdle,
    ChorusGrow,
    ChorusDeath,
    Glass,
    PotionBrewed,
    CastSpell,
    PrepareAttack,
    PrepareSummon,
    PrepareWololo,
    Fang,
    Charge,
    CameraTakePicture,
    LeashKnotPlace,
    LeashKnotBreak,
    Growl,
    Whine,
    Pant,
    Purr,
    Purreow,
    DeathMinVolume,
    DeathMidVolume,
    ImitateBlaze,
    ImitateCaveSpider,
    ImitateCreeper,
    ImitateElderGuardian,
    ImitateEnderDragon,
    ImitateEnderman,
    ImitateEvocationIllager,
    ImitateGhast,
    ImitateHusk,
    ImitateIllusionIllager,
    ImitateMagmaCube,
    ImitatePolarBear,
    ImitateShulker,
    ImitateSilverfish,
    ImitateSkeleton,
    ImitateSlime,
    ImitateSpider,
    ImitateStray,
    ImitateVex,
    ImitateVindicationIllager,
    ImitateWitch,
    ImitateWither,
    ImitateWitherSkeleton,
    ImitateWolf,
    ImitateZombie,
    ImitateZombiePigman,
    ImitateZombieVillager,
    BlockEndPortalFrameFill,
    BlockEndPortalSpawn,
    RandomAnvilUse,
    BottleDragonBreath,
    PortalTravel,
    ItemTridentHit,
    ItemTridentReturn,
    ItemTridentRiptide1,
    ItemTridentRiptide2,
    ItemTridentRiptide3,
    ItemTridentThrow,
    ItemTridentThunder,
    ItemTridentHitGround,
    Default,
    BlockFletchingTableUse,
    ElementConstructorOpen,
    IceBombHit,
    BalloonPop,
    LabTableReactionIceBomb,
    LabTableReactionBleach,
    LabTableReactionEpaste,
    LabTableReactionEpaste2,
    LabTableReactionFertilizer,
    LabTableReactionFireball,
    LabTableReactionMagnesiumSalt,
    LabTableReactionMiscFire,
    LabTableReactionFire,
    LabTableReactionMiscExplosion,
    LabTableReactionMiscMystical,
    LabTableReactionMiscMystical2,
    LabTableReactionProduct,
    SparklerUse,
    GlowStickUse,
    SparklerActive,
    ConvertToDrowned,
    BucketFillFish,
    BucketEmptyFish,
    BubbleUp,
    BubbleDown,
    BubblePop,
    BubbleUpInside,
    BubbleDownInside,
    HurtBaby,
    DeathBaby,
    StepBaby,
    Born,
    BlockTurtleEggBreak,
    BlockTurtleEggCrack,
    BlockTurtleEggHatch,
    BlockTurtleEggAttack,
    BeaconActivate,
    BeaconAmbient,
    BeaconDeactivate,
    BeaconPower,
    ConduitActivate,
    ConduitAmbient,
    ConduitAttack,
    ConduitDeactivate,
    ConduitShort,
    Swoop,
    BlockBambooSaplingPlace,
    PreSneeze,
    Sneeze,
    AmbientTame,
    Scared,
    BlockScaffoldingClimb,
    CrossbowLoadingStart,
    CrossbowLoadingMiddle,
    CrossbowLoadingEnd,
    CrossbowShoot,
    CrossbowQuickChargeStart,
    CrossbowQuickChargeMiddle,
    CrossbowQuickChargeEnd,
    AmbientAggressive,
    AmbientWorried,
    CannotBreed,
    ItemShieldBlock,
    ItemBookPut,
    BlockGrindstoneUse,
    BlockBellHit,
    BlockCampfireCrackle,
    Roar,
    Stun,
    BlockSweetBerryBushHurt,
    BlockSweetBerryBushPick,
    BlockCartographyTableUse,
    BlockStoneCutterUse,
    BlockComposterEmpty,
    BlockComposterFill,
    BlockComposterFillSuccess,
    BlockComposterReady,
    BlockBarrelOpen,
    BlockBarrelClose,
    RaidHorn,
    BlockLoomUse,
    AmbientInRaid,
    UiCartographyTableTakeResult,
    UiStoneCutterTakeResult,
    UiLoomTakeResult,
    BlockSmokerSmoke,
    BlockBlastFurnaceFireCrackle,
    BlockSmithingTableUse,
    Screech,
    BlockFurnaceLit,
    Sleep,
    ConvertMooshroom,
    MilkSuspiciously,
    Celebrate,
    JumpPrevent,
    AmbientPollinate,
    BlockBeehiveDrip,
    BlockBeehiveEnter,
    BlockBeehiveExit,
    BlockBeehiveWork,
    BlockBeehiveShear,
    HoneyBottleDrink,
    AmbientCave,
    Retreat,
    ConvertToZombified,
    Admire,
    StepLava,
    Tempt,
    Panic,
    Angry,
    AmbientWarpedForest,
    AmbientSoulSandValley,
    AmbientNetherWastes,
    AmbientBasaltDeltas,
    AmbientCrimsonForest,
    RespawnAnchorCharge,
    RespawnAnchorDeplete,
    RespawnAnchorSetSpawn,
    RespawnAnchorAmbient,
    SoulEscapeQuiet,
    SoulEscapeLoud,
    RecordPigstep,
    LinkCompassToLodestone,
    UseSmithingTable,
    EquipNetherite,
    AmbientLoopWarpedForest,
    AmbientLoopSoulsandValley,
    AmbientLoopNetherWastes,
    AmbientLoopBasaltDeltas,
    AmbientLoopCrimsonForest,
    AmbientAdditionWarpedForest,
    AmbientAdditionSoulsandValley,
    AmbientAdditionNetherWastes,
    AmbientAdditionBasaltDeltas,
    AmbientAdditionCrimsonForest,
    SculkSensorPowerOn,
    SculkSensorPowerOff,
    BucketFillPowderSnow,
    BucketEmptyPowderSnow,
    PointedDripstoneCauldronDripLava,
    PointedDripstoneCauldronDripWater,
    PointedDripstoneDripLava,
    PointedDripstonDripWater,
    CaveVinesPickBerries,
    BigDripleafTiltDown,
    BigDripleafTiltUp,
    CopperWaxOn,
    CopperWaxOff,
    Scrape,
    PlayerHurtDrown,
    PlayerHurtOnFire,
    PlayerHurtFreeze,
    UseSpyglass,
    StopUsingSpyglass,
    AmethystBlockChime,
    AmbientScreamer,
    HurtScreamer,
    DeathScreamer,
    MilkScreamer,
    JumpToBlock,
    PreRam,
    PreRamScreamer,
    RamImpact,
    RamImpactScreamer,
    SquidInkSquirt,
    GlowSquidInkSquirt,
    ConvertToStray,
    CakeAddCandle,
    ExtinguishCandle,
    AmbientCandle,
    BlockClick,
    BlockClickFail,
    SculkCatalystBloom,
    SculkShriekerShriek,
    WardenNearbyClose,
    WardenNearbyCloser,
    WardenNearbyClosest,
    WardenSlightlyAngry,
    Undefined;

    companion object {
        val registry = Registry(Undefined).apply {
            this[0x000] = ItemUseOn
            this[0x001] = Hit
            this[0x002] = Step
            this[0x003] = Fly
            this[0x004] = Jump
            this[0x005] = Break
            this[0x006] = Place
            this[0x007] = HeavyStep
            this[0x008] = Gallop
            this[0x009] = Fall
            this[0x00A] = Ambient
            this[0x00B] = AmbientBaby
            this[0x00C] = AmbientInWater
            this[0x00D] = Breath
            this[0x00E] = Death
            this[0x00F] = DeathInWater
            this[0x010] = DeathToZombie
            this[0x011] = Hurt
            this[0x012] = HurtInWater
            this[0x013] = Mad
            this[0x014] = Boost
            this[0x015] = Bow
            this[0x016] = SquishBig
            this[0x017] = SquishSmall
            this[0x018] = FallBig
            this[0x019] = FallSmall
            this[0x01A] = Splash
            this[0x01B] = Fizz
            this[0x01C] = Flap
            this[0x01D] = Swim
            this[0x01E] = Drink
            this[0x01F] = Eat
            this[0x020] = TakeOff
            this[0x021] = Shake
            this[0x022] = Plop
            this[0x023] = Land
            this[0x024] = Saddle
            this[0x025] = Armor
            this[0x026] = MobArmorStandPlace
            this[0x027] = AddChest
            this[0x028] = Throw
            this[0x029] = Attack
            this[0x02A] = AttackNoDamage
            this[0x02B] = AttackStrong
            this[0x02C] = Warn
            this[0x02D] = Shear
            this[0x02E] = Milk
            this[0x02F] = Thunder
            this[0x030] = Explode
            this[0x031] = Fire
            this[0x032] = Ignite
            this[0x033] = Fuse
            this[0x034] = Stare
            this[0x035] = Spawn
            this[0x036] = Shoot
            this[0x037] = BreakBlock
            this[0x038] = Launch
            this[0x039] = Blast
            this[0x03A] = LargeBlast
            this[0x03B] = Twinkle
            this[0x03C] = Remedy
            this[0x03D] = Unfect
            this[0x03E] = LevelUp
            this[0x03F] = BowHit
            this[0x040] = BulletHit
            this[0x041] = ExtinguishFire
            this[0x042] = ItemFizz
            this[0x043] = ChestOpen
            this[0x044] = ChestClosed
            this[0x045] = ShulkerBoxOpen
            this[0x046] = ShulkerBoxClosed
            this[0x047] = EnderChestOpen
            this[0x048] = EnderChestClosed
            this[0x049] = PowerOn
            this[0x04A] = PowerOff
            this[0x04B] = Attach
            this[0x04C] = Detach
            this[0x04D] = Deny
            this[0x04E] = Tripod
            this[0x04F] = Pop
            this[0x050] = DropSlot
            this[0x051] = Note
            this[0x052] = Thorns
            this[0x053] = PistonIn
            this[0x054] = PistonOut
            this[0x055] = Portal
            this[0x056] = Water
            this[0x057] = LavaPop
            this[0x058] = Lava
            this[0x059] = Burp
            this[0x05A] = BucketFillWater
            this[0x05B] = BucketFillLava
            this[0x05C] = BucketEmptyWater
            this[0x05D] = BucketEmptyLava
            this[0x05E] = ArmorEquipChain
            this[0x05F] = ArmorEquipDiamond
            this[0x060] = ArmorEquipGeneric
            this[0x061] = ArmorEquipGold
            this[0x062] = ArmorEquipIron
            this[0x063] = ArmorEquipLeather
            this[0x064] = ArmorEquipElytra
            this[0x065] = Record13
            this[0x066] = RecordCat
            this[0x067] = RecordBlocks
            this[0x068] = RecordChirp
            this[0x069] = RecordFar
            this[0x06A] = RecordMall
            this[0x06B] = RecordMellohi
            this[0x06C] = RecordStal
            this[0x06D] = RecordStrad
            this[0x06E] = RecordWard
            this[0x06F] = Record11
            this[0x070] = RecordWait
            this[0x071] = StopRecord
            this[0x072] = Flop
            this[0x073] = ElderGuardianCurse
            this[0x074] = MobWarning
            this[0x075] = MobWarningBaby
            this[0x076] = Teleport
            this[0x077] = ShulkerOpen
            this[0x078] = ShulkerClose
            this[0x079] = Haggle
            this[0x07A] = HaggleYes
            this[0x07B] = HaggleNo
            this[0x07C] = HaggleIdle
            this[0x07D] = ChorusGrow
            this[0x07E] = ChorusDeath
            this[0x07F] = Glass
            this[0x080] = PotionBrewed
            this[0x081] = CastSpell
            this[0x082] = PrepareAttack
            this[0x083] = PrepareSummon
            this[0x084] = PrepareWololo
            this[0x085] = Fang
            this[0x086] = Charge
            this[0x087] = CameraTakePicture
            this[0x088] = LeashKnotPlace
            this[0x089] = LeashKnotBreak
            this[0x08A] = Growl
            this[0x08B] = Whine
            this[0x08C] = Pant
            this[0x08D] = Purr
            this[0x08E] = Purreow
            this[0x08F] = DeathMinVolume
            this[0x090] = DeathMidVolume
            this[0x091] = ImitateBlaze
            this[0x092] = ImitateCaveSpider
            this[0x093] = ImitateCreeper
            this[0x094] = ImitateElderGuardian
            this[0x095] = ImitateEnderDragon
            this[0x096] = ImitateEnderman
            this[0x098] = ImitateEvocationIllager
            this[0x099] = ImitateGhast
            this[0x09A] = ImitateHusk
            this[0x09B] = ImitateIllusionIllager
            this[0x09C] = ImitateMagmaCube
            this[0x09D] = ImitatePolarBear
            this[0x09E] = ImitateShulker
            this[0x09F] = ImitateSilverfish
            this[0x0A0] = ImitateSkeleton
            this[0x0A1] = ImitateSlime
            this[0x0A2] = ImitateSpider
            this[0x0A3] = ImitateStray
            this[0x0A4] = ImitateVex
            this[0x0A5] = ImitateVindicationIllager
            this[0x0A6] = ImitateWitch
            this[0x0A7] = ImitateWither
            this[0x0A8] = ImitateWitherSkeleton
            this[0x0A9] = ImitateWolf
            this[0x0AA] = ImitateZombie
            this[0x0AB] = ImitateZombiePigman
            this[0x0AC] = ImitateZombieVillager
            this[0x0AD] = BlockEndPortalFrameFill
            this[0x0AE] = BlockEndPortalSpawn
            this[0x0AF] = RandomAnvilUse
            this[0x0B0] = BottleDragonBreath
            this[0x0B1] = PortalTravel
            this[0x0B2] = ItemTridentHit
            this[0x0B3] = ItemTridentReturn
            this[0x0B4] = ItemTridentRiptide1
            this[0x0B5] = ItemTridentRiptide2
            this[0x0B6] = ItemTridentRiptide3
            this[0x0B7] = ItemTridentThrow
            this[0x0B8] = ItemTridentThunder
            this[0x0B9] = ItemTridentHitGround
            this[0x0BA] = Default
            this[0x0BB] = BlockFletchingTableUse
            this[0x0BC] = ElementConstructorOpen
            this[0x0BD] = IceBombHit
            this[0x0BE] = BalloonPop
            this[0x0BF] = LabTableReactionIceBomb
            this[0x0C0] = LabTableReactionBleach
            this[0x0C1] = LabTableReactionEpaste
            this[0x0C2] = LabTableReactionEpaste2
            this[0x0C7] = LabTableReactionFertilizer
            this[0x0C8] = LabTableReactionFireball
            this[0x0C9] = LabTableReactionMagnesiumSalt
            this[0x0CA] = LabTableReactionMiscFire
            this[0x0CB] = LabTableReactionFire
            this[0x0CC] = LabTableReactionMiscExplosion
            this[0x0CD] = LabTableReactionMiscMystical
            this[0x0CE] = LabTableReactionMiscMystical2
            this[0x0CF] = LabTableReactionProduct
            this[0x0D0] = SparklerUse
            this[0x0D1] = GlowStickUse
            this[0x0D2] = SparklerActive
            this[0x0D3] = ConvertToDrowned
            this[0x0D4] = BucketFillFish
            this[0x0D5] = BucketEmptyFish
            this[0x0D6] = BubbleUp
            this[0x0D7] = BubbleDown
            this[0x0D8] = BubblePop
            this[0x0D9] = BubbleUpInside
            this[0x0DA] = BubbleDownInside
            this[0x0DB] = HurtBaby
            this[0x0DC] = DeathBaby
            this[0x0DD] = StepBaby
            this[0x0DF] = Born
            this[0x0E0] = BlockTurtleEggBreak
            this[0x0E1] = BlockTurtleEggCrack
            this[0x0E2] = BlockTurtleEggHatch
            this[0x0E4] = BlockTurtleEggAttack
            this[0x0E5] = BeaconActivate
            this[0x0E6] = BeaconAmbient
            this[0x0E7] = BeaconDeactivate
            this[0x0E8] = BeaconPower
            this[0x0E9] = ConduitActivate
            this[0x0EA] = ConduitAmbient
            this[0x0EB] = ConduitAttack
            this[0x0EC] = ConduitDeactivate
            this[0x0ED] = ConduitShort
            this[0x0EE] = Swoop
            this[0x0EF] = BlockBambooSaplingPlace
            this[0x0F0] = PreSneeze
            this[0x0F1] = Sneeze
            this[0x0F2] = AmbientTame
            this[0x0F3] = Scared
            this[0x0F4] = BlockScaffoldingClimb
            this[0x0F5] = CrossbowLoadingStart
            this[0x0F6] = CrossbowLoadingMiddle
            this[0x0F7] = CrossbowLoadingEnd
            this[0x0F8] = CrossbowShoot
            this[0x0F9] = CrossbowQuickChargeStart
            this[0x0FA] = CrossbowQuickChargeMiddle
            this[0x0FB] = CrossbowQuickChargeEnd
            this[0x0FC] = AmbientAggressive
            this[0x0FD] = AmbientWorried
            this[0x0FE] = CannotBreed
            this[0x0FF] = ItemShieldBlock
            this[0x100] = ItemBookPut
            this[0x101] = BlockGrindstoneUse
            this[0x102] = BlockBellHit
            this[0x103] = BlockCampfireCrackle
            this[0x104] = Roar
            this[0x105] = Stun
            this[0x106] = BlockSweetBerryBushHurt
            this[0x107] = BlockSweetBerryBushPick
            this[0x108] = BlockCartographyTableUse
            this[0x109] = BlockStoneCutterUse
            this[0x10A] = BlockComposterEmpty
            this[0x10B] = BlockComposterFill
            this[0x10C] = BlockComposterFillSuccess
            this[0x10D] = BlockComposterReady
            this[0x10E] = BlockBarrelOpen
            this[0x10F] = BlockBarrelClose
            this[0x110] = RaidHorn
            this[0x111] = BlockLoomUse
            this[0x112] = AmbientInRaid
            this[0x113] = UiCartographyTableTakeResult
            this[0x114] = UiStoneCutterTakeResult
            this[0x115] = UiLoomTakeResult
            this[0x116] = BlockSmokerSmoke
            this[0x117] = BlockBlastFurnaceFireCrackle
            this[0x118] = BlockSmithingTableUse
            this[0x119] = Screech
            this[0x11A] = Sleep
            this[0x11B] = BlockFurnaceLit
            this[0x11C] = ConvertMooshroom
            this[0x11D] = MilkSuspiciously
            this[0x11E] = Celebrate
            this[0x11F] = JumpPrevent
            this[0x120] = AmbientPollinate
            this[0x121] = BlockBeehiveDrip
            this[0x122] = BlockBeehiveEnter
            this[0x123] = BlockBeehiveExit
            this[0x124] = BlockBeehiveWork
            this[0x125] = BlockBeehiveShear
            this[0x126] = HoneyBottleDrink
            this[0x127] = AmbientCave
            this[0x128] = Retreat
            this[0x129] = ConvertToZombified
            this[0x12A] = Admire
            this[0x12B] = StepLava
            this[0x12C] = Tempt
            this[0x12D] = Panic
            this[0x12E] = Angry
            this[0x12F] = AmbientWarpedForest
            this[0x130] = AmbientSoulSandValley
            this[0x131] = AmbientNetherWastes
            this[0x132] = AmbientBasaltDeltas
            this[0x133] = AmbientCrimsonForest
            this[0x134] = RespawnAnchorCharge
            this[0x135] = RespawnAnchorDeplete
            this[0x136] = RespawnAnchorSetSpawn
            this[0x137] = RespawnAnchorAmbient
            this[0x138] = SoulEscapeQuiet
            this[0x139] = SoulEscapeLoud
            this[0x13A] = RecordPigstep
            this[0x13B] = LinkCompassToLodestone
            this[0x13C] = UseSmithingTable
            this[0x13D] = EquipNetherite
            this[0x13E] = AmbientLoopWarpedForest
            this[0x13F] = AmbientLoopSoulsandValley
            this[0x140] = AmbientLoopNetherWastes
            this[0x141] = AmbientLoopBasaltDeltas
            this[0x142] = AmbientLoopCrimsonForest
            this[0x143] = AmbientAdditionWarpedForest
            this[0x144] = AmbientAdditionSoulsandValley
            this[0x145] = AmbientAdditionNetherWastes
            this[0x146] = AmbientAdditionBasaltDeltas
            this[0x147] = AmbientAdditionCrimsonForest
            this[0x148] = SculkSensorPowerOn
            this[0x149] = SculkSensorPowerOff
            this[0x14A] = BucketFillPowderSnow
            this[0x14B] = BucketEmptyPowderSnow
            this[0x14C] = PointedDripstoneCauldronDripLava
            this[0x14D] = PointedDripstoneCauldronDripWater
            this[0x14E] = PointedDripstoneDripLava
            this[0x14F] = PointedDripstonDripWater
            this[0x150] = CaveVinesPickBerries
            this[0x151] = BigDripleafTiltDown
            this[0x152] = BigDripleafTiltUp
            this[0x153] = CopperWaxOn
            this[0x154] = CopperWaxOff
            this[0x155] = Scrape
            this[0x156] = PlayerHurtDrown
            this[0x157] = PlayerHurtOnFire
            this[0x158] = PlayerHurtFreeze
            this[0x159] = UseSpyglass
            this[0x15A] = StopUsingSpyglass
            this[0x15B] = AmethystBlockChime
            this[0x15C] = AmbientScreamer
            this[0x15D] = HurtScreamer
            this[0x15E] = DeathScreamer
            this[0x15F] = MilkScreamer
            this[0x160] = JumpToBlock
            this[0x161] = PreRam
            this[0x162] = PreRamScreamer
            this[0x163] = RamImpact
            this[0x164] = RamImpactScreamer
            this[0x165] = SquidInkSquirt
            this[0x166] = GlowSquidInkSquirt
            this[0x167] = ConvertToStray
            this[0x168] = CakeAddCandle
            this[0x169] = ExtinguishCandle
            this[0x16A] = AmbientCandle
            this[0x16B] = BlockClick
            this[0x16C] = BlockClickFail
            this[0x16D] = SculkCatalystBloom
            this[0x16E] = SculkShriekerShriek
            this[0x16F] = WardenNearbyClose
            this[0x170] = WardenNearbyCloser
            this[0x171] = WardenNearbyClosest
            this[0x172] = WardenSlightlyAngry
        }
    }
}
