/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.foundry.ecs.system

import com.valaphee.foundry.ecs.Attribute
import com.valaphee.foundry.ecs.Context
import kotlin.reflect.KClass

/**
 * @author Kevin Ludwig
 */
interface System<C : Context> {
    val id: Long
    val mandatoryAttributes: Set<KClass<out Attribute>>
}
