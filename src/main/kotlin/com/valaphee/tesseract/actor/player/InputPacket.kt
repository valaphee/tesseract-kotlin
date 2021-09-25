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

package com.valaphee.tesseract.actor.player

import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToServer)
data class InputPacket(
    val rotation: Float2,
    val position: Float3,
    val move: Float2,
    val headRotationYaw: Float,
    val input: Collection<Input>,
    val inputMode: User.InputMode,
    val playMode: PlayMode,
    val virtualRealityGazeDirection: Float3? = null,
    val tick: Long,
    val positionDelta: Float3
) : Packet {
    enum class PlayMode {
        Normal,
        Teaser,
        Screen,
        Viewer,
        VirtualReality,
        PlacementLivingRoom,
        ExitLevel,
        ExitLevelLivingRoom
    }

    enum class Input {
        Ascend,
        Descend,
        NorthJump,
        JumpDown,
        SprintDown,
        ChangeHeight,
        Jumping,
        AutoJumpingInWater,
        Sneaking,
        SneakDown,
        Up,
        Down,
        Left,
        Right,
        UpLeft,
        UpRight,
        WantUp,
        WantDown,
        WantDownSlow,
        WantUpSlow,
        Sprinting,
        AscendScaffolding,
        DescendScaffolding,
        SneakToggleDown,
        PersistSneak,
        StartSprinting,
        StopSprinting,
        StartSneaking,
        StopSneaking,
        StartSwimming,
        StopSwimming,
        StartJumping,
        StartGliding,
        StopGliding,
        PerformItemInteraction,
        PerformBlockActions,
        PerformItemStackRequest
    }

    override val id get() = 0x90

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeFloat2(rotation)
        buffer.writeFloat3(position)
        buffer.writeFloat2(move)
        buffer.writeFloatLE(headRotationYaw)
        buffer.writeVarULongFlags(input)
        buffer.writeVarUInt(inputMode.ordinal)
        buffer.writeVarUInt(playMode.ordinal)
        if (playMode == PlayMode.VirtualReality) buffer.writeFloat3(virtualRealityGazeDirection!!)
        if (version >= 419) {
            buffer.writeVarULong(tick)
            buffer.writeFloat3(positionDelta)
        }
    }

    override fun handle(handler: PacketHandler) = handler.input(this)
}

/**
 * @author Kevin Ludwig
 */
object InputPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): InputPacket {
        val rotation = buffer.readFloat2()
        val position = buffer.readFloat3()
        val move = buffer.readFloat2()
        val headRotationYaw = buffer.readFloatLE()
        val input = buffer.readVarULongFlags<InputPacket.Input>()
        val inputMode = User.InputMode.values()[buffer.readVarUInt()]
        val playMode = InputPacket.PlayMode.values()[buffer.readVarUInt()]
        val virtualRealityGazeDirection = if (playMode == InputPacket.PlayMode.VirtualReality) buffer.readFloat3() else null
        val tick: Long
        val delta: Float3
        if (version >= 419) {
            tick = buffer.readVarULong()
            delta = buffer.readFloat3()
        } else {
            tick = 0L
            delta = Float3.Zero
        }
        return InputPacket(rotation, position, move, headRotationYaw, input, inputMode, playMode, virtualRealityGazeDirection, tick, delta)
    }
}
