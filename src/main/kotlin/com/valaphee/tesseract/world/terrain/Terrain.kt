/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.terrain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.valaphee.foundry.ecs.BaseAttribute
import com.valaphee.tesseract.world.sector.Sector

/**
 * @author Kevin Ludwig
 */
class Terrain(
    val cartesian: ReadWriteCartesian,
) : BaseAttribute() {
    @JsonIgnore
    val cartesianDelta = CartesianDelta(cartesian)
    @JsonIgnore
    var lastCartesianDeltaMerge = 0
}

val Sector.terrain get() = findAttribute(Terrain::class)
