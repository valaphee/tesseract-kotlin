/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.sector

import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.entity.addEntities
import com.valaphee.tesseract.world.entity.removeEntities
import com.valaphee.tesseract.world.terrain.generator.DefaultGenerator
import com.valaphee.tesseract.world.terrain.generator.Generator
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap

/**
 * @author Kevin Ludwig
 */
class SectorManager : BaseFacet<WorldContext, SectorManagerMessage>(SectorManagerMessage::class) {
    private val generator: Generator = DefaultGenerator()
    private val sectors = Long2ObjectOpenHashMap<Sector>()

    override suspend fun receive(message: SectorManagerMessage): Response {
        val context = message.context
        when (message) {
            is SectorAcquire -> context.world.addEntities(context, *message.sectorPositions.filterNot(sectors::containsKey).map { context.backend.loadSector(it) ?: context.entityFactory(SectorType, setOf(Location(decodePosition(it)), generator.generate(decodePosition(it)))).apply { sectors[it] = this } }.toTypedArray())
            is SectorRelease -> context.world.removeEntities(context, *message.sectorPositions.map { sectors.remove(it)?.also { context.backend.saveSector(it) }?.id }.filterNotNull().toLongArray())
        }

        return Pass
    }
}
