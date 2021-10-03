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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.common.collect.HashBiMap
import com.google.inject.Inject
import com.valaphee.tesseract.command.net.CommandsPacket
import com.valaphee.tesseract.command.net.LocalPlayerAsInitializedPacket
import com.valaphee.tesseract.data.block.Block
import com.valaphee.tesseract.data.recipe.ShapedRecipeData
import com.valaphee.tesseract.data.recipe.ShapelessRecipeData
import com.valaphee.tesseract.entity.player.AuthExtra
import com.valaphee.tesseract.entity.player.User
import com.valaphee.tesseract.entity.player.appearance.Appearance
import com.valaphee.tesseract.entity.player.appearance.AppearanceImage
import com.valaphee.tesseract.entity.player.appearance.readAppearanceImage
import com.valaphee.tesseract.inventory.CreativeInventoryPacket
import com.valaphee.tesseract.inventory.item.craft.Recipe
import com.valaphee.tesseract.inventory.item.craft.RecipesPacket
import com.valaphee.tesseract.inventory.item.stack.Stack
import com.valaphee.tesseract.net.Connection
import com.valaphee.tesseract.net.EncryptionInitializer
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.base.BiomeDefinitionsPacket
import com.valaphee.tesseract.net.base.CacheStatusPacket
import com.valaphee.tesseract.net.base.ClientToServerHandshakePacket
import com.valaphee.tesseract.net.base.EntityIdentifiersPacket
import com.valaphee.tesseract.net.base.LoginPacket
import com.valaphee.tesseract.net.base.ServerToClientHandshakePacket
import com.valaphee.tesseract.pack.PacksPacket
import com.valaphee.tesseract.pack.PacksResponsePacket
import com.valaphee.tesseract.pack.PacksStackPacket
import com.valaphee.tesseract.util.Int2ObjectOpenHashBiMap
import com.valaphee.tesseract.util.generateKeyPair
import com.valaphee.tesseract.world.WorldPacket
import io.netty.buffer.Unpooled
import java.io.File
import java.io.FileOutputStream
import java.net.InetSocketAddress
import java.util.Locale
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
class CapturePacketHandler(
    private val connection: Connection
) : PacketHandler {
    @Inject private lateinit var blocks: Map<String, @JvmSuppressWildcards Block>
    @Inject private lateinit var objectMapper: ObjectMapper
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
                        this::class.java.getResourceAsStream("/default_skin.png")!!.readAppearanceImage(),
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
        val blockStates = Int2ObjectOpenHashBiMap<String>()
        var runtimeId = 0
        blocks.values.sortedWith(compareBy { it.key.split(":", limit = 2)[1].lowercase() }).forEach {
            if (it.states.size != 1) blockStates.reverse[it.key] = runtimeId
            it.states.forEach { blockStates[runtimeId++] = it.toString() }
        }
        connection.blockStates = blockStates
        val items = Int2ObjectOpenHashBiMap<String>()
        packet.items!!.forEach { items[it.key] = it.value.key }
        connection.items = items

        connection.write(LocalPlayerAsInitializedPacket(packet.runtimeEntityId))
    }

    override fun creativeInventory(packet: CreativeInventoryPacket) {
        return

        objectMapper.writeValue(File("creative_items.json"), packet.content)
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
        packet.recipes.forEach { if (it.type == Recipe.Type.Multi) println(it.id) }
        return

        File("data/minecraft/recipes").mkdirs()
        packet.recipes.forEach { recipe ->
            when (recipe.type) {
                Recipe.Type.Shapeless -> objectMapper.writeValue(
                    File("data/minecraft/recipes/${recipe.name!!.replace("minecraft:", "")}.json"),
                    ShapelessRecipeData(
                        recipe.name,
                        arrayOf(recipe.tag!!),
                        recipe.inputs!!.filterNotNull().toTypedArray(),
                        recipe.priority,
                        recipe.outputs!!.first()!!
                    )
                )
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
                    objectMapper.writeValue(
                        File("data/minecraft/recipes/${recipe.name!!.replace("minecraft:", "")}.json"),
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

        objectMapper.writeValue(File("potion_mix_recipes.json"), packet.potionMixRecipes)
        objectMapper.writeValue(File("container_mix_recipes.json"), packet.containerMixRecipes)
    }

    override fun commands(packet: CommandsPacket) {
        return

        val objectMapper = jacksonObjectMapper()
        objectMapper.writeValue(File("commands.json"), packet.commands)
        objectMapper.writeValue(File("constraints.json"), packet.constraints)
    }
}
