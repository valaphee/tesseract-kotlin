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
 *
 */

package com.valaphee.tesseract.world.chunk

import com.valaphee.foundry.math.Int2
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import com.valaphee.tesseract.util.nbt.CompoundTag
import com.valaphee.tesseract.world.chunk.storage.BlockStorage
import com.valaphee.tesseract.world.chunk.storage.readSection
import it.unimi.dsi.fastutil.ints.Int2ShortMap
import it.unimi.dsi.fastutil.ints.Int2ShortMaps

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
class ChunkPacket private constructor(
    val position: Int2,
    val blockStorage: BlockStorage,
    val biomes: ByteArray?,
    val blockExtraData: Int2ShortMap,
    val blockEntities: Array<CompoundTag>,
    val cache: Boolean,
    val blobIds: LongArray? = null
) : Packet {
    override val id get() = 0x3A

    constructor(position: Int2, blockStorage: BlockStorage, biomes: ByteArray, blockExtraData: Int2ShortMap, blockEntities: Array<CompoundTag>) : this(position, blockStorage, biomes, blockExtraData, blockEntities, false, null)

    constructor(position: Int2, blockStorage: BlockStorage, blockExtraData: Int2ShortMap, blockEntities: Array<CompoundTag>, blobIds: LongArray) : this(position, blockStorage, null, blockExtraData, blockEntities, true, blobIds)

    override fun write(buffer: PacketBuffer, version: Int) {
        val (x, z) = position
        buffer.writeVarInt(x)
        buffer.writeVarInt(z)
        var sectionCount = blockStorage.sections.size - 1
        while (sectionCount >= 0 && blockStorage.sections[sectionCount].empty) sectionCount--
        buffer.writeVarUInt(++sectionCount)
        buffer.writeBoolean(cache)
        if (cache) blobIds!!.let {
            buffer.writeVarUInt(it.size)
            it.forEach(buffer::writeLongLE)
        }
        val dataLengthIndex = buffer.writerIndex()
        buffer.writeZero(PacketBuffer.MaximumVarUIntLength)
        if (!cache) {
            repeat(sectionCount) { i -> blockStorage.sections[i].writeToBuffer(buffer) }
            buffer.writeBytes(biomes!!)
        }
        buffer.writeByte(0)
        buffer.writeVarInt(blockExtraData.size)
        blockExtraData.forEach {
            buffer.writeVarInt(it.key)
            buffer.writeShortLE(it.value.toInt())
        }
        buffer.toNbtOutputStream().use { stream -> blockEntities.forEach { stream.writeTag(it) } }
        buffer.setMaximumLengthVarUInt(dataLengthIndex, buffer.writerIndex() - (dataLengthIndex + PacketBuffer.MaximumVarUIntLength))
    }

    override fun handle(handler: PacketHandler) = handler.chunk(this)

    override fun toString() = "ChunkPacket(position=$position, blockStorage=$blockStorage, biomes=${biomes?.contentToString()}, blockExtraData=$blockExtraData, blockEntities=${blockEntities.contentToString()}, cache=$cache, blobIds=${blobIds?.contentToString()})"
}

/**
 * @author Kevin Ludwig
 */
object ChunkPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): ChunkPacket {
        val position = Int2(buffer.readVarInt(), buffer.readVarInt())
        val sectionCount = buffer.readVarUInt()
        val cache = buffer.readBoolean()
        if (cache) {
            val blobIds = LongArray(buffer.readVarUInt()) { buffer.readLongLE() }
            buffer.readVarUInt()
            val blockStorage = BlockStorage(buffer.blockStates.getKey(airKey), sectionCount)
            buffer.readByte()
            /*val blockExtraData = Int2ShortOpenHashMap().apply { repeat(buffer.readVarInt()) { this[buffer.readVarInt()] = buffer.readShortLE() } }*/
            return ChunkPacket(position, blockStorage, Int2ShortMaps.EMPTY_MAP, emptyArray(), blobIds)
        } else {
            val blockStorage = BlockStorage(buffer.blockStates.getKey(airKey), Array(sectionCount) { buffer.readSection(buffer.blockStates.getKey(airKey)) })
            val biomes = buffer.readBytes(ByteArray(BlockStorage.XZSize * BlockStorage.XZSize)).array()
            buffer.readByte()
            /*val blockExtraData = Int2ShortOpenHashMap().apply { repeat(buffer.readVarInt()) { this[buffer.readVarInt()] = buffer.readShortLE() } }*/
            return ChunkPacket(position, blockStorage, biomes, Int2ShortMaps.EMPTY_MAP, emptyArray())
        }
    }

    private const val airKey = "minecraft:air"
}
