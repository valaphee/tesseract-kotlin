/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.provider

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Inject
import com.google.inject.name.Named
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
class TesseractProvider @Inject constructor(
    @Named("world") private val objectMapper: ObjectMapper,
) : Provider {
    private val database: DB = JniDBFactory.factory.open(File("world"), Options().createIfMissing(true).blockSize(64 * 1024))

    override fun loadWorld(): World? = null

    override fun saveWorld(world: World) = Unit

    override fun loadChunk(chunkPosition: Long) = database.get(ByteBuffer.wrap(ByteArray(8)).apply { putLong(chunkPosition) }.array())?.let { objectMapper.readValue<Chunk>(it) }

    override fun saveChunks(chunks: Iterable<Chunk>) {
        database.createWriteBatch().use { batch ->
            chunks.forEach {
                val (x, y) = it.position
                batch.put(ByteBuffer.wrap(ByteArray(8)).apply { putLong(encodePosition(x, y)) }.array(), objectMapper.writeValueAsBytes(it))
            }
            database.write(batch)
        }
    }
}
