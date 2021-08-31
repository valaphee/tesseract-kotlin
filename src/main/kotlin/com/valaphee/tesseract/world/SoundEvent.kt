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
    RecordPigStep,
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
    Undefined
}