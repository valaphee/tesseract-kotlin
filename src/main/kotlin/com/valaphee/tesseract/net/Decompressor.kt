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
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.unix.Buffer
import io.netty.handler.codec.MessageToMessageDecoder
import io.netty.util.ReferenceCountUtil

/**
 * @author Kevin Ludwig
 */
class Decompressor : MessageToMessageDecoder<ByteBuf>() {
    private lateinit var compressor: Compressor

    override fun handlerAdded(context: ChannelHandlerContext) {
        super.handlerAdded(context)
        compressor = compressor(false, true)
    }

    override fun handlerRemoved(context: ChannelHandlerContext) {
        compressor.close()
        super.handlerRemoved(context)
    }

    override fun decode(context: ChannelHandlerContext, `in`: ByteBuf, out: MutableList<Any>) {
        var out0: ByteBuf? = null
        try {
            if (compressor is ZlibCompressor) {
                val zlibCompressor: ZlibCompressor = compressor as ZlibCompressor
                val allocator = context.alloc()
                val out1 = allocator.compositeDirectBuffer(128).also { out0 = it }
                val buffers = `in`.nioBuffers()
                var bufferIndex = 0
                var temporaryOut = allocator.directBuffer(chunkSize, chunkSize)
                while (!zlibCompressor.isFinished && bufferIndex != buffers.size) {
                    if (chunkFloor > temporaryOut.writableBytes()) {
                        out1.addComponent(true, temporaryOut)
                        temporaryOut = allocator.directBuffer(chunkSize, chunkSize)
                    }
                    val buffer = buffers[bufferIndex.coerceAtMost(buffers.size - 1)]
                    temporaryOut.writerIndex(temporaryOut.writerIndex() + zlibCompressor.process(Buffer.memoryAddress(buffer) + buffer.position(), buffer.remaining(), temporaryOut.memoryAddress() + temporaryOut.writerIndex(), temporaryOut.writableBytes()))
                    buffer.position(buffer.position() + zlibCompressor.consumed)
                    if (!buffer.hasRemaining()) bufferIndex++
                }
                if (temporaryOut.isReadable) out1.addComponent(true, temporaryOut) else temporaryOut.release()
                zlibCompressor.reset()
            } else {
                out0 = context.alloc().ioBuffer(`in`.readableBytes() shl 2)
                compressor.process(`in`, out0!!)
            }
            while (out0!!.isReadable) out.add(out0!!.readRetainedSlice(readVarInt(out0)))
        } finally {
            out0?.let { ReferenceCountUtil.safeRelease(out0) }
        }
    }

    private fun readVarInt(buffer: ByteBuf?): Int {
        var value = 0
        var shift = 0
        while (35 >= shift) {
            val head = buffer!!.readByte().toInt()
            value = value or ((head and 0x7F) shl shift)
            if (0 == head and 0x80) return value
            shift += 7
        }
        throw ArithmeticException("VarInt wider than 35-bit")
    }

    companion object {
        const val NAME = "ta-decompressor"
        private const val chunkSize = 8192
        private const val chunkFloor = 512
    }
}
