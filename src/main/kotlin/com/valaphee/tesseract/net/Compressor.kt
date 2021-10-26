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

package com.valaphee.tesseract.net

import com.valaphee.tesseract.util.Compressor
import com.valaphee.tesseract.util.ZlibCompressor
import com.valaphee.tesseract.util.compressor
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.CompositeByteBuf
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.channel.ChannelPromise
import io.netty.channel.unix.Buffer
import io.netty.util.ReferenceCountUtil
import network.ycc.raknet.pipeline.FlushTickHandler

/**
 * @author Kevin Ludwig
 */
class Compressor(
    val level: Int = 7
) : ChannelOutboundHandlerAdapter() {
    private lateinit var compressor: Compressor
    private lateinit var temporaryHeader: ByteBuf
    private lateinit var temporaryIn: ByteBuf
    private lateinit var temporaryOut: ByteBuf
    private lateinit var `in`: CompositeByteBuf
    private lateinit var out: CompositeByteBuf
    private var dirty = false

    override fun handlerAdded(context: ChannelHandlerContext) {
        super.handlerAdded(context)
        compressor = compressor(true, true, level)
        val allocator = context.alloc()
        temporaryHeader = allocator.directBuffer(8, 8)
        temporaryIn = allocator.directBuffer(chunkSize, chunkSize)
        temporaryOut = allocator.directBuffer(chunkSize, chunkSize)
        `in` = allocator.compositeDirectBuffer(componentMaximum)
        out = allocator.compositeDirectBuffer(componentMaximum)
    }

    override fun handlerRemoved(context: ChannelHandlerContext) {
        ReferenceCountUtil.safeRelease(out)
        ReferenceCountUtil.safeRelease(`in`)
        ReferenceCountUtil.safeRelease(temporaryIn)
        ReferenceCountUtil.safeRelease(temporaryOut)
        ReferenceCountUtil.safeRelease(temporaryHeader)
        compressor.close()
        super.handlerRemoved(context)
    }

    override fun write(context: ChannelHandlerContext, message: Any, promise: ChannelPromise) {
        if (message is ByteBuf) {
            try {
                promise.trySuccess()
                if (!message.isReadable) return
                if (poolByteMaximum < `in`.readableBytes()) _flush(context)
                if (compressor is ZlibCompressor) {
                    if (message.readableBytes() >= temporaryIn.writableBytes() - 5) {
                        flushTemporaryIn(context.alloc())
                        writeVarInt(message.readableBytes(), temporaryHeader.clear())
                        zlibDeflate(temporaryHeader, context.alloc())
                        zlibDeflate(message, context.alloc())
                    } else {
                        writeVarInt(message.readableBytes(), temporaryIn)
                        temporaryIn.writeBytes(message)
                    }
                } else {
                    val headerBuffer = context.alloc().directBuffer(8, 8)
                    writeVarInt(message.readableBytes(), headerBuffer)
                    `in`.addComponent(true, headerBuffer)
                    `in`.addComponent(true, message.retain())
                }
                dirty = true
                if (poolByteMaximum < out.readableBytes()) _flush(context)
                FlushTickHandler.checkFlushTick(context.channel())
            } finally {
                ReferenceCountUtil.safeRelease(message)
            }
        } else super.write(context, message, promise)
    }

    override fun flush(context: ChannelHandlerContext) {
        if (dirty) _flush(context)
        super.flush(context)
    }

    private fun flushTemporaryIn(allocator: ByteBufAllocator) {
        zlibDeflate(temporaryIn, allocator)
        temporaryIn.clear()
    }

    private fun zlibDeflate(`in`: ByteBuf, allocator: ByteBufAllocator) {
        if (`in`.isReadable) {
            val zlibCompressor: ZlibCompressor = compressor as ZlibCompressor
            if (`in`.hasMemoryAddress()) {
                while (`in`.isReadable) {
                    checkOutFloor(allocator)
                    temporaryOut.writerIndex(temporaryOut.writerIndex() + zlibCompressor.process(`in`.memoryAddress() + `in`.readerIndex(), `in`.readableBytes(), temporaryOut.memoryAddress() + temporaryOut.writerIndex(), temporaryOut.writableBytes()))
                    `in`.readerIndex(`in`.readerIndex() + zlibCompressor.consumed)
                }
            } else {
                val buffers = `in`.nioBuffers()
                var bufferIndex = 0
                while (bufferIndex != buffers.size) {
                    checkOutFloor(allocator)
                    val buffer = buffers[bufferIndex]
                    temporaryOut.writerIndex(temporaryOut.writerIndex() + zlibCompressor.process(Buffer.memoryAddress(buffer) + buffer.position(), buffer.remaining(), temporaryOut.memoryAddress() + temporaryOut.writerIndex(), temporaryOut.writableBytes()))
                    buffer.position(buffer.position() + zlibCompressor.consumed)
                    if (!buffer.hasRemaining()) bufferIndex++
                }
                `in`.readerIndex(`in`.readerIndex() + `in`.readableBytes())
            }
        }
    }

    private fun checkOutFloor(allocator: ByteBufAllocator) {
        if (chunkFloor > temporaryOut.writableBytes()) {
            out.addComponent(true, temporaryOut)
            temporaryOut = allocator.directBuffer(chunkSize, chunkSize)
        }
    }

    private fun _flush(context: ChannelHandlerContext) {
        dirty = false
        val allocator = context.alloc()
        if (compressor is ZlibCompressor) {
            flushTemporaryIn(allocator)
            val zlibCompressor: ZlibCompressor = compressor as ZlibCompressor
            while (!zlibCompressor.isFinished) {
                checkOutFloor(allocator)
                temporaryOut.writerIndex(temporaryOut.writerIndex() + zlibCompressor.process(0, 0, temporaryOut.memoryAddress() + temporaryOut.writerIndex(), temporaryOut.writableBytes()))
            }
            if (temporaryOut.isReadable) {
                out.addComponent(true, temporaryOut)
                temporaryOut = allocator.directBuffer(chunkSize, chunkSize)
            }
            zlibCompressor.reset()
            val out = out
            this.out = allocator.compositeDirectBuffer(componentMaximum)
            context.write(out).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
        } else {
            val out = allocator.directBuffer(`in`.readableBytes() / 4 + 16)
            compressor.process(`in`, out)
            `in`.release()
            `in` = allocator.compositeDirectBuffer(componentMaximum)
            context.write(out).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
        }
    }

    private fun writeVarInt(value: Int, buffer: ByteBuf) {
        @Suppress("NAME_SHADOWING") var value = value
        while (true) {
            if (value and 0x7F.inv() == 0) {
                buffer.writeByte(value)
                return
            } else {
                buffer.writeByte((value and 0x7F) or 0x80)
                value = value ushr 7
            }
        }
    }

    companion object {
        const val NAME = "ta-compressor"
        private const val componentMaximum = 512
        private const val poolByteMaximum = 128 * 1024
        private const val chunkSize = 8192
        private const val chunkFloor = 512
    }
}
