/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.player

import com.valaphee.foundry.ecs.entity.BaseEntityType
import com.valaphee.tesseract.actor.ActorType
import com.valaphee.tesseract.world.EntityOfWorld

/**
 * @author Kevin Ludwig
 */
object PlayerType : BaseEntityType("player"), ActorType

typealias Player = EntityOfWorld<PlayerType>
