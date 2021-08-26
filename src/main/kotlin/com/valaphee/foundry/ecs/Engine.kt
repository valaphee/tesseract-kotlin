/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.foundry.ecs

import com.valaphee.foundry.ecs.entity.Entity
import com.valaphee.foundry.ecs.entity.EntityType

/**
 * @author Kevin Ludwig
 */
interface Engine<C : Context> {
    fun addEntity(entity: Entity<out EntityType, C>)

    fun removeEntity(entity: Entity<out EntityType, C>)

    fun findEntityOrNull(id: Long): Entity<out EntityType, C>?

    fun run(context: C)
}
