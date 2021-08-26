/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor

import com.valaphee.foundry.ecs.entity.EntityType
import com.valaphee.tesseract.world.EntityOfWorld

/**
 * @author Kevin Ludwig
 */
interface ActorType : EntityType

typealias Actor = EntityOfWorld<ActorType>

typealias AnyActorOfWorld = EntityOfWorld<out ActorType>
