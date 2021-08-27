/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk.terrain

/**
 * @author Kevin Ludwig
 */
interface ReadWriteCartesian : ReadOnlyCartesian {
    operator fun set(x: Int, y: Int, z: Int, value: Int)

    fun setIfEmpty(x: Int, y: Int, z: Int, value: Int): Int
}
