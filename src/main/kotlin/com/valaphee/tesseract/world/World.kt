/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world

import com.valaphee.foundry.ecs.entity.BaseEntityType

/**
 * @author Kevin Ludwig
 */
object WorldType : BaseEntityType("world")

typealias World = EntityOfWorld<WorldType>
