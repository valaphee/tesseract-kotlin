/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.inject.Guice
import com.valaphee.tesseract.inventory.CreativeInventoryPacket
import com.valaphee.tesseract.item.Item
import com.valaphee.tesseract.item.stack.Stack
import com.valaphee.tesseract.util.nbt.NbtInputStream
import com.valaphee.tesseract.net.init.BiomeDefinitionsPacket
import com.valaphee.tesseract.net.init.EntityIdentifiersPacket
import com.valaphee.tesseract.util.LittleEndianByteBufInputStream
import com.valaphee.tesseract.util.getCompoundTag
import com.valaphee.tesseract.util.getInt
import com.valaphee.tesseract.util.getIntOrNull
import com.valaphee.tesseract.util.getJsonArray
import com.valaphee.tesseract.util.getString
import com.valaphee.tesseract.util.getStringOrNull
import com.valaphee.tesseract.world.chunk.terrain.block.Block
import com.valaphee.tesseract.world.chunk.terrain.block.BlockState
import com.valaphee.tesseract.world.chunk.terrain.block.Blocks
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.PooledByteBufAllocator
import io.netty.buffer.Unpooled
import io.opentelemetry.sdk.OpenTelemetrySdk
import org.mozilla.javascript.Context
import java.io.IOException
import java.io.InputStreamReader
import java.lang.invoke.MethodHandles
import java.util.Base64
import kotlin.concurrent.thread

lateinit var biomeDefinitionsPacket: BiomeDefinitionsPacket
lateinit var entityIdentifiersPacket: EntityIdentifiersPacket
lateinit var creativeInventoryPacket: CreativeInventoryPacket

fun main() {
    initializeConsole()
    initializeLogging()

    val clazz = MethodHandles.lookup().lookupClass()
    val gson = GsonBuilder().create()
    val base64Decoder = Base64.getDecoder()

    run {
        val buffer = PooledByteBufAllocator.DEFAULT.directBuffer()
        try {
            @Suppress("BlockingMethodInNonBlockingContext")
            buffer.writeBytes(clazz.getResourceAsStream("/runtime_block_states.dat")!!.readBytes())
            @Suppress("BlockingMethodInNonBlockingContext")
            NbtInputStream(ByteBufInputStream(buffer)).use { it.readTag() }?.asCompoundTag()?.get("blocks")?.asListTag()!!.toList().map { it.asCompoundTag()!! }.forEach { BlockState.register(BlockState(it.getString("name"), it.getCompoundTag("states"), it.getInt("version"))) }
        } finally {
            buffer.release()
        }
        BlockState.finish()
        Block.finish()
        Blocks.populate()
    }

    run {
        @Suppress("BlockingMethodInNonBlockingContext")
        gson.newJsonReader(InputStreamReader(clazz.getResourceAsStream("/runtime_item_states.json")!!)).use { (gson.fromJson(it, JsonArray::class.java) as JsonArray).map { it.asJsonObject }.forEach { Item.register(it.getString("name"), it.getInt("id")) } }
    }

    run {
        val data = clazz.getResourceAsStream("/biome_definitions.dat")!!.readBytes()
        @Suppress("BlockingMethodInNonBlockingContext")
        biomeDefinitionsPacket = BiomeDefinitionsPacket(data)
    }

    run {
        val data = clazz.getResourceAsStream("/entity_identifiers.dat")!!.readBytes()
        @Suppress("BlockingMethodInNonBlockingContext")
        entityIdentifiersPacket = EntityIdentifiersPacket(data)
    }

    run {
        @Suppress("BlockingMethodInNonBlockingContext")
        gson.newJsonReader(InputStreamReader(clazz.getResourceAsStream("/creative_items.json")!!)).use {
            val content = ArrayList<Stack<*>>()
            (gson.fromJson(it, JsonObject::class.java) as JsonObject).getJsonArray("items").map { it.asJsonObject }.forEach {
                Item.byKey(it.getString("id"))?.let { item ->
                    content += Stack(Item.byId(item.id)!!, it.getIntOrNull("damage") ?: 0, 1, it.getStringOrNull("nbt_b64")?.let {
                        val buffer = Unpooled.wrappedBuffer(base64Decoder.decode(it))
                        val tag = NbtInputStream(LittleEndianByteBufInputStream(buffer)).use { it.readTag() }?.asCompoundTag()
                        buffer.release()
                        tag
                    }, blockRuntimeId = it.getIntOrNull("blockRuntimeId") ?: 0)
                }
            }
            creativeInventoryPacket = CreativeInventoryPacket(content.toTypedArray())
        }
    }

    val instance = ServerInstance(
        Guice.createInjector(),
        OpenTelemetrySdk.builder()
            /*.setTracerProvider(
                SdkTracerProvider.builder()
                    .addSpanProcessor(BatchSpanProcessor.builder(JaegerGrpcSpanExporter.builder().build()).build())
                    .build()
            )*/
            .buildAndRegisterGlobal()
    )
    instance.bind()

    reader.prompt = "js> "
    thread(isDaemon = true, name = "console-reader") {
        val context = Context.enter()
        try {
            val scope = context.initStandardObjects()
            while (true) {
                try {
                    val message = reader.readLine(null, null, null)
                    if (message.trim { it <= ' ' }.isNotEmpty()) {
                        try {
                            context.evaluateString(scope, message, "<console>", 1, null)
                        } catch (ex: Throwable) {
                            println(ex.message)
                        }
                    }
                } catch (_: IOException) {
                } catch (_: IndexOutOfBoundsException) {
                }
            }
        } finally {
            Context.exit()
        }
    }

    thread(name = "infinisleeper") { Thread.sleep(Long.MAX_VALUE) }
}
