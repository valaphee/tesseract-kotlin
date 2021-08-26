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
interface Backend {
    fun loadWorld(): World?

    fun saveWorld(world: World)

    fun loadSector(sectorPosition: Long): Sector?

    fun saveSector(sector: Sector)
}
