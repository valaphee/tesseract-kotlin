/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net

import com.valaphee.tesseract.net.init.ServerToClientHandshakePacket
import com.valaphee.tesseract.util.MbedTlsAesCipher
import com.valaphee.tesseract.util.aesCipher
import com.valaphee.tesseract.util.generateSecret
import com.valaphee.tesseract.util.sha256Hasher
import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.channel.ChannelPromise
import io.netty.channel.unix.Buffer
import io.netty.handler.codec.DecoderException
import io.netty.util.ReferenceCountUtil
import java.security.Key
import java.security.KeyPair
import java.security.SecureRandom

/**
 * @author Kevin Ludwig
 */
class EncryptionInitializer(
    serverKeyPair: KeyPair,
    clientPublicKey: Key,
    val gcm: Boolean
) : ChannelInitializer<Channel>() {
    val serverToClientHandshakePacket: ServerToClientHandshakePacket
    private val key: ByteArray
    private val iv: ByteArray
    private lateinit var keyBuffer: ByteBuf

    init {
        val clientSalt = ByteArray(16)
        random.nextBytes(clientSalt)
        val secret = generateSecret(serverKeyPair.private, clientPublicKey)
        val hasher = hasherLocal.get()
        val buffer = PooledByteBufAllocator.DEFAULT.directBuffer(clientSalt.size + secret.size)
        try {
            buffer.writeBytes(clientSalt)
            buffer.writeBytes(secret)
            hasher.update(buffer)
        } finally {
            buffer.release()
        }
        serverToClientHandshakePacket = ServerToClientHandshakePacket(serverKeyPair.public, serverKeyPair.private, clientSalt)
        key = hasher.digest()
        if (gcm) {
            iv = ByteArray(16)
            System.arraycopy(key, 0, iv, 0, 12)
            iv[15] = 2
        } else {
            iv = ByteArray(16)
            System.arraycopy(key, 0, iv, 0, iv.size)
        }
    }

    override fun initChannel(channel: Channel) {
        keyBuffer = channel.alloc().directBuffer(32, 32).writeBytes(key)
        channel.pipeline()
            .addBefore(Compressor.NAME, "va-encryptor", Encryptor())
            .addBefore("va-encryptor", "va-decryptor", Decryptor())
    }

    private inner class Encryptor : ChannelOutboundHandlerAdapter() {
        private val cipher = aesCipher(true, key, iv, gcm)
        private var counter = 0L

        override fun handlerRemoved(context: ChannelHandlerContext) {
            cipher.close()
            super.handlerRemoved(context)
        }

        override fun write(context: ChannelHandlerContext, message: Any, promise: ChannelPromise) {
            if (message is ByteBuf) {
                try {
                    val readerIndex = message.readerIndex()
                    val hasher = hasherLocal.get()
                    val hash = context.alloc().directBuffer()
                    try {
                        hash.writeLongLE(counter++)
                        hash.writeBytes(message)
                        keyBuffer.markReaderIndex()
                        hash.writeBytes(keyBuffer)
                        keyBuffer.resetReaderIndex()
                        hasher.update(hash)
                    } finally {
                        hash.release()
                    }
                    message.readerIndex(readerIndex)
                    message.writeBytes(hasher.digest().copyOf(8))
                    if (cipher is MbedTlsAesCipher) {
                        if (message.hasMemoryAddress()) {
                            val address = message.memoryAddress() + message.readerIndex()
                            cipher.cipher(address, address, message.readableBytes())
                        } else message.nioBuffers().forEach {
                            if (it.remaining() > 0) {
                                val address = Buffer.memoryAddress(it) + it.position()
                                cipher.cipher(address, address, it.remaining())
                            }
                        }
                    } else cipher.cipher(message.duplicate(), message.duplicate().writerIndex(message.readerIndex()))
                    super.write(context, message.retain(), promise)
                } finally {
                    ReferenceCountUtil.safeRelease(message)
                }
            } else super.write(context, message, promise)
        }
    }

    private inner class Decryptor : ChannelInboundHandlerAdapter() {
        private val cipher = aesCipher(false, key, iv, gcm)
        private var count = 0L

        override fun handlerRemoved(context: ChannelHandlerContext) {
            cipher.close()
            super.handlerRemoved(context)
        }

        override fun channelRead(context: ChannelHandlerContext, message: Any) {
            if (message is ByteBuf) {
                val `in`: ByteBuf
                if (1 < message.nioBufferCount()) {
                    `in` = context.alloc().directBuffer(message.readableBytes())
                    `in`.writeBytes(message)
                    message.release()
                } else `in` = message
                try {
                    val inReaderIndex = `in`.readerIndex()
                    val inWriterIndex = `in`.writerIndex()
                    if (cipher is MbedTlsAesCipher) {
                        if (`in`.hasMemoryAddress()) {
                            val inAddress = `in`.memoryAddress() + `in`.readerIndex()
                            cipher.cipher(inAddress, inAddress, `in`.readableBytes())
                        } else `in`.nioBuffers().forEach {
                            if (it.remaining() > 0) {
                                val bufferAddress: Long = Buffer.memoryAddress(it) + it.position()
                                cipher.cipher(bufferAddress, bufferAddress, it.remaining())
                            }
                        }
                    } else cipher.cipher(`in`.duplicate(), `in`.duplicate().writerIndex(`in`.readerIndex()))
                    val hasher = hasherLocal.get()
                    val hash = context.alloc().directBuffer()
                    try {
                        hash.writeLongLE(count++)
                        hash.writeBytes(`in`.readerIndex(inReaderIndex).writerIndex(inWriterIndex - 8))
                        keyBuffer.markReaderIndex()
                        hash.writeBytes(keyBuffer)
                        keyBuffer.resetReaderIndex()
                        hasher.update(hash)
                    } finally {
                        hash.release()
                    }
                    `in`.writerIndex(inWriterIndex)
                    if (hasher.digest().copyOf(8).any { it != `in`.readByte() }) throw DecoderException("Checksum mismatch")
                    super.channelRead(context, `in`.readerIndex(inReaderIndex).writerIndex(inWriterIndex - 8).retain())
                } finally {
                    ReferenceCountUtil.safeRelease(`in`)
                }
            } else super.channelRead(context, message)
        }
    }

    companion object {
        private val hasherLocal = ThreadLocal.withInitial { sha256Hasher() }
        private val random = SecureRandom()
    }
}
