/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.inventory

import com.fasterxml.jackson.annotation.JsonIgnore
import com.valaphee.foundry.ecs.BaseAttribute
import com.valaphee.tesseract.actor.AnyActorOfWorld

/**
 * @author Kevin Ludwig
 */
class InventoryHolder(
    @JsonIgnore val inventory: Inventory
) : BaseAttribute()

val AnyActorOfWorld.inventory get() = findAttribute(InventoryHolder::class).inventory
