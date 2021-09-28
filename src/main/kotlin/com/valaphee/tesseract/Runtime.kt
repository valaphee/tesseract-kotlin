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

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.valaphee.tesseract.data.block.BlockState
import com.valaphee.tesseract.inventory.CreativeInventoryPacket
import com.valaphee.tesseract.inventory.item.Item
import com.valaphee.tesseract.inventory.item.stack.Stack
import com.valaphee.tesseract.net.init.BiomeDefinitionsPacket
import com.valaphee.tesseract.net.init.EntityIdentifiersPacket
import com.valaphee.tesseract.util.LittleEndianByteBufInputStream
import com.valaphee.tesseract.util.getCompoundTag
import com.valaphee.tesseract.util.getInt
import com.valaphee.tesseract.util.getIntOrNull
import com.valaphee.tesseract.util.getJsonArray
import com.valaphee.tesseract.util.getString
import com.valaphee.tesseract.util.getStringOrNull
import com.valaphee.tesseract.util.nbt.NbtInputStream
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.PooledByteBufAllocator
import io.netty.buffer.Unpooled
import java.io.InputStreamReader
import java.lang.invoke.MethodHandles
import java.util.Base64

lateinit var biomeDefinitionsPacket: BiomeDefinitionsPacket
lateinit var entityIdentifiersPacket: EntityIdentifiersPacket
lateinit var creativeInventoryPacket: CreativeInventoryPacket

fun initializeRuntime() {
    val `class` = MethodHandles.lookup().lookupClass()
    val gson = GsonBuilder().create()
    val base64Decoder = Base64.getDecoder()

    run {
        val buffer = PooledByteBufAllocator.DEFAULT.directBuffer()
        try {
            buffer.writeBytes(`class`.getResourceAsStream("/runtime_block_states.dat")!!.readBytes())
            NbtInputStream(ByteBufInputStream(buffer)).use { it.readTag() }?.asCompoundTag()?.get("blocks")?.asListTag()!!.toList().map { it.asCompoundTag()!! }.forEach { BlockState.register(BlockState(it.getString("name"), it.getCompoundTag("states"), it.getInt("version"))) }
        } finally {
            buffer.release()
        }
        BlockState.finish()
    }

    run {
        gson.newJsonReader(InputStreamReader(`class`.getResourceAsStream("/runtime_item_states.json")!!)).use { (gson.fromJson(it, JsonArray::class.java) as JsonArray).map { it.asJsonObject }.forEach { Item.register(it.getString("name"), it.getInt("id")) } }
    }

    run {
        biomeDefinitionsPacket = BiomeDefinitionsPacket(`class`.getResourceAsStream("/biome_definitions.dat")!!.readBytes())
    }

    run {
        entityIdentifiersPacket = EntityIdentifiersPacket(`class`.getResourceAsStream("/entity_identifiers.dat")!!.readBytes())
    }

    run {
        gson.newJsonReader(InputStreamReader(`class`.getResourceAsStream("/creative_items.json")!!)).use {
            val content = mutableListOf<Stack/*<*>*/?>()
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
}
