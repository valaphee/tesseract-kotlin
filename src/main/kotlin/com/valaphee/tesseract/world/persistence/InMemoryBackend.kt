/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.persistence

import com.valaphee.tesseract.world.World

/**
 * @author Kevin Ludwig
 */
class InMemoryBackend : Backend {
    private var world: World? = null

    override fun loadWorld(): World? = world

    override fun saveWorld(world: World) {
        this.world = world
    }
}
