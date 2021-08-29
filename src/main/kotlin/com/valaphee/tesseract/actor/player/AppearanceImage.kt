/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.player

import com.google.gson.JsonObject
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.util.getString
import java.awt.Transparency
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ColorModel
import java.awt.image.ComponentColorModel
import java.awt.image.DataBuffer
import java.awt.image.DataBufferByte
import java.awt.image.Raster
import java.io.InputStream
import java.util.Base64
import javax.imageio.ImageIO

/**
 * @author Kevin Ludwig
 */
data class AppearanceImage(
    var width: Int,
    var height: Int,
    val data: ByteArray
) {
    val image: BufferedImage by lazy { BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB).apply { data = Raster.createRaster(colorModel.createCompatibleSampleModel(width, height), DataBufferByte(this@AppearanceImage.data, this@AppearanceImage.data.size), null) } }

    constructor(image: BufferedImage) : this(
        image.width, image.height, (BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(image.width, image.height), false, null).apply {
            createGraphics().apply {
                drawImage(image, 0, 0, null)
                dispose()
            }
        }.raster.dataBuffer as DataBufferByte).data
    )

    fun toJson(json: JsonObject, name: String? = null) {
        json.addProperty("${name}ImageWidth", width)
        json.addProperty("${name}ImageHeight", height)
        json.addProperty(if (null != name) "${name}Data" else "Image", base64Encoder.encodeToString(data))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AppearanceImage

        if (width != other.width) return false
        if (height != other.height) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + data.contentHashCode()
        return result
    }

    override fun toString() = "AppearanceImage(width=$width, height=$height, image=$image)"

    companion object {
        val Empty = AppearanceImage(0, 0, ByteArray(0))
    }
}

fun JsonObject.getAsAppearanceImage(name: String?): AppearanceImage {
    val data = base64Decoder.decode(getString(if (null != name) "${name}Data" else "Image"))
    val width: Int
    val height: Int
    if (has("${name ?: ""}ImageWidth") && has("${name ?: ""}ImageHeight")) {
        width = this["${name ?: ""}ImageWidth"].asInt
        height = this["${name ?: ""}ImageHeight"].asInt
    } else when (data.size) {
        64 * 32 * bytesPerPixel -> {
            width = 64
            height = 32
        }
        64 * 64 * bytesPerPixel -> {
            width = 64
            height = 64
        }
        128 * 64 * bytesPerPixel -> {
            width = 128
            height = 64
        }
        128 * 128 * bytesPerPixel -> {
            width = 128
            height = 128
        }
        256 * 256 * bytesPerPixel -> {
            width = 256
            height = 256
        }
        512 * 256 * bytesPerPixel -> {
            width = 512
            height = 256
        }
        512 * 512 * bytesPerPixel -> {
            width = 512
            height = 512
        }
        else -> throw IllegalArgumentException("Unable to detect image dimensions, image was ${data.size}")
    }
    return AppearanceImage(width, height, data)
}

fun PacketBuffer.readAppearanceImage(): AppearanceImage {
    val width = readIntLE()
    val height = readIntLE()
    return AppearanceImage(width, height, readByteArrayOfExpectedLength(width * height * bytesPerPixel))
}

fun PacketBuffer.writeAppearanceImage(value: AppearanceImage) {
    writeIntLE(value.width)
    writeIntLE(value.height)
    writeByteArray(value.data)
}

fun InputStream.readAppearanceImage(): AppearanceImage {
    val image = ImageIO.read(this)
    val width = image.width
    val height = image.height
    val convertedImage = BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(width, height), false, null)
    val convertedImageGraphics = convertedImage.createGraphics()
    convertedImageGraphics.drawImage(image, 0, 0, null)
    convertedImageGraphics.dispose()
    return AppearanceImage(width, height, (convertedImage.raster.dataBuffer as DataBufferByte).data)
}

private val base64Decoder = Base64.getDecoder()
private val base64Encoder = Base64.getEncoder()
private const val bytesPerPixel = 4
private val colorModel: ColorModel = ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), intArrayOf(8, 8, 8, 8), true, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE)
