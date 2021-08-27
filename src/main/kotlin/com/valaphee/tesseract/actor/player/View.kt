/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.player

import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.entity.Entity
import com.valaphee.foundry.ecs.entity.EntityType
import com.valaphee.foundry.ecs.system.BaseActor
import com.valaphee.foundry.math.Int2
import com.valaphee.tesseract.actor.ActorType
import com.valaphee.tesseract.actor.location.LocationManagerMessage
import com.valaphee.tesseract.actor.location.position
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.ChunkAcquire
import com.valaphee.tesseract.world.chunk.ChunkRelease
import com.valaphee.tesseract.world.chunk.encodePosition
import com.valaphee.tesseract.world.whenTypeIs
import it.unimi.dsi.fastutil.longs.LongOpenHashSet

/**
 * @author Kevin Ludwig
 */
class View : BaseActor<WorldContext, LocationManagerMessage>(LocationManagerMessage::class) {
    private lateinit var lastSectorPosition: Int2
    private val acquiredSectors = LongOpenHashSet()

    override suspend fun update(entity: Entity<out EntityType, WorldContext>, context: WorldContext): Boolean {
        if (this::lastSectorPosition.isInitialized) return false

        entity.whenTypeIs<ActorType> {
            val (x, _, z) = it.position.toInt3()
            val sectorX = x shr 8
            val sectorZ = z shr 8
            val sectorPosition = Int2(x shr 8, z shr 8)

            lastSectorPosition = sectorPosition

            val sectorsInDistance = LongOpenHashSet()
            for (sectorXr in -distance..distance) {
                for (sectorZr in -distance..distance) {
                    sectorsInDistance.add(encodePosition(sectorX + sectorXr, sectorZ + sectorZr))
                }
            }

            val sectorsToRelease = acquiredSectors.filterNot(sectorsInDistance::contains)
            val sectorsToAcquire = sectorsInDistance.filterNot(acquiredSectors::contains)
            if (sectorsToRelease.isNotEmpty()) {
                acquiredSectors.removeAll(sectorsToRelease)
                context.world.receiveMessage(ChunkRelease(context, entity, sectorsToRelease.toLongArray()))
            }
            if (sectorsToAcquire.isNotEmpty()) {
                acquiredSectors.addAll(sectorsToAcquire)
                context.world.receiveMessage(ChunkAcquire(context, entity, sectorsToAcquire.toLongArray()))
            }
        }

        return false
    }

    override suspend fun receive(message: LocationManagerMessage): Response {
        message.entity?.whenTypeIs<ActorType> {
            val (x, _, z) = it.position.toInt3()
            val sectorX = x shr 8
            val sectorZ = z shr 8
            val sectorPosition = Int2(x shr 8, z shr 8)
            if (lastSectorPosition != sectorPosition) {
                lastSectorPosition = sectorPosition

                val sectorsInDistance = LongOpenHashSet()
                for (sectorXr in -distance..distance) {
                    for (sectorZr in -distance..distance) {
                        sectorsInDistance.add(encodePosition(sectorX + sectorXr, sectorZ + sectorZr))
                    }
                }

                val sectorsToRelease = acquiredSectors.filterNot(sectorsInDistance::contains)
                val sectorsToAcquire = sectorsInDistance.filterNot(acquiredSectors::contains)
                if (sectorsToRelease.isNotEmpty()) {
                    acquiredSectors.removeAll(sectorsToRelease)
                    message.context.world.receiveMessage(ChunkRelease(message.context, message.source, sectorsToRelease.toLongArray()))
                }
                if (sectorsToAcquire.isNotEmpty()) {
                    acquiredSectors.addAll(sectorsToAcquire)
                    message.context.world.receiveMessage(ChunkAcquire(message.context, message.source, sectorsToAcquire.toLongArray()))
                }
            }
        }

        return Pass
    }

    companion object {
        const val distance = 1
    }
}
