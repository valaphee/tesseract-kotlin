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

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import io.netty.util.ByteProcessor
import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.nio.channels.GatheringByteChannel
import java.nio.channels.ScatteringByteChannel
import java.nio.charset.Charset

/**
 * @author Kevin Ludwig
 */
open class ByteBufWrapper(
    val buffer: ByteBuf
) : ByteBuf(), Closeable {
    override fun refCnt() = buffer.refCnt()

    override fun retain(increment: Int): ByteBuf = buffer.retain(increment)

    override fun retain(): ByteBuf = buffer.retain()

    override fun touch(): ByteBuf = buffer.touch()

    override fun touch(hint: Any?): ByteBuf = buffer.touch(hint)

    override fun release() = buffer.release()

    override fun release(decrement: Int) = buffer.release(decrement)

    override fun compareTo(other: ByteBuf?) = buffer.compareTo(other)

    override fun capacity() = buffer.capacity()

    override fun capacity(newCapacity: Int): ByteBuf = buffer.capacity(newCapacity)

    override fun maxCapacity() = buffer.maxCapacity()

    override fun alloc(): ByteBufAllocator = buffer.alloc()

    @Suppress("DEPRECATION")
    override fun order(): ByteOrder = buffer.order()

    @Suppress("DEPRECATION")
    override fun order(endianness: ByteOrder): ByteBuf = buffer.order(endianness)

    override fun unwrap(): ByteBuf = buffer.unwrap()

    override fun isDirect() = buffer.isDirect

    override fun isReadOnly() = buffer.isReadOnly

    override fun asReadOnly(): ByteBuf = buffer.asReadOnly()

    override fun readerIndex() = buffer.readerIndex()

    override fun readerIndex(readerIndex: Int): ByteBuf = buffer.readerIndex(readerIndex)

    override fun writerIndex() = buffer.writerIndex()

    override fun writerIndex(writerIndex: Int): ByteBuf = buffer.writerIndex(writerIndex)

    override fun setIndex(readerIndex: Int, writerIndex: Int): ByteBuf = buffer.setIndex(readerIndex, writerIndex)

    override fun readableBytes() = buffer.readableBytes()

    override fun writableBytes() = buffer.writableBytes()

    override fun maxWritableBytes() = buffer.maxWritableBytes()

    override fun isReadable() = buffer.isReadable

    override fun isReadable(size: Int) = buffer.isReadable(size)

    override fun isWritable() = buffer.isWritable

    override fun isWritable(size: Int) = buffer.isWritable(size)

    override fun clear(): ByteBuf = buffer.clear()

    override fun markReaderIndex(): ByteBuf = buffer.markReaderIndex()

    override fun resetReaderIndex(): ByteBuf = buffer.resetReaderIndex()

    override fun markWriterIndex(): ByteBuf = buffer.markWriterIndex()

    override fun resetWriterIndex(): ByteBuf = buffer.resetWriterIndex()

    override fun discardReadBytes(): ByteBuf = buffer.discardReadBytes()

    override fun discardSomeReadBytes(): ByteBuf = buffer.discardSomeReadBytes()

    override fun ensureWritable(minWritableBytes: Int): ByteBuf = buffer.ensureWritable(minWritableBytes)

    override fun ensureWritable(minWritableBytes: Int, force: Boolean) = buffer.ensureWritable(minWritableBytes, force)

    override fun getBoolean(index: Int) = buffer.getBoolean(index)

    override fun getByte(index: Int) = buffer.getByte(index)

    override fun getUnsignedByte(index: Int) = buffer.getUnsignedByte(index)

    override fun getShort(index: Int) = buffer.getShort(index)

    override fun getShortLE(index: Int) = buffer.getShortLE(index)

    override fun getUnsignedShort(index: Int) = buffer.getUnsignedShort(index)

    override fun getUnsignedShortLE(index: Int) = buffer.getUnsignedShortLE(index)

    override fun getMedium(index: Int) = buffer.getMedium(index)

    override fun getMediumLE(index: Int) = buffer.getMediumLE(index)

    override fun getUnsignedMedium(index: Int) = buffer.getUnsignedMedium(index)

    override fun getUnsignedMediumLE(index: Int) = buffer.getUnsignedMediumLE(index)

    override fun getInt(index: Int) = buffer.getInt(index)

    override fun getIntLE(index: Int) = buffer.getIntLE(index)

    override fun getUnsignedInt(index: Int) = buffer.getUnsignedInt(index)

    override fun getUnsignedIntLE(index: Int) = buffer.getUnsignedIntLE(index)

    override fun getLong(index: Int) = buffer.getLong(index)

    override fun getLongLE(index: Int) = buffer.getLongLE(index)

    override fun getChar(index: Int) = buffer.getChar(index)

    override fun getFloat(index: Int) = buffer.getFloat(index)

    override fun getDouble(index: Int) = buffer.getDouble(index)

    override fun getBytes(index: Int, dst: ByteBuf): ByteBuf = buffer.getBytes(index, dst)

    override fun getBytes(index: Int, dst: ByteBuf, length: Int): ByteBuf = buffer.getBytes(index, dst, length)

    override fun getBytes(index: Int, dst: ByteBuf, dstIndex: Int, length: Int): ByteBuf = buffer.getBytes(index, dst, dstIndex, length)

    override fun getBytes(index: Int, dst: ByteArray): ByteBuf = buffer.getBytes(index, dst)

    override fun getBytes(index: Int, dst: ByteArray, dstIndex: Int, length: Int): ByteBuf = buffer.getBytes(index, dst, dstIndex, length)

    override fun getBytes(index: Int, dst: ByteBuffer): ByteBuf = buffer.getBytes(index, dst)

    override fun getBytes(index: Int, out: OutputStream, length: Int): ByteBuf = buffer.getBytes(index, out, length)

    override fun getBytes(index: Int, out: GatheringByteChannel, length: Int) = buffer.getBytes(index, out, length)

    override fun getBytes(index: Int, out: FileChannel, position: Long, length: Int) = buffer.getBytes(index, out, position, length)

    override fun getCharSequence(index: Int, length: Int, charset: Charset): CharSequence = buffer.getCharSequence(index, length, charset)

    override fun setBoolean(index: Int, value: Boolean): ByteBuf = buffer.setBoolean(index, value)

    override fun setByte(index: Int, value: Int): ByteBuf = buffer.setByte(index, value)

    override fun setShort(index: Int, value: Int): ByteBuf = buffer.setShort(index, value)

    override fun setShortLE(index: Int, value: Int): ByteBuf = buffer.setShortLE(index, value)

    override fun setMedium(index: Int, value: Int): ByteBuf = buffer.setMedium(index, value)

    override fun setMediumLE(index: Int, value: Int): ByteBuf = buffer.setMediumLE(index, value)

    override fun setInt(index: Int, value: Int): ByteBuf = buffer.setInt(index, value)

    override fun setIntLE(index: Int, value: Int): ByteBuf = buffer.setIntLE(index, value)

    override fun setLong(index: Int, value: Long): ByteBuf = buffer.setLong(index, value)

    override fun setLongLE(index: Int, value: Long): ByteBuf = buffer.setLongLE(index, value)

    override fun setChar(index: Int, value: Int): ByteBuf = buffer.setChar(index, value)

    override fun setFloat(index: Int, value: Float): ByteBuf = buffer.setFloat(index, value)

    override fun setDouble(index: Int, value: Double): ByteBuf = buffer.setDouble(index, value)

    override fun setBytes(index: Int, src: ByteBuf): ByteBuf = buffer.setBytes(index, src)

    override fun setBytes(index: Int, src: ByteBuf, length: Int): ByteBuf = buffer.setBytes(index, src, length)

    override fun setBytes(index: Int, src: ByteBuf, srcIndex: Int, length: Int): ByteBuf = buffer.setBytes(index, src, srcIndex, length)

    override fun setBytes(index: Int, src: ByteArray): ByteBuf = buffer.setBytes(index, src)

    override fun setBytes(index: Int, src: ByteArray, srcIndex: Int, length: Int): ByteBuf = buffer.setBytes(index, src, srcIndex, length)

    override fun setBytes(index: Int, src: ByteBuffer): ByteBuf = buffer.setBytes(index, src)

    override fun setBytes(index: Int, `in`: InputStream, length: Int) = buffer.setBytes(index, `in`, length)

    override fun setBytes(index: Int, `in`: ScatteringByteChannel, length: Int) = buffer.setBytes(index, `in`, length)

    override fun setBytes(index: Int, `in`: FileChannel, position: Long, length: Int) = buffer.setBytes(index, `in`, position, length)

    override fun setZero(index: Int, length: Int): ByteBuf = buffer.setZero(index, length)

    override fun setCharSequence(index: Int, sequence: CharSequence, charset: Charset) = buffer.setCharSequence(index, sequence, charset)

    override fun readBoolean() = buffer.readBoolean()

    override fun readByte() = buffer.readByte()

    override fun readUnsignedByte() = buffer.readUnsignedByte()

    override fun readShort() = buffer.readShort()

    override fun readShortLE() = buffer.readShortLE()

    override fun readUnsignedShort() = buffer.readUnsignedShort()

    override fun readUnsignedShortLE() = buffer.readUnsignedShortLE()

    override fun readMedium() = buffer.readMedium()

    override fun readMediumLE() = buffer.readMediumLE()

    override fun readUnsignedMedium() = buffer.readUnsignedMedium()

    override fun readUnsignedMediumLE() = buffer.readUnsignedMediumLE()

    override fun readInt() = buffer.readInt()

    override fun readIntLE() = buffer.readIntLE()

    override fun readUnsignedInt() = buffer.readUnsignedInt()

    override fun readUnsignedIntLE() = buffer.readUnsignedIntLE()

    override fun readLong() = buffer.readLong()

    override fun readLongLE() = buffer.readLongLE()

    override fun readChar() = buffer.readChar()

    override fun readFloat() = buffer.readFloat()

    override fun readDouble() = buffer.readDouble()

    override fun readBytes(length: Int): ByteBuf = buffer.readBytes(length)

    override fun readBytes(dst: ByteBuf): ByteBuf = buffer.readBytes(dst)

    override fun readBytes(dst: ByteBuf, length: Int): ByteBuf = buffer.readBytes(dst, length)

    override fun readBytes(dst: ByteBuf, dstIndex: Int, length: Int): ByteBuf = buffer.readBytes(dst, dstIndex, length)

    override fun readBytes(dst: ByteArray): ByteBuf = buffer.readBytes(dst)

    override fun readBytes(dst: ByteArray, dstIndex: Int, length: Int): ByteBuf = buffer.readBytes(dst, dstIndex, length)

    override fun readBytes(dst: ByteBuffer): ByteBuf = buffer.readBytes(dst)

    override fun readBytes(out: OutputStream, length: Int): ByteBuf = buffer.readBytes(out, length)

    override fun readBytes(out: GatheringByteChannel, length: Int) = buffer.readBytes(out, length)

    override fun readBytes(out: FileChannel, position: Long, length: Int) = buffer.readBytes(out, position, length)

    override fun readSlice(length: Int): ByteBuf = buffer.readSlice(length)

    override fun readRetainedSlice(length: Int): ByteBuf = buffer.readRetainedSlice(length)

    override fun readCharSequence(length: Int, charset: Charset?): CharSequence = buffer.readCharSequence(length, charset)

    override fun skipBytes(length: Int): ByteBuf = buffer.skipBytes(length)

    override fun writeBoolean(value: Boolean): ByteBuf = buffer.writeBoolean(value)

    override fun writeByte(value: Int): ByteBuf = buffer.writeByte(value)

    override fun writeShort(value: Int): ByteBuf = buffer.writeShort(value)

    override fun writeShortLE(value: Int): ByteBuf = buffer.writeShortLE(value)

    override fun writeMedium(value: Int): ByteBuf = buffer.writeMedium(value)

    override fun writeMediumLE(value: Int): ByteBuf = buffer.writeMediumLE(value)

    override fun writeInt(value: Int): ByteBuf = buffer.writeInt(value)

    override fun writeIntLE(value: Int): ByteBuf = buffer.writeIntLE(value)

    override fun writeLong(value: Long): ByteBuf = buffer.writeLong(value)

    override fun writeLongLE(value: Long): ByteBuf = buffer.writeLongLE(value)

    override fun writeChar(value: Int): ByteBuf = buffer.writeChar(value)

    override fun writeFloat(value: Float): ByteBuf = buffer.writeFloat(value)

    override fun writeDouble(value: Double): ByteBuf = buffer.writeDouble(value)

    override fun writeBytes(src: ByteBuf): ByteBuf = buffer.writeBytes(src)

    override fun writeBytes(src: ByteBuf, length: Int): ByteBuf = buffer.writeBytes(src, length)

    override fun writeBytes(src: ByteBuf?, srcIndex: Int, length: Int): ByteBuf = buffer.writeBytes(src, srcIndex, length)

    override fun writeBytes(src: ByteArray): ByteBuf = buffer.writeBytes(src)

    override fun writeBytes(src: ByteArray, srcIndex: Int, length: Int): ByteBuf = buffer.writeBytes(src, srcIndex, length)

    override fun writeBytes(src: ByteBuffer): ByteBuf = buffer.writeBytes(src)

    override fun writeBytes(`in`: InputStream, length: Int) = buffer.writeBytes(`in`, length)

    override fun writeBytes(`in`: ScatteringByteChannel, length: Int) = buffer.writeBytes(`in`, length)

    override fun writeBytes(`in`: FileChannel, position: Long, length: Int) = buffer.writeBytes(`in`, position, length)

    override fun writeZero(length: Int): ByteBuf = buffer.writeZero(length)

    override fun writeCharSequence(sequence: CharSequence, charset: Charset) = buffer.writeCharSequence(sequence, charset)

    override fun indexOf(fromIndex: Int, toIndex: Int, value: Byte) = buffer.indexOf(fromIndex, toIndex, value)

    override fun bytesBefore(value: Byte) = buffer.bytesBefore(value)

    override fun bytesBefore(length: Int, value: Byte) = buffer.bytesBefore(length, value)

    override fun bytesBefore(index: Int, length: Int, value: Byte) = buffer.bytesBefore(index, length, value)

    override fun forEachByte(processor: ByteProcessor) = buffer.forEachByte(processor)

    override fun forEachByte(index: Int, length: Int, processor: ByteProcessor?) = buffer.forEachByte(index, length, processor)

    override fun forEachByteDesc(processor: ByteProcessor) = buffer.forEachByteDesc(processor)

    override fun forEachByteDesc(index: Int, length: Int, processor: ByteProcessor) = buffer.forEachByteDesc(index, length, processor)

    override fun copy(): ByteBuf = buffer.copy()

    override fun copy(index: Int, length: Int): ByteBuf = buffer.copy(index, length)

    override fun slice(): ByteBuf = buffer.slice()

    override fun slice(index: Int, length: Int): ByteBuf = buffer.slice(index, length)

    override fun retainedSlice(): ByteBuf = buffer.retainedSlice()

    override fun retainedSlice(index: Int, length: Int): ByteBuf = buffer.retainedSlice(index, length)

    override fun duplicate(): ByteBuf = buffer.duplicate()

    override fun retainedDuplicate(): ByteBuf = buffer.retainedDuplicate()

    override fun nioBufferCount() = buffer.nioBufferCount()

    override fun nioBuffer(): ByteBuffer = buffer.nioBuffer()

    override fun nioBuffer(index: Int, length: Int): ByteBuffer = buffer.nioBuffer(index, length)

    override fun internalNioBuffer(index: Int, length: Int): ByteBuffer = buffer.internalNioBuffer(index, length)

    override fun nioBuffers(): Array<ByteBuffer> = buffer.nioBuffers()

    override fun nioBuffers(index: Int, length: Int): Array<ByteBuffer> = buffer.nioBuffers(index, length)

    override fun hasArray() = buffer.hasArray()

    override fun array(): ByteArray = buffer.array()

    override fun arrayOffset() = buffer.arrayOffset()

    override fun hasMemoryAddress() = buffer.hasMemoryAddress()

    override fun memoryAddress() = buffer.memoryAddress()

    override fun isContiguous() = buffer.isContiguous

    override fun close() {
        buffer.release()
    }

    override fun equals(other: Any?) = buffer == other

    override fun hashCode() = buffer.hashCode()

    override fun toString(charset: Charset): String = buffer.toString(charset)

    override fun toString(index: Int, length: Int, charset: Charset): String = buffer.toString(index, length, charset)

    override fun toString() = buffer.toString()
}
