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

package com.valaphee.tesseract.tool

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.google.inject.Guice
import com.valaphee.tesseract.data.DataModule
import com.valaphee.tesseract.initializeConsole
import com.valaphee.tesseract.initializeLogging
import com.valaphee.tesseract.net.Compressor
import com.valaphee.tesseract.net.Connection
import com.valaphee.tesseract.net.Decompressor
import com.valaphee.tesseract.net.PacketDecoder
import com.valaphee.tesseract.net.PacketEncoder
import com.valaphee.tesseract.pack.Content
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFactory
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioDatagramChannel
import network.ycc.raknet.RakNet
import network.ycc.raknet.client.channel.RakNetClientChannel
import network.ycc.raknet.pipeline.UserDataCodec
import org.apache.logging.log4j.LogManager
import java.io.File
import java.net.InetSocketAddress

fun main() {
    initializeConsole()
    initializeLogging()

    //capture()
    encryptPack()
}

fun capture() {
    val injector = Guice.createInjector(DataModule())
    val group = NioEventLoopGroup(0, ThreadFactoryBuilder().setNameFormat("capture-%d").build())
    val userDataCodec = UserDataCodec(0xFE)
    val bootstrap = Bootstrap()
        .group(group)
        .channelFactory(ChannelFactory { RakNetClientChannel(NioDatagramChannel::class.java) })
        .option(RakNet.MTU, 1_464)
        .option(RakNet.PROTOCOL_VERSION, 10)
        .handler(object : ChannelInitializer<Channel>() {
            override fun initChannel(channel: Channel) {
                val connection = Connection()
                connection.setHandler(CapturePacketHandler(connection).apply { injector.injectMembers(this) })
                channel.pipeline()
                    .addLast(UserDataCodec.NAME, userDataCodec)
                    .addLast(Compressor.NAME, Compressor())
                    .addLast(Decompressor.NAME, Decompressor())
                    .addLast(PacketEncoder.NAME, PacketEncoder(false))
                    .addLast(PacketDecoder.NAME, PacketDecoder(false))
                    .addLast(connection)
            }
        })
    val log = LogManager.getLogger()
    bootstrap.connect(InetSocketAddress("127.0.0.1", 19132)).addListener(ChannelFutureListener {
        if (it.isSuccess) log.info("Capturing {}", it.channel().remoteAddress())
        else log.error("Failed to capture {}", it.channel().remoteAddress(), it.cause())
    })
}

fun encryptPack() {
    val path = File("pack")
    val objectMapper = jacksonObjectMapper().apply {
        registerModule(AfterburnerModule())
        propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
        setSerializationInclusion(JsonInclude.Include.NON_DEFAULT)
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        enable(JsonParser.Feature.ALLOW_COMMENTS)
    }
    objectMapper.writeValue(File(path, "contents.json"), Content(path.walkTopDown().filter { it.isFile }.map { Content.Entry(it.path.replace('\\', '/'), null) }.toList()))
}
