/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.terrain.generator

import com.valaphee.foundry.math.Int2
import com.valaphee.tesseract.world.terrain.OcTree
import com.valaphee.tesseract.world.terrain.Terrain

/**
 * @author Kevin Ludwig
 */
class SandboxGenerator : Generator {
    override fun generate(position: Int2) = Terrain(OcTree())
}
