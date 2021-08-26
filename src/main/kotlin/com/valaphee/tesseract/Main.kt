/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract

import com.google.inject.Guice
import com.valaphee.tesseract.nbt.NbtInputStream
import com.valaphee.tesseract.nbt.TagType
import com.valaphee.tesseract.util.getCompoundTag
import com.valaphee.tesseract.util.getInt
import com.valaphee.tesseract.util.getString
import com.valaphee.tesseract.world.terrain.block.Block
import com.valaphee.tesseract.world.terrain.block.BlockState
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.PooledByteBufAllocator

fun main() {
    initializeConsole()
    initializeLogging()

    run {
        val buffer = PooledByteBufAllocator.DEFAULT.directBuffer()
        try {
            @Suppress("BlockingMethodInNonBlockingContext")
            buffer.writeBytes(Instance::class.java.getResourceAsStream("runtime_block_states.dat")!!.readBytes())
            @Suppress("BlockingMethodInNonBlockingContext")
            NbtInputStream(ByteBufInputStream(buffer)).use { it.readTag() }?.asCompoundTag()?.get("blocks")?.asListTag()!!.toList().map { it.asCompoundTag()!! }.forEach {
                val blockStateProperties = HashMap<String, Any>()
                var currentRuntimeId = 0
                it.getCompoundTag("states").toMap().forEach { (blockStatePropertyName, blockStatePropertyTag) ->
                    blockStateProperties[blockStatePropertyName] = when (blockStatePropertyTag.type) {
                        TagType.Byte -> blockStatePropertyTag.asNumberTag()!!.toByte() != 0.toByte()
                        TagType.Int -> blockStatePropertyTag.asNumberTag()!!.toInt()
                        TagType.String -> blockStatePropertyTag.asArrayTag()!!.valueToString()
                        else -> throw IndexOutOfBoundsException()
                    }
                }
                BlockState.register(BlockState(it.getString("name"), blockStateProperties, it.getInt("version")).apply { runtimeId = currentRuntimeId++ })
            }
        } finally {
            buffer.release()
        }
        Block.finish()
        BlockState.finish()
    }

    val instance = ServerInstance(Guice.createInjector())
    instance.bind()

    Thread({ Thread.sleep(0x7FFFFFFFFFFFFFFFL) }, "infinisleeper").apply {
        isDaemon = false
        start()
    }
}
