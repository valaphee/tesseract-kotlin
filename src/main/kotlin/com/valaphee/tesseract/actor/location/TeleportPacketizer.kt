/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.location

import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.tesseract.actor.ActorType
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.whenTypeIs

/**
 * @author Kevin Ludwig
 */
class TeleportPacketizer : BaseFacet<WorldContext, LocationManagerMessage>(LocationManagerMessage::class, Location::class) {
    override suspend fun receive(message: LocationManagerMessage): Response {
        message.entity?.whenTypeIs<ActorType> { if (message is Teleport) TeleportPacket(it, it.position, it.rotation, Float.NaN, true, false) }

        return Pass
    }
}
