/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.foundry.ecs

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.concurrent.atomic.AtomicLong

/**
 * @author Kevin Ludwig
 */
interface Attribute {
    @get:JsonIgnore
    val id: Long
}

/**
 * @author Kevin Ludwig
 */
abstract class BaseAttribute(
    override val id: Long = nextId.getAndIncrement()
) : Attribute {
    companion object {
        internal val nextId = AtomicLong()
    }
}
