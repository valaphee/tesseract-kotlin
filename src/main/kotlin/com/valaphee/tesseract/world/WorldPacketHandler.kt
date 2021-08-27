/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world

import ConnectionAttribute
import Difficulty
import Dimension
import GameMode
import Rank
import RecipesPacket
import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.foundry.math.Int3
import com.valaphee.tesseract.actor.Actor
import com.valaphee.tesseract.actor.location.Location
import com.valaphee.tesseract.actor.location.Teleport
import com.valaphee.tesseract.actor.location.position
import com.valaphee.tesseract.actor.location.rotation
import com.valaphee.tesseract.actor.player.AuthExtra
import com.valaphee.tesseract.actor.player.Player
import com.valaphee.tesseract.actor.player.PlayerLocationPacket
import com.valaphee.tesseract.actor.player.PlayerType
import com.valaphee.tesseract.actor.player.User
import com.valaphee.tesseract.actor.player.View
import com.valaphee.tesseract.actor.player.ViewDistancePacket
import com.valaphee.tesseract.actor.player.ViewDistanceRequestPacket
import com.valaphee.tesseract.biomeDefinitionsPacket
import com.valaphee.tesseract.creativeInventoryPacket
import com.valaphee.tesseract.entityIdentifiersPacket
import com.valaphee.tesseract.item.Item
import com.valaphee.tesseract.net.Connection
import com.valaphee.tesseract.net.GamePublishMode
import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.base.StatusPacket
import com.valaphee.tesseract.world.entity.addEntities
import com.valaphee.tesseract.world.entity.removeEntities

/**
 * @author Kevin Ludwig
 */
class WorldPacketHandler(
    private val context: WorldContext,
    private val connection: Connection,
    private val authExtra: AuthExtra,
    private val user: User,
    private val chunkCacheSupported: Boolean
) : PacketHandler {
    lateinit var player: Player

    override fun initialize() {
        player = context.entityFactory(PlayerType, setOf(Location(Float3(0.0f, 200.0f, 0.0f), Float2.Zero), ConnectionAttribute(connection)))
        context.world.addEntities(context, null, player)

        @Suppress("UNCHECKED_CAST") val actor = player as Actor
        connection.write(
            WorldPacket(
                player.id,
                player.id,
                GameMode.Default,
                actor.position,
                actor.rotation,
                0,
                WorldPacket.BiomeType.Default,
                "",
                Dimension.Overworld,
                WorldPacket.Overworld,
                GameMode.Creative,
                Difficulty.Normal,
                Int3.Zero,
                true,
                -1,
                0,
                false,
                "",
                WorldPacket.EducationEditionOffer.None,
                0.0f,
                0.0f,
                false,
                true,
                true,
                GamePublishMode.Public,
                GamePublishMode.Public,
                true,
                false,
                arrayOf(),
                arrayOf(),
                false,
                false,
                false,
                Rank.Operator,
                4,
                false,
                false,
                false,
                true,
                false,
                false,
                false,
                "*",
                16,
                16,
                true,
                false,
                "",
                "Tesseract",
                "",
                false,
                WorldPacket.AuthoritativeMovement.Client,
                0L,
                0,
                null,
                null,
                null,
                /*Block.all.toTypedArray()*/emptyArray(),
                null,
                Item.all.toTypedArray(),
                "",
                false,
                0,
                false,
                "Tesseract"
            )
        )
        connection.write(biomeDefinitionsPacket)
        connection.write(entityIdentifiersPacket)
        connection.write(creativeInventoryPacket)
        connection.write(RecipesPacket(emptyArray(), emptyArray(), emptyArray(), true))
        connection.write(StatusPacket(StatusPacket.Status.PlayerSpawn))
    }

    override fun destroy() {
        context.world.removeEntities(context, null, player.id)
    }

    override fun other(packet: Packet) = Unit

    override fun playerLocation(packet: PlayerLocationPacket) {
        /*@Suppress("UNCHECKED_CAST") val actor = player as Actor
        val position = actor.position
        val newPosition = packet.position
        val positionalMovement = position != newPosition
        val newRotation = packet.rotation
        val rotationalMovement = actor.rotation != newRotation
        if (positionalMovement && rotationalMovement) player.sendMessage(MoveRotate(context, player, newPosition.toMutableFloat3().sub(position), newRotation))
        else if (positionalMovement) player.sendMessage(Move(context, player, newPosition.toMutableFloat3().sub(position)))
        else if (rotationalMovement) player.sendMessage(Rotate(context, player, newRotation))*/
        player.sendMessage(Teleport(context, player, player, packet.position, packet.rotation))
    }

    override fun viewDistanceRequest(packet: ViewDistanceRequestPacket) {
        player.findFacet(View::class).distance = packet.distance
        connection.write(ViewDistancePacket(packet.distance))
    }
}
