/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.terrain.generator

import com.valaphee.foundry.math.Int2
import com.valaphee.tesseract.world.terrain.Terrain

/**
 * @author Kevin Ludwig
 */
interface Generator {
    fun generate(position: Int2): Terrain
}
