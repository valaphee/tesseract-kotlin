/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.chunk

import com.valaphee.foundry.ecs.Pass
import com.valaphee.foundry.ecs.Response
import com.valaphee.foundry.ecs.system.BaseFacet
import com.valaphee.tesseract.actor.player.PlayerType
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.entity.EntityAdd
import com.valaphee.tesseract.world.entity.EntityManagerMessage
import com.valaphee.tesseract.world.filterType
import com.valaphee.tesseract.world.whenTypeIs

/**
 * @author Kevin Ludwig
 */
class ChunkPacketizer : BaseFacet<WorldContext, EntityManagerMessage>(EntityManagerMessage::class) {
    override suspend fun receive(message: EntityManagerMessage): Response {
        message.source?.whenTypeIs<PlayerType> {
            when (message) {
                is EntityAdd -> {
                    message.entities.filterType<ChunkType>().forEach {}
                }
            }
        }

        return Pass
    }
}
