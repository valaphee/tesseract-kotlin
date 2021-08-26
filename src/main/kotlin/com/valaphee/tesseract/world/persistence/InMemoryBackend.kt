/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */
package com.valaphee.tesseract.world.persistence

import com.valaphee.tesseract.world.World
import com.valaphee.tesseract.world.sector.Sector
import com.valaphee.tesseract.world.sector.encodePosition
import com.valaphee.tesseract.world.sector.position
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap

/**
 * @author Kevin Ludwig
 */
class InMemoryBackend : Backend {
    private var world: World? = null
    private var sectors = Long2ObjectOpenHashMap<Sector>()

    override fun loadWorld(): World? = world

    override fun saveWorld(world: World) {
        this.world = world
    }

    override fun loadSector(sectorPosition: Long): Sector? = sectors[sectorPosition]

    override fun saveSector(sector: Sector) {
        val (x, y) = sector.position
        sectors[encodePosition(x, y)] = sector
    }
}
