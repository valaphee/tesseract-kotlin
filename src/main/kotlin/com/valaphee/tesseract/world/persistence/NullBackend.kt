/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.persistence

import com.valaphee.tesseract.world.World

/**
 * @author Kevin Ludwig
 */
object NullBackend : Backend {
    override fun loadWorld(): World? = null

    override fun saveWorld(world: World) {}
}
