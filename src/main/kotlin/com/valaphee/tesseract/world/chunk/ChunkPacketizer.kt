/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk

import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.tesseract.actor.Actor
import com.valaphee.tesseract.actor.location.position
import com.valaphee.tesseract.actor.player.PlayerType
import com.valaphee.tesseract.actor.player.View
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.WorldPacketHandler
import com.valaphee.tesseract.world.whenTypeIs
import connection

/**
 * @author Kevin Ludwig
 */
class ChunkPacketizer : BaseFacet<WorldContext, ChunkAcquired>(ChunkAcquired::class) {
    override suspend fun receive(message: ChunkAcquired): Response {
        message.source?.whenTypeIs<PlayerType> {
            Thread.sleep(2000L)
            @Suppress("UNCHECKED_CAST")
            it.connection.write(ChunkPublishPacket((it as Actor).position.toInt3(), it.findFacet(View::class).distance shl 4))
            message.chunks.forEach { chunk -> (it.connection.handler as WorldPacketHandler).cacheChunk(chunk) }
        }

        return Pass
    }
}
