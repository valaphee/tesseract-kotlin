/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.persistence

import com.aayushatharva.brotli4j.decoder.BrotliInputStream
import com.aayushatharva.brotli4j.encoder.BrotliOutputStream
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.valaphee.tesseract.world.World
import com.valaphee.tesseract.world.sector.Sector
import com.valaphee.tesseract.world.sector.encodePosition
import com.valaphee.tesseract.world.sector.position
import io.netty.handler.codec.compression.Brotli
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.Base64

/**
 * @author Kevin Ludwig
 */
class BrotliFileStorageBackend(
    private val objectMapper: ObjectMapper,
    private val path: File
) : Backend {
    init {
        Brotli.ensureAvailability()

        path.mkdirs()
    }

    override fun loadWorld(): World? {
        val file = File(path, ".dat")
        return if (!file.exists()) null else objectMapper.readValue(BrotliInputStream(FileInputStream(file)))
    }

    override fun saveWorld(world: World) {
        val file = File(path, ".dat")
        if (!file.exists()) file.createNewFile()
        objectMapper.writeValue(BrotliOutputStream(FileOutputStream(file)), world)
    }

    override fun loadSector(sectorPosition: Long): Sector? {
        val file = File(path, "${base64Encoder.encodeToString(ByteBuffer.wrap(ByteArray(8)).apply { putLong(sectorPosition) }.array())}.dat")
        return if (!file.exists()) null else objectMapper.readValue(BrotliInputStream(FileInputStream(file)))
    }

    override fun saveSector(sector: Sector) {
        val (x, y) = sector.position
        val file = File(path, "${base64Encoder.encodeToString(ByteBuffer.wrap(ByteArray(8)).apply { putLong(encodePosition(x, y)) }.array())}.dat")
        if (!file.exists()) file.createNewFile()
        objectMapper.writeValue(BrotliOutputStream(FileOutputStream(file)), sector)
    }

    companion object {
        private val base64Encoder = Base64.getUrlEncoder()
    }
}
