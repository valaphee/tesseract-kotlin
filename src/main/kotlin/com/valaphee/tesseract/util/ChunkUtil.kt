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

package com.valaphee.tesseract.util

import com.valaphee.foundry.math.Int2
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.util.nbt.Tag
import com.valaphee.tesseract.world.chunk.storage.BlockStorage
import io.netty.buffer.ByteBufUtil
import io.netty.buffer.Unpooled

fun chunkData(borderBlocks: List<Int2>, blockEntities: List<Tag>): ByteArray = PacketBuffer(Unpooled.buffer()).use { buffer ->
    buffer.writeByte(borderBlocks.size)
    borderBlocks.forEach { buffer.writeByte((it.x and 0xF) or ((it.y and 0xF) shl 4)) }
    buffer.toNbtOutputStream().use { stream -> blockEntities.forEach { stream.writeTag(it) } }
    ByteBufUtil.getBytes(buffer)
}

fun chunkData(blockStorage: BlockStorage, biomes: ByteArray, borderBlocks: List<Int2>, blockEntities: List<Tag>): ByteArray = PacketBuffer(Unpooled.buffer()).use { buffer ->
    repeat(blockStorage.subChunkCount) { i -> blockStorage.subChunks[i].writeToBuffer(buffer) }
    buffer.writeBytes(biomes)
    buffer.writeByte(borderBlocks.size)
    borderBlocks.forEach { buffer.writeByte((it.x and 0xF) or ((it.y and 0xF) shl 4)) }
    buffer.toNbtOutputStream().use { stream -> blockEntities.forEach { stream.writeTag(it) } }
    ByteBufUtil.getBytes(buffer)
}
