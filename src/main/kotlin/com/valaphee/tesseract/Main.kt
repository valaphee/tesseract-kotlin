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

package com.valaphee.tesseract

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.name.Names
import com.valaphee.tesseract.actor.ActorTypeRegistry
import com.valaphee.tesseract.command.CommandManager
import com.valaphee.tesseract.inventory.CreativeInventoryPacket
import com.valaphee.tesseract.inventory.item.Item
import com.valaphee.tesseract.inventory.item.Items
import com.valaphee.tesseract.inventory.item.stack.Stack
import com.valaphee.tesseract.net.init.BiomeDefinitionsPacket
import com.valaphee.tesseract.net.init.EntityIdentifiersPacket
import com.valaphee.tesseract.util.LittleEndianByteBufInputStream
import com.valaphee.tesseract.util.LittleEndianVarIntByteBufInputStream
import com.valaphee.tesseract.util.getCompoundTag
import com.valaphee.tesseract.util.getInt
import com.valaphee.tesseract.util.getIntOrNull
import com.valaphee.tesseract.util.getJsonArray
import com.valaphee.tesseract.util.getListTag
import com.valaphee.tesseract.util.getString
import com.valaphee.tesseract.util.getStringOrNull
import com.valaphee.tesseract.util.jackson.PatternDeserializer
import com.valaphee.tesseract.util.jackson.PatternSerializer
import com.valaphee.tesseract.util.nbt.NbtInputStream
import com.valaphee.tesseract.world.chunk.terrain.block.Block
import com.valaphee.tesseract.world.chunk.terrain.block.BlockState
import com.valaphee.tesseract.world.chunk.terrain.block.Blocks
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.PooledByteBufAllocator
import io.netty.buffer.Unpooled
import org.apache.logging.log4j.LogManager
import java.io.IOException
import java.io.InputStreamReader
import java.lang.invoke.MethodHandles
import java.util.Base64
import java.util.regex.Pattern
import kotlin.concurrent.thread

lateinit var biomeDefinitionsPacket: BiomeDefinitionsPacket
lateinit var entityIdentifiersPacket: EntityIdentifiersPacket
lateinit var creativeInventoryPacket: CreativeInventoryPacket

fun main(arguments: Array<String>) {
    val argument = Argument().apply { if (!parse(arguments)) return }
    println("""
        ________                                           _____ 
        ___  __/____________________________________ ________  /_
        __  /  _  _ \_  ___/_  ___/  _ \_  ___/  __ `/  ___/  __/
        _  /   /  __/(__  )_(__  )/  __/  /   / /_/ // /__ / /_  
        /_/    \___//____/ /____/ \___//_/    \__,_/ \___/ \__/  
    """.trimIndent())

    initializeConsole()
    initializeLogging()

    val log = LogManager.getLogger("Registry")
    log.info("Populating registries...")

    val clazz = MethodHandles.lookup().lookupClass()
    val gson = GsonBuilder().create()
    val base64Decoder = Base64.getDecoder()

    run {
        val buffer = PooledByteBufAllocator.DEFAULT.directBuffer()
        try {
            buffer.writeBytes(clazz.getResourceAsStream("/runtime_block_states.dat")!!.readBytes())
            NbtInputStream(ByteBufInputStream(buffer)).use { it.readTag() }?.asCompoundTag()?.get("blocks")?.asListTag()!!.toList().map { it.asCompoundTag()!! }.forEach { BlockState.register(BlockState(it.getString("name"), it.getCompoundTag("states"), it.getInt("version"))) }
        } finally {
            buffer.release()
        }
        BlockState.finish()
        Block.finish()
        Blocks.populate()
        log.info("Blocks found: {}", Block.all.size)
        log.info("Block states found: {}", BlockState.all.size)
    }

    run {
        gson.newJsonReader(InputStreamReader(clazz.getResourceAsStream("/runtime_item_states.json")!!)).use { (gson.fromJson(it, JsonArray::class.java) as JsonArray).map { it.asJsonObject }.forEach { Item.register(it.getString("name"), it.getInt("id")) } }
        log.info("Items found: {}", Item.all.size)
        Items.populate()
    }

    run {
        val data = clazz.getResourceAsStream("/biome_definitions.dat")!!.readBytes()
        biomeDefinitionsPacket = BiomeDefinitionsPacket(data)
    }

    run {
        val buffer = PooledByteBufAllocator.DEFAULT.directBuffer()
        try {
            val data = clazz.getResourceAsStream("/entity_identifiers.dat")!!.readBytes()
            buffer.writeBytes(data)
            NbtInputStream(LittleEndianVarIntByteBufInputStream(buffer)).use { it.readTag() }!!.asCompoundTag()!!.getListTag("idlist").toList().map { it.asCompoundTag()!! }.forEach { ActorTypeRegistry.register(it.getString("id"), it.getInt("rid")) }
            entityIdentifiersPacket = EntityIdentifiersPacket(data)
        } finally {
            buffer.release()
        }
        log.info("Actor types found: {}", ActorTypeRegistry.size)
    }

    run {
        gson.newJsonReader(InputStreamReader(clazz.getResourceAsStream("/creative_items.json")!!)).use {
            val content = mutableListOf<Stack<*>>()
            (gson.fromJson(it, JsonObject::class.java) as JsonObject).getJsonArray("items").map { it.asJsonObject }.forEach {
                Item.byKeyOrNull(it.getString("id"))?.let { item ->
                    content += Stack(Item.byIdOrNull(item.id)!!, it.getIntOrNull("damage") ?: 0, 1, it.getStringOrNull("nbt_b64")?.let {
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

    val guice = Guice.createInjector(object : AbstractModule() {
        override fun configure() {
            bind(ObjectMapper::class.java).annotatedWith(Names.named("config")).toInstance(ObjectMapper().apply {
                registerKotlinModule()
                registerModule(
                    SimpleModule()
                        .addSerializer(Pattern::class.java, PatternSerializer)
                        .addDeserializer(Pattern::class.java, PatternDeserializer)
                )
                propertyNamingStrategy = PropertyNamingStrategies.KEBAB_CASE
                enable(SerializationFeature.INDENT_OUTPUT)
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            })
            bind(Argument::class.java).toInstance(argument)
        }
    })

    val serverInstance = ServerInstance(guice)
    serverInstance.bind()

    val commandManager = guice.getInstance(CommandManager::class.java)
    if (ansi) reader.prompt = "tesseract> "
    thread(isDaemon = true, name = "console-reader") {
        while (true) {
            try {
                val message = reader.readLine(null, null, null)
                if (message.trim { it <= ' ' }.isNotEmpty()) commandManager.dispatch(message)
            } catch (_: IOException) {
            } catch (_: IndexOutOfBoundsException) {
            }
        }
    }
}
