/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.player

import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.tesseract.actor.ActorType
import com.valaphee.tesseract.actor.location.Location
import com.valaphee.tesseract.actor.location.LocationManagerMessage
import com.valaphee.tesseract.actor.location.position
import com.valaphee.tesseract.actor.location.rotation
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.whenTypeIs

/**
 * @author Kevin Ludwig
 */
class PlayerLocationPacketizer : BaseFacet<WorldContext, LocationManagerMessage>(LocationManagerMessage::class, Location::class) {
    override suspend fun receive(message: LocationManagerMessage): Response {
        message.entity?.whenTypeIs<ActorType> { PlayerLocationPacket(it as Player, it.position, it.rotation, 0.0f, PlayerLocationPacket.Mode.Normal, true, null, null, 0L) }

        return Pass
    }
}
