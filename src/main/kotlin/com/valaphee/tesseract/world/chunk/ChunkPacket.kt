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

import com.valaphee.foundry.math.Int2
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import com.valaphee.tesseract.util.nbt.CompoundTag
import com.valaphee.tesseract.world.chunk.storage.BlockStorage
import com.valaphee.tesseract.world.chunk.storage.readLayer
import com.valaphee.tesseract.world.chunk.storage.readSubChunk

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
class ChunkPacket private constructor(
    val position: Int2,
    val subChunkCount: Int,
    val blockStorage: BlockStorage?,
    val biomes: ByteArray?,
    val blockEntities: Array<CompoundTag>,
    val borderBlocks: Array<Int2>,
    val cache: Boolean,
    val blobIds: LongArray? = null
) : Packet() {
    override val id get() = 0x3A

    constructor(position: Int2, blockStorage: BlockStorage, biomes: ByteArray?, blockEntities: Array<CompoundTag>, borderBlocks: Array<Int2>) : this(position, blockStorage.subChunkCount, blockStorage, biomes, blockEntities, borderBlocks, false, null)

    constructor(position: Int2, subChunkCount: Int, blockEntities: Array<CompoundTag>, borderBlocks: Array<Int2>, blobIds: LongArray) : this(position, subChunkCount, null, null, blockEntities, borderBlocks, true, blobIds)

    override fun write(buffer: PacketBuffer, version: Int) {
        val (x, z) = position
        buffer.writeVarInt(x)
        buffer.writeVarInt(z)
        buffer.writeVarUInt(subChunkCount)
        buffer.writeBoolean(cache)
        if (cache) blobIds!!.let {
            buffer.writeVarUInt(it.size)
            it.forEach(buffer::writeLongLE)
        }
        val dataLengthIndex = buffer.writerIndex()
        buffer.writeZero(PacketBuffer.MaximumVarUIntLength)
        if (!cache) blockStorage!!.let {
            repeat(subChunkCount) { i -> it.subChunks[i].writeToBuffer(buffer) }
            if (cavesAndCliffs) {
                it.biomes!!.forEach { it.writeToBuffer(buffer, true) }; TODO()
            } else buffer.writeBytes(biomes!!)
        }
        buffer.writeByte(borderBlocks.size)
        borderBlocks.forEach { buffer.writeByte((it.x and 0xF) or ((it.y and 0xF) shl 4)) }
        buffer.toNbtOutputStream().use { stream -> blockEntities.forEach { stream.writeTag(it) } }
        buffer.setMaximumLengthVarUInt(dataLengthIndex, buffer.writerIndex() - (dataLengthIndex + PacketBuffer.MaximumVarUIntLength))
    }

    override fun handle(handler: PacketHandler) = handler.chunk(this)

    override fun toString() = "ChunkPacket(position=$position, subChunkCount=$subChunkCount, blockStorage=$blockStorage, blockEntities=${blockEntities.contentToString()}, borderBlocks=${borderBlocks.contentToString()}, cache=$cache, blobIds=${blobIds?.contentToString()})"
}

/**
 * @author Kevin Ludwig
 */
object ChunkPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): ChunkPacket {
        val position = Int2(buffer.readVarInt(), buffer.readVarInt())
        val subChunkCount = buffer.readVarUInt()
        return if (buffer.readBoolean()) {
            val blobIds = LongArray(buffer.readVarUInt()) { buffer.readLongLE() }
            buffer.readVarUInt()
            val borderBlocks = Array(buffer.readUnsignedByte().toInt()) {
                val borderBlock = buffer.readUnsignedByte().toInt()
                Int2(borderBlock and 0xF, borderBlock shr 4)
            }
            val blockEntities = mutableListOf<CompoundTag>()
            buffer.toNbtInputStream().use { while (buffer.isReadable) blockEntities.add(it.readTag()?.asCompoundTag()!!) }
            ChunkPacket(position, subChunkCount, blockEntities.toTypedArray(), borderBlocks, blobIds)
        } else {
            buffer.readVarUInt()
            val blockStorage: BlockStorage
            val biomes: ByteArray?
            if (cavesAndCliffs) {
                blockStorage = BlockStorage(buffer.blockStates.getId(airKey), Array(subChunkCount) { buffer.readSubChunk(buffer.blockStates.getId(airKey)) }, Array(0) { buffer.readLayer(0) })
                biomes = null
                TODO()
            } else {
                blockStorage = BlockStorage(buffer.blockStates.getId(airKey), Array(subChunkCount) { buffer.readSubChunk(buffer.blockStates.getId(airKey)) }, null)
                biomes = ByteArray(BlockStorage.XZSize * BlockStorage.XZSize).also { buffer.readBytes(it) }
            }
            val borderBlocks = Array(buffer.readUnsignedByte().toInt()) {
                val borderBlock = buffer.readUnsignedByte().toInt()
                Int2(borderBlock and 0xF, borderBlock shr 4)
            }
            val blockEntities = mutableListOf<CompoundTag>()
            buffer.toNbtInputStream().use { while (buffer.isReadable) blockEntities.add(it.readTag()?.asCompoundTag()!!) }
            ChunkPacket(position, blockStorage, biomes, blockEntities.toTypedArray(), borderBlocks)
        }
    }

    private const val airKey = "minecraft:air"
}

private const val cavesAndCliffs = false
