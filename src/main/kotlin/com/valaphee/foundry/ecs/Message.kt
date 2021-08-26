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
interface Message<C : Context> {
    val context: C
    val source: Entity<out EntityType, C>?
}
