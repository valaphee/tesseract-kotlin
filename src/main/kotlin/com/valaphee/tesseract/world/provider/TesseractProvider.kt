/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.provider

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.valaphee.tesseract.world.World
import com.valaphee.tesseract.world.chunk.Chunk
import com.valaphee.tesseract.world.chunk.encodePosition
import com.valaphee.tesseract.world.chunk.position
import org.fusesource.leveldbjni.JniDBFactory
import org.iq80.leveldb.DB
import org.iq80.leveldb.Options
import java.io.File
import java.nio.ByteBuffer

/**
 * @author Kevin Ludwig
 */
class TesseractProvider(
    private val objectMapper: ObjectMapper,
    path: File
) : Provider {
    private val db: DB = JniDBFactory.factory.open(path, Options().createIfMissing(true))

    override fun loadWorld(): World? = null

    override fun saveWorld(world: World) = Unit

    override fun loadChunk(chunkPosition: Long) = db.get(ByteBuffer.wrap(ByteArray(8)).apply { putLong(chunkPosition) }.array())?.let { objectMapper.readValue<Chunk>(it) }

    override fun saveChunk(chunk: Chunk) {
        val (x, y) = chunk.position
        db.put(ByteBuffer.wrap(ByteArray(8)).apply { putLong(encodePosition(x, y)) }.array(), objectMapper.writeValueAsBytes(chunk))
    }
}
