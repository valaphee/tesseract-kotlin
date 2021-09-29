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

package com.valaphee.tesseract.entity.attribute

/**
 * @author Kevin Ludwig
 */
enum class AttributeField(
    val key: String,
    val minimum: Float,
    val maximum: Float,
    val defaultValue: Float
) {
    Absorption("minecraft:absorption", 0.0f, Float.MAX_VALUE, 0.0f),
    Saturation("minecraft:player.saturation", 0.0f, 20.0f, 5.0f),
    Exhaustion("minecraft:player.exhaustion", 0.0f, 5.0f, 0.41f),
    KnockbackResistance("minecraft:knockback_resistance", 0.0f, 1.0f, 0.0f),
    Health("minecraft:health", 0.0f, 20.0f, 20.0f),
    MovementSpeed("minecraft:movement", 0.0f, Float.MAX_VALUE, 0.1f),
    FollowRange("minecraft:follow_range", 0.0f, 2048.0f, 16.0f),
    Hunger("minecraft:player.hunger", 0.0f, 20.0f, 20.0f),
    AttackDamage("minecraft:attack_damage", 0.0f, Float.MAX_VALUE, 1.0f),
    ExperienceLevel("minecraft:player.level", 0.0f, 24791.0f, 0.0f),
    Experience("minecraft:player.experience", 0.0f, 1.0f, 0.0f),
    Luck("minecraft:luck", -1024.0f, 1024.0f, 0.0f),
    HorseJumpStrength("minecraft:horse.jump_strength", 0.0f, 2.0f, 0.7f);

    fun attributeValue() = AttributeValue(key, minimum, maximum, defaultValue)

    override fun toString() = key
}
