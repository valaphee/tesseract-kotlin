/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.persistence

import com.valaphee.tesseract.world.World

/**
 * @author Kevin Ludwig
 */
interface Backend {
    fun loadWorld(): World?

    fun saveWorld(world: World)
}
