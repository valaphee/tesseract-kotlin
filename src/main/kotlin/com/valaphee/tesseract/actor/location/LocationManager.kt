/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.location

import com.google.inject.Singleton
import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.foundry.math.Float3
import com.valaphee.tesseract.actor.ActorType
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.whenTypeIs

/**
 * @author Kevin Ludwig
 */
@Singleton
class LocationManager : BaseFacet<WorldContext, LocationManagerMessage>(LocationManagerMessage::class, Location::class) {
    override suspend fun receive(message: LocationManagerMessage): Response {
        message.entity?.whenTypeIs<ActorType> {
            when (message) {
                is Move -> it.position += message.move.toMutableFloat3().rotate(it.rotation.x, Float3.YAxis)
                is MoveRotate -> {
                    it.rotation = message.rotation
                    it.position += message.move.toMutableFloat3().rotate(it.rotation.x, Float3.YAxis)
                }
                is Rotate -> it.rotation = message.rotation
                is Teleport -> {
                    it.position = message.position
                    it.rotation = message.rotation
                }
            }
        }

        return Pass
    }
}
