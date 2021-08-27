/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract

import BiomeDefinitionsPacket
import CreativeInventoryPacket
import EntityIdentifiersPacket
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.inject.Guice
import com.valaphee.tesseract.item.Item
import com.valaphee.tesseract.item.stack.Stack
import com.valaphee.tesseract.nbt.NbtInputStream
import com.valaphee.tesseract.nbt.TagType
import com.valaphee.tesseract.util.LittleEndianByteBufInputStream
import com.valaphee.tesseract.util.getCompoundTag
import com.valaphee.tesseract.util.getInt
import com.valaphee.tesseract.util.getIntOrNull
import com.valaphee.tesseract.util.getJsonArray
import com.valaphee.tesseract.util.getString
import com.valaphee.tesseract.util.getStringOrNull
import com.valaphee.tesseract.world.chunk.terrain.block.Block
import com.valaphee.tesseract.world.chunk.terrain.block.BlockState
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.PooledByteBufAllocator
import io.netty.buffer.Unpooled
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor
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
        BlockState.finish()
        Block.finish()
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
            .setTracerProvider(
                SdkTracerProvider.builder()
                    .addSpanProcessor(BatchSpanProcessor.builder(JaegerGrpcSpanExporter.builder().build()).build())
                    .build()
            )
            .buildAndRegisterGlobal()
    )
    instance.bind()

    thread(name = "infinisleeper") { Thread.sleep(Long.MAX_VALUE) }
}
