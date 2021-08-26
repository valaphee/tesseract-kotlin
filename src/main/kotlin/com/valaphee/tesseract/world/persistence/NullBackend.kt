/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.persistence

import com.valaphee.tesseract.world.World
import com.valaphee.tesseract.world.sector.Sector

/**
 * @author Kevin Ludwig
 */
object NullBackend : Backend {
    override fun loadWorld(): World? = null

    override fun saveWorld(world: World) = Unit

    override fun loadSector(sectorPosition: Long): Sector? = null

    override fun saveSector(sector: Sector) = Unit
}
