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

package com.valaphee.tesseract.entity

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.util.Registry

/**
 * @author Kevin Ludwig
 */
class EntityEventPacket(
    val runtimeEntityId: Long,
    val event: Event,
    val data: Int = 0
) : Packet {
    enum class Event {
        JumpAnimation,
        HurtAnimation,
        DeathAnimation,
        AttackStart,
        AttackStop,
        TameFail,
        TameSuccess,
        WolfShakeWet,
        UseItem,
        EatBlockAnimation,
        FishHookBubble,
        FishHookPosition,
        FishHookHook,
        FishHookLured,
        SquidInkCloud,
        ZombieVillagerCure,
        Respawn,
        IronGolemOfferFlower,
        IronGolemWithdrawFlower,
        VillagerHurt,
        LoveParticles,
        VillagerStopTrading,
        WitchSpellParticles,
        FireworkParticles,
        InLoveHearts,
        ExplosionParticles,
        GuardianAttackAnimation,
        WitchDrinkPotion,
        WitchThrowPotion,
        MinecartTntPrimeFuse,
        PrimeCreeper,
        AirSupply,
        PlayerAddXpLevels,
        ElderGuardianCurse,
        AgentArmSwing,
        EnderDragonDeath,
        DustParticles,
        ArrowShake,
        EatingItem,
        BabyAnimalFeed,
        DeathSmokeCloud,
        CompleteTrade,
        RemoveLeash,
        Caravan,
        ConsumeTotem,
        CheckTreasureHunterAchievement,
        Spawn,
        DragonFlaming,
        MergeStack,
        StartSwimming,
        BalloonPop,
        FindTreasureBribe,
        SummonAgent,
        FinishedChargingCrossbow,
        LandedOnGround,
        GrowUp
    }

    override val id get() = 0x1B

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeVarULong(runtimeEntityId)
        buffer.writeByte(events.getId(event))
        buffer.writeVarInt(data)
    }

    override fun handle(handler: PacketHandler) = handler.entityEvent(this)

    override fun toString() = "EntityEventPacket(runtimeEntityId=$runtimeEntityId, event=$event, data=$data)"

    companion object {
        internal val events = Registry<Event>().apply {
            this[0x01] = Event.JumpAnimation
            this[0x02] = Event.HurtAnimation
            this[0x03] = Event.DeathAnimation
            this[0x04] = Event.AttackStart
            this[0x05] = Event.AttackStop
            this[0x06] = Event.TameFail
            this[0x07] = Event.TameSuccess
            this[0x08] = Event.WolfShakeWet
            this[0x09] = Event.UseItem
            this[0x0A] = Event.EatBlockAnimation
            this[0x0B] = Event.FishHookBubble
            this[0x0C] = Event.FishHookPosition
            this[0x0D] = Event.FishHookHook
            this[0x0E] = Event.FishHookLured
            this[0x0F] = Event.SquidInkCloud
            this[0x10] = Event.ZombieVillagerCure
            this[0x12] = Event.Respawn
            this[0x13] = Event.IronGolemOfferFlower
            this[0x14] = Event.IronGolemWithdrawFlower
            this[0x15] = Event.LoveParticles
            this[0x16] = Event.VillagerHurt
            this[0x17] = Event.VillagerStopTrading
            this[0x18] = Event.WitchSpellParticles
            this[0x19] = Event.FireworkParticles
            this[0x1A] = Event.InLoveHearts
            this[0x1B] = Event.ExplosionParticles
            this[0x1C] = Event.GuardianAttackAnimation
            this[0x1D] = Event.WitchDrinkPotion
            this[0x1E] = Event.WitchThrowPotion
            this[0x1F] = Event.MinecartTntPrimeFuse
            this[0x20] = Event.PrimeCreeper
            this[0x21] = Event.AirSupply
            this[0x22] = Event.PlayerAddXpLevels
            this[0x23] = Event.ElderGuardianCurse
            this[0x24] = Event.AgentArmSwing
            this[0x25] = Event.EnderDragonDeath
            this[0x26] = Event.DustParticles
            this[0x27] = Event.ArrowShake
            this[0x39] = Event.EatingItem
            this[0x3C] = Event.BabyAnimalFeed
            this[0x3D] = Event.DeathSmokeCloud
            this[0x3E] = Event.CompleteTrade
            this[0x3F] = Event.RemoveLeash
            this[0x40] = Event.Caravan
            this[0x41] = Event.ConsumeTotem
            this[0x42] = Event.CheckTreasureHunterAchievement
            this[0x43] = Event.Spawn
            this[0x44] = Event.DragonFlaming
            this[0x45] = Event.MergeStack
            this[0x46] = Event.StartSwimming
            this[0x47] = Event.BalloonPop
            this[0x48] = Event.FindTreasureBribe
            this[0x49] = Event.SummonAgent
            this[0x4A] = Event.FinishedChargingCrossbow
            this[0x4B] = Event.LandedOnGround
            this[0x4C] = Event.GrowUp
        }
    }
}

/**
 * @author Kevin Ludwig
 */
object EntityEventPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = EntityEventPacket(buffer.readVarULong(), EntityEventPacket.events[buffer.readUnsignedByte().toInt()], buffer.readVarInt())
}
