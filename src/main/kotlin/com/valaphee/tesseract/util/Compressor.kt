/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.util

import io.netty.buffer.ByteBuf
import java.io.Closeable
import java.util.zip.Deflater
import java.util.zip.Inflater

/**
 * @author Kevin Ludwig
 */
interface Compressor : Closeable {
    fun process(`in`: ByteBuf, out: ByteBuf)
}

fun compressor(compress: Boolean, raw: Boolean, level: Int = 7): Compressor = JavaZipCompressor(compress, raw, level)

/**
 * @author Kevin Ludwig
 */
private class JavaZipCompressor(
    private val compress: Boolean,
    raw: Boolean,
    level: Int
) : Compressor {
    private val buffer = ByteArray(8192)
    private val deflater: Deflater?
    private val inflater: Inflater?

    init {
        if (compress) {
            deflater = Deflater(level, raw)
            inflater = null
        } else {
            deflater = null
            inflater = Inflater(raw)
        }
    }

    override fun process(`in`: ByteBuf, out: ByteBuf) {
        val inData = ByteArray(`in`.readableBytes())
        `in`.readBytes(inData)
        if (compress) {
            deflater!!.setInput(inData)
            deflater.finish()
            while (!deflater.finished()) out.writeBytes(buffer, 0, deflater.deflate(buffer))
            deflater.reset()
        } else {
            inflater!!.setInput(inData)
            while (!inflater.finished() && inflater.totalIn < inData.size) out.writeBytes(buffer, 0, inflater.inflate(buffer))
            inflater.reset()
        }
    }

    override fun close() {
        if (compress) deflater!!.end() else inflater!!.end()
    }
}

class ZlibCompressor(
    private val compress: Boolean,
    raw: Boolean,
    level: Int
) : Compressor {
    private val impl = ZlibCompressorImpl()
    private var zStream = impl.init(compress, level, raw)

    override fun process(`in`: ByteBuf, out: ByteBuf) {
        while (!impl.finished && (compress || `in`.isReadable)) {
            out.ensureWritable(8192)
            val processed = impl.process(zStream, `in`.memoryAddress() + `in`.readerIndex(), `in`.readableBytes(), out.memoryAddress() + out.writerIndex(), out.writableBytes(), compress)
            `in`.readerIndex(`in`.readerIndex() + impl.consumed)
            out.writerIndex(out.writerIndex() + processed)
        }
        reset()
    }

    fun process(`in`: Long, inLength: Int, out: Long, outLength: Int) = impl.process(zStream, `in`, inLength, out, outLength, compress)

    val consumed get() = impl.consumed

    val isFinished get() = impl.finished

    fun reset() {
        impl.reset(zStream, compress)
        impl.consumed = 0
        impl.finished = false
    }

    override fun close() {
        impl.free(zStream, compress)
        zStream = 0
        impl.consumed = 0
        impl.finished = false
    }
}
