/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net

import com.valaphee.tesseract.util.lazyToString
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.ReadTimeoutException
import org.apache.logging.log4j.LogManager
import java.net.InetSocketAddress

/**
 * @author Kevin Ludwig
 */
class Connection : SimpleChannelInboundHandler<Packet>() {
    lateinit var context: ChannelHandlerContext
        private set
    lateinit var handler: PacketHandler
        private set
    private var notClosed = true

    override fun channelActive(context: ChannelHandlerContext) {
        super.channelActive(context)
        this.context = context
        handler.initialize()
    }

    override fun channelInactive(context: ChannelHandlerContext) {
        handler.destroy()
        super.channelInactive(context)
    }

    override fun channelWritabilityChanged(context: ChannelHandlerContext) {
        handler.writabilityChanged()
        super.channelWritabilityChanged(context)
    }

    override fun exceptionCaught(context: ChannelHandlerContext, cause: Throwable) {
        if (context.channel().isActive) {
            when (cause) {
                is ReadTimeoutException -> {
                    log.warn("{}: Read timed out", handler)
                    context.close()
                }
                else -> log.error("{}: Unhandled exception caught", handler, cause)
            }
            try {
                handler.exceptionCaught(cause)
            } catch (thrown: Throwable) {
                log.error("{}: Exception processing exception", handler, cause)
            }
        }
    }

    override fun channelRead0(context: ChannelHandlerContext, packet: Packet) {
        if (notClosed) {
            log.debug("In: {}", lazyToString(packet::toString))
            packet.handle(handler)
        }
    }

    val address get() = context.channel().remoteAddress() as InetSocketAddress

    fun setHandler(handler: PacketHandler) {
        if (this::handler.isInitialized) {
            this.handler.destroy()
            this.handler = handler
            this.handler.initialize()
        } else this.handler = handler
    }

    fun write(packet: Packet) {
        if (notClosed) {
            log.debug("Out: {}", lazyToString(packet::toString))
            context.write(packet, context.voidPromise())
        }
    }

    @JvmOverloads
    fun close(packet: Packet? = null) {
        if (notClosed) {
            notClosed = false
            if (packet != null && context.channel().isActive) {
                log.debug("Out: {}", lazyToString(packet::toString))
                context.writeAndFlush(packet).addListeners(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE, ChannelFutureListener.CLOSE)
            } else {
                context.flush()
                context.close()
            }
        }
    }

    fun closeInternal() {
        notClosed = false
    }

    companion object {
        private val log = LogManager.getLogger(Connection::class.java)
    }
}
