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

package com.valaphee.tesseract.capture

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.HashBiMap
import com.valaphee.tesseract.actor.player.AuthExtra
import com.valaphee.tesseract.actor.player.User
import com.valaphee.tesseract.actor.player.appearance.Appearance
import com.valaphee.tesseract.actor.player.appearance.AppearanceImage
import com.valaphee.tesseract.actor.player.appearance.readAppearanceImage
import com.valaphee.tesseract.command.net.LocalPlayerAsInitializedPacket
import com.valaphee.tesseract.data.recipe.ShapedRecipeData
import com.valaphee.tesseract.data.recipe.ShapelessRecipeData
import com.valaphee.tesseract.inventory.CreativeInventoryPacket
import com.valaphee.tesseract.inventory.item.craft.Recipe
import com.valaphee.tesseract.inventory.item.craft.RecipesPacket
import com.valaphee.tesseract.inventory.item.stack.Stack
import com.valaphee.tesseract.net.Connection
import com.valaphee.tesseract.net.EncryptionInitializer
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.base.CacheStatusPacket
import com.valaphee.tesseract.net.init.BiomeDefinitionsPacket
import com.valaphee.tesseract.net.init.ClientToServerHandshakePacket
import com.valaphee.tesseract.net.init.EntityIdentifiersPacket
import com.valaphee.tesseract.net.init.LoginPacket
import com.valaphee.tesseract.net.init.ServerToClientHandshakePacket
import com.valaphee.tesseract.net.init.pack.PacksPacket
import com.valaphee.tesseract.net.init.pack.PacksResponsePacket
import com.valaphee.tesseract.net.init.pack.PacksStackPacket
import com.valaphee.tesseract.util.generateKeyPair
import com.valaphee.tesseract.world.WorldPacket
import io.netty.buffer.Unpooled
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.net.InetSocketAddress
import java.util.Locale
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
class CapturePacketHandler(
    private val connection: Connection,
    private val objectMapper: ObjectMapper
) : PacketHandler {
    private val keyPair = generateKeyPair()

    override fun initialize() {
        connection.write(
            LoginPacket(
                465,
                keyPair.public,
                keyPair.private,
                AuthExtra(
                    UUID.randomUUID(),
                    "0",
                    "Tesseract"
                ),
                User(
                    UUID.randomUUID(),
                    0L,
                    "Tesseract",
                    false,
                    Appearance(
                        "Custom",
                        "",
                        mapOf("geometry" to ("default" to "geometry.humanoid.customSlim")),
                        this::class.java.getResourceAsStream("/alex.png")!!.readAppearanceImage(),
                        emptyList(),
                        AppearanceImage.Empty,
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "#0",
                        emptyList(),
                        emptyList(),
                        false,
                        false,
                        false,
                        false,
                        false
                    ),
                    "",
                    "",
                    UUID.randomUUID().toString(),
                    "",
                    User.OperatingSystem.Unknown,
                    "1.17.30",
                    Locale.US,
                    User.InputMode.KeyboardAndMouse,
                    User.InputMode.KeyboardAndMouse,
                    0,
                    User.UiProfile.Classic,
                    InetSocketAddress("127.0.0.1", 19132)
                ),
                false
            )
        )
    }

    override fun other(packet: Packet) {}

    override fun serverToClientHandshake(packet: ServerToClientHandshakePacket) {
        connection.context.pipeline().addLast(EncryptionInitializer(keyPair, packet.serverPublicKey, true, packet.salt))
        connection.write(ClientToServerHandshakePacket)
    }

    override fun packs(packet: PacksPacket) {
        connection.write(CacheStatusPacket(false))
        connection.write(PacksResponsePacket(PacksResponsePacket.Status.HaveAllPacks, emptyArray()))
    }

    override fun packsStack(packet: PacksStackPacket) {
        connection.write(PacksResponsePacket(PacksResponsePacket.Status.Completed, emptyArray()))
    }

    override fun world(packet: WorldPacket) {
        connection.write(LocalPlayerAsInitializedPacket(packet.runtimeEntityId))

        FileWriter(File("runtime_item_states.json")).use { objectMapper.writeValue(it, packet.items!!.map { it.value.key to it.key }.toMap()) }
    }

    override fun creativeInventory(packet: CreativeInventoryPacket) {
        return

        FileWriter(File("creative_items.json")).use { objectMapper.writeValue(it, packet.content) }
    }

    override fun biomeDefinitions(packet: BiomeDefinitionsPacket) {
        return

        var buffer: PacketBuffer? = null
        try {
            buffer = PacketBuffer(Unpooled.buffer()).also { it.toNbtOutputStream().use { it.writeTag(packet.tag) } }
            val array = ByteArray(buffer.readableBytes())
            buffer.readBytes(array)
            FileOutputStream(File("biome_definitions.dat")).use { it.write(array) }
        } finally {
            buffer?.release()
        }
    }

    override fun entityIdentifiers(packet: EntityIdentifiersPacket) {
        return

        var buffer: PacketBuffer? = null
        try {
            buffer = PacketBuffer(Unpooled.buffer()).also { it.toNbtOutputStream().use { it.writeTag(packet.tag) } }
            val array = ByteArray(buffer.readableBytes())
            buffer.readBytes(array)
            FileOutputStream(File("entity_identifiers.dat")).use { it.write(array) }
        } finally {
            buffer?.release()
        }
    }

    override fun recipes(packet: RecipesPacket) {
        return

        File("data/minecraft/recipes").mkdirs()
        packet.recipes.forEach { recipe ->
            when (recipe.type) {
                Recipe.Type.Shapeless -> FileWriter(File("data/minecraft/recipes/${recipe.name!!.replace("minecraft:", "")}.json")).use {
                    objectMapper.writeValue(
                        it,
                        ShapelessRecipeData(
                            recipe.name,
                            arrayOf(recipe.tag!!),
                            recipe.inputs!!.filterNotNull().toTypedArray(),
                            recipe.priority,
                            recipe.outputs!!.first()!!
                        )
                    )
                }
                Recipe.Type.Shaped -> {
                    val inputs = recipe.inputs!!
                    val ingredients = mutableSetOf<Stack>()
                    inputs.forEach { it?.let { ingredients.add(it) } }
                    val map = HashBiMap.create<Char, Stack>()
                    ingredients.forEachIndexed { i, stack ->
                        map['A' + i] = stack
                    }
                    val width = recipe.width
                    val height = recipe.height
                    val pattern = Array(height) {
                        StringBuilder().apply {
                            repeat(width) { append(' ') }
                        }
                    }
                    val mapInverse = map.inverse()
                    repeat(height) { i -> repeat(width) { j -> pattern[i].setCharAt(j, mapInverse[inputs[i * width + j]] ?: ' ') } }
                    FileWriter(File("data/minecraft/recipes/${recipe.name!!.replace("minecraft:", "")}.json")).use {
                        objectMapper.writeValue(
                            it,
                            ShapedRecipeData(
                                recipe.name,
                                arrayOf(recipe.tag!!),
                                map,
                                pattern.map(StringBuilder::toString).toTypedArray(),
                                recipe.priority,
                                recipe.outputs!!.first()!!
                            )
                        )
                    }
                }
            }
        }
    }
}
