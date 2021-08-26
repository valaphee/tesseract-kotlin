/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.math

import com.valaphee.foundry.math.Int3
import java.util.EnumMap

/**
 * @author Kevin Ludwig
 */
enum class Direction(
    private val noPitch: Boolean = false,
    private val noYaw: Boolean = false,
    private val noRoll: Boolean = false,
    val axis: Int3
) {
    Down(noYaw = true, axis = Int3(0, -1, 0)),
    Up(noYaw = true, axis = Int3(0, 1, 0)),
    North(noRoll = true, axis = Int3(0, 0, -1)),
    South(noRoll = true, axis = Int3(0, 0, 1)),
    West(noPitch = true, axis = Int3(-1, 0, 0)),
    East(noPitch = true, axis = Int3(1, 0, 0));

    fun reverse() = reverse[this]!!

    fun rotatePitch(steps: Int) = if (noPitch) this else when ((if (0 > steps) -steps + 2 else steps) % 4) {
        1 -> clockwisePitch[this]!!
        2 -> reverse[this]!!
        3 -> counterclockwisePitch[this]!!
        else -> this
    }

    fun rotateYaw(steps: Int) = if (noYaw) this else when ((if (0 > steps) -steps + 2 else steps) % 4) {
        1 -> clockwiseYaw[this]!!
        2 -> reverse[this]!!
        3 -> counterclockwiseYaw[this]!!
        else -> this
    }

    fun rotateRoll(steps: Int) = if (noRoll) this else when ((if (0 > steps) -steps + 2 else steps) % 4) {
        1 -> clockwiseRoll[this]!!
        2 -> reverse[this]!!
        3 -> counterclockwiseRoll[this]!!
        else -> this
    }

    companion object {
        private val reverse = EnumMap<Direction, Direction>(Direction::class.java).apply {
            this[North] = South
            this[South] = North
            this[East] = West
            this[West] = East
            this[Up] = Down
            this[Down] = Up
        }
        private val clockwisePitch = EnumMap<Direction, Direction>(Direction::class.java).apply {
            this[North] = Up
            this[Down] = North
            this[South] = Down
            this[Up] = South
        }
        private val counterclockwisePitch = EnumMap<Direction, Direction>(Direction::class.java).apply {
            this[North] = Down
            this[Down] = South
            this[South] = Up
            this[Up] = North
        }
        private val clockwiseYaw = EnumMap<Direction, Direction>(Direction::class.java).apply {
            this[East] = North
            this[South] = East
            this[West] = South
            this[North] = West
        }
        private val counterclockwiseYaw = EnumMap<Direction, Direction>(Direction::class.java).apply {
            this[East] = South
            this[South] = West
            this[West] = North
            this[North] = East
        }
        private val clockwiseRoll = EnumMap<Direction, Direction>(Direction::class.java).apply {
            this[Up] = West
            this[West] = Down
            this[Down] = East
            this[East] = Up
        }
        private val counterclockwiseRoll = EnumMap<Direction, Direction>(Direction::class.java).apply {
            this[Up] = East
            this[West] = Up
            this[Down] = West
            this[East] = Down
        }
    }
}
