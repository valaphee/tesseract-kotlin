/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain

/**
 * @author Kevin Ludwig
 */
interface ReadOnlyCartesian {
    operator fun get(x: Int, y: Int, z: Int): Int
}
