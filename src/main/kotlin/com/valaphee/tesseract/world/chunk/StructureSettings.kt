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

package com.valaphee.tesseract.world.chunk

import com.valaphee.foundry.math.Float3
import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.net.PacketBuffer

/**
 * @author Kevin Ludwig
 */
data class StructureSettings(
    val paletteName: String,
    val ignoringEntities: Boolean,
    val ignoringBlocks: Boolean,
    val size: Int3,
    val offset: Int3,
    val lastEditedByEntityId: Long,
    val rotation: Rotation,
    val mirror: Mirror,
    val animationMode: AnimationMode,
    val animationTime: Float,
    val integrityValue: Float,
    val integritySeed: Int,
    val pivot: Float3
) {
    enum class Rotation {
        None, Clockwise90, Reverse, Counterclockwise90
    }

    enum class Mirror {
        None, X, Z, XZ
    }

    enum class AnimationMode {
        None, Layer, Blocks
    }
}

fun PacketBuffer.readStructureSettingsPre440() = StructureSettings(readString(), readBoolean(), readBoolean(), readInt3UnsignedY(), readInt3UnsignedY(), readVarLong(), StructureSettings.Rotation.values()[readUnsignedByte().toInt()], StructureSettings.Mirror.values()[readUnsignedByte().toInt()], StructureSettings.AnimationMode.None, 0.0f, readFloatLE(), readIntLE(), readFloat3())

fun PacketBuffer.readStructureSettings() = StructureSettings(readString(), readBoolean(), readBoolean(), readInt3UnsignedY(), readInt3UnsignedY(), readVarLong(), StructureSettings.Rotation.values()[readUnsignedByte().toInt()], StructureSettings.Mirror.values()[readUnsignedByte().toInt()], StructureSettings.AnimationMode.values()[readUnsignedByte().toInt()], readFloatLE(), readFloatLE(), readIntLE(), readFloat3())

fun PacketBuffer.writeStructureSettingsPre440(value: StructureSettings) {
    writeString(value.paletteName)
    writeBoolean(value.ignoringEntities)
    writeBoolean(value.ignoringBlocks)
    writeInt3UnsignedY(value.size)
    writeInt3UnsignedY(value.offset)
    writeVarLong(value.lastEditedByEntityId)
    writeByte(value.rotation.ordinal)
    writeByte(value.mirror.ordinal)
    writeFloatLE(value.integrityValue)
    writeIntLE(value.integritySeed)
    writeFloat3(value.pivot)
}

fun PacketBuffer.writeStructureSettings(value: StructureSettings) {
    writeString(value.paletteName)
    writeBoolean(value.ignoringEntities)
    writeBoolean(value.ignoringBlocks)
    writeInt3UnsignedY(value.size)
    writeInt3UnsignedY(value.offset)
    writeVarLong(value.lastEditedByEntityId)
    writeByte(value.rotation.ordinal)
    writeByte(value.mirror.ordinal)
    writeByte(value.animationMode.ordinal)
    writeFloatLE(value.animationTime)
    writeFloatLE(value.integrityValue)
    writeIntLE(value.integritySeed)
    writeFloat3(value.pivot)
}
