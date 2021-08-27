/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.location

import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.foundry.math.Float2
import com.valaphee.foundry.math.Float3
import com.valaphee.foundry.math.Int3
import com.valaphee.foundry.math.isZero
import com.valaphee.tesseract.actor.ActorType
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.whenTypeIs

/**
 * @author Kevin Ludwig
 */
class MoveRotatePacketizer : BaseFacet<WorldContext, LocationManagerMessage>(LocationManagerMessage::class, Location::class) {
    override suspend fun receive(message: LocationManagerMessage): Response {
        message.entity?.whenTypeIs<ActorType> {
            when (message) {
                is Move -> {
                    val (x, y, z) = it.position
                    MoveRotatePacket(it, Int3.Zero, Float3(if (message.move.x.isZero()) Float.NaN else x, if (message.move.y.isZero()) Float.NaN else y, if (message.move.z.isZero()) Float.NaN else z), Float2(Float.NaN), Float.NaN, true, false, false)
                }
                is MoveRotate -> {
                    val (x, y, z) = it.position
                    val (yaw, pitch) = it.rotation
                    MoveRotatePacket(it, Int3.Zero, Float3(if (message.move.x.isZero()) Float.NaN else x, if (message.move.y.isZero()) Float.NaN else y, if (message.move.z.isZero()) Float.NaN else z), Float2(pitch, yaw), Float.NaN, true, false, false)
                }
                is Rotate -> {
                    val (pitch, yaw) = it.rotation
                    MoveRotatePacket(it, Int3.Zero, Float3(Float.NaN), Float2(pitch, yaw), Float.NaN, true, false, false)
                }
            }
        }

        return Pass
    }
}
