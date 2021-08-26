/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.foundry.ecs.message

import com.valaphee.foundry.ecs.Context
import com.valaphee.foundry.ecs.Message
import com.valaphee.foundry.ecs.entity.Entity
import com.valaphee.foundry.ecs.entity.EntityType
import com.valaphee.foundry.ecs.system.Facet

/**
 * @author Kevin Ludwig
 */
data class StateChanged<C : Context>(
    override val context: C,
    override val source: Entity<out EntityType, C>?,
    val oldState: Facet<C, out Message<C>>,
    val newState: Facet<C, out Message<C>>
) : Message<C>
