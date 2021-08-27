/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.location

import com.valaphee.foundry.ecs.BaseAttribute
import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.actor.Actor

/**
 * @author Kevin Ludwig
 */
class Location(
    var position: Float3,
    var rotation: Float2
) : BaseAttribute()

var Actor.position
    get() = findAttribute(Location::class).position
    set(value) {
        findAttribute(Location::class).also { it.position = value }
    }

var Actor.rotation
    get() = findAttribute(Location::class).rotation
    set(value) {
        findAttribute(Location::class).also { it.rotation = value }
    }
