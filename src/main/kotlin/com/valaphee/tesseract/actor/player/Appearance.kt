/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.player

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.internal.Streams
import com.google.gson.stream.JsonReader
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.util.ByteBufStringReader
import com.valaphee.tesseract.util.getBool
import com.valaphee.tesseract.util.getFloat
import com.valaphee.tesseract.util.getInt
import com.valaphee.tesseract.util.getIntOrNull
import com.valaphee.tesseract.util.getJsonArray
import com.valaphee.tesseract.util.getJsonArrayOrNull
import com.valaphee.tesseract.util.getString
import com.valaphee.tesseract.util.getStringOrNull
import java.io.StringReader
import java.nio.charset.StandardCharsets
import java.util.Base64

/**
 * @author Kevin Ludwig
 */
data class Appearance constructor(
    var id: String? = null,
    val skinId: String,
    val skinResourcePatch: Map<String, Any>,
    val skinImage: AppearanceImage,
    val skinGeometryData: String,
    val skinAnimationData: String,
    val capeId: String,
    val capeImage: AppearanceImage,
    val capeOnClassicSkin: Boolean,
    val animations: List<AppearanceAnimation>,
    val premiumSkin: Boolean,
    val personaSkin: Boolean,
    val armSize: String,
    val skinColor: String,
    val personaPieces: List<PersonaPiece>,
    val personaPieceTints: List<PersonaPieceTint>,
    var trusted: Boolean,
    var playFabId: String,
) {
    data class PersonaPiece(
        val id: String,
        val type: String,
        val packId: String,
        val defaultPersonaPiece: Boolean,
        val productId: String
    )

    data class PersonaPieceTint(
        val pieceType: String,
        val colors: Array<String>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as PersonaPieceTint

            if (pieceType != other.pieceType) return false
            if (!colors.contentEquals(other.colors)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = pieceType.hashCode()
            result = 31 * result + colors.contentHashCode()
            return result
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Appearance

        if (id != other.id) return false

        return true
    }

    override fun hashCode() = id?.hashCode() ?: 0

    fun toJson(json: JsonObject): JsonObject {
        json.addProperty("SkinId", skinId)
        val jsonSkinResourcePatch = JsonObject()
        skinResourcePatch.forEach { jsonSkinResourcePatch.add(it.key, gson.toJsonTree(it.value)) }
        json.addProperty("SkinResourcePatch", base64Encoder.encodeToString(jsonSkinResourcePatch.toString().toByteArray(StandardCharsets.UTF_8)))
        skinImage.toJson(json, "Skin")
        json.addProperty("SkinGeometryData", base64Encoder.encodeToString(skinGeometryData.toByteArray(StandardCharsets.UTF_8)))
        json.addProperty("SkinAnimationData", base64Encoder.encodeToString(skinAnimationData.toByteArray(StandardCharsets.UTF_8)))
        json.addProperty("CapeId", capeId)
        capeImage.toJson(json, "Cape")
        json.addProperty("CapeOnClassicSkin", capeOnClassicSkin)
        val jsonAnimations = JsonArray()
        animations.forEach { (image, type, frames, expression) ->
            val jsonAnimation = JsonObject()
            jsonAnimation.addProperty("Type", type.ordinal)
            jsonAnimation.addProperty("Frames", frames)
            image.toJson(jsonAnimation)
            jsonAnimation.addProperty("AnimationExpression", expression.ordinal)
            jsonAnimations.add(jsonAnimation)
        }
        json.add("AnimatedImageData", jsonAnimations)
        json.addProperty("PremiumSkin", premiumSkin)
        json.addProperty("PersonaSkin", personaSkin)
        json.addProperty("ArmSize", armSize)
        json.addProperty("SkinColor", skinColor)
        val jsonPersonaPieces = JsonArray()
        personaPieces.forEach {
            val jsonPersonaPiece = JsonObject()
            jsonPersonaPiece.addProperty("PieceId", it.id)
            jsonPersonaPiece.addProperty("PieceType", it.type)
            jsonPersonaPiece.addProperty("PackId", it.packId)
            jsonPersonaPiece.addProperty("IsDefault", it.defaultPersonaPiece)
            jsonPersonaPiece.addProperty("ProductId", it.productId)
            jsonPersonaPieces.add(jsonPersonaPiece)
        }
        json.add("PersonaPieces", jsonPersonaPieces)
        val jsonPersonaPieceTints = JsonArray()
        personaPieceTints.forEach {
            val jsonPersonaPieceTint = JsonObject()
            jsonPersonaPieceTint.addProperty("PieceType", it.pieceType)
            val jsonPersonaPieceTintColors = JsonArray()
            it.colors.forEach { jsonPersonaPieceTintColors.add(it) }
            jsonPersonaPieceTint.add("Colors", jsonPersonaPieceTintColors)
            jsonPersonaPieceTints.add(jsonPersonaPieceTint)
        }
        json.add("PieceTintColors", jsonPersonaPieceTints)
        return json
    }
}

val JsonObject.asAppearance
    get() = Appearance(
        "",
        getString("SkinId"),
        getStringOrNull("SkinResourcePatch")?.let {
            val jsonSkinResourcePatch = Streams.parse(JsonReader(StringReader(String(base64Decoder.decode(it), StandardCharsets.UTF_8))))
            val skinResourcePatch: MutableMap<String, Any> = HashMap()
            if (jsonSkinResourcePatch.isJsonObject) jsonSkinResourcePatch.asJsonObject.entrySet().forEach { skinResourcePatch[it.key] = gson.fromJson(it.value, Any::class.java) }
            skinResourcePatch
        } ?: emptyMap(),
        getAsAppearanceImage("Skin"),
        String(base64Decoder.decode(getString("SkinGeometryData")), StandardCharsets.UTF_8),
        String(base64Decoder.decode(getString("SkinAnimationData")), StandardCharsets.UTF_8),
        getString("CapeId"),
        getAsAppearanceImage("Cape"),
        getBool("CapeOnClassicSkin"),
        run {
            val animations: MutableList<AppearanceAnimation> = ArrayList()
            getJsonArray("AnimatedImageData").forEach {
                val jsonAnimation = it.asJsonObject
                animations.add(
                    AppearanceAnimation(
                        jsonAnimation.getAsAppearanceImage(null),
                        AppearanceAnimation.Type.values()[jsonAnimation.getInt("Type")],
                        jsonAnimation.getFloat("Frames"),
                        AppearanceAnimation.Expression.values()[jsonAnimation.getIntOrNull("AnimationExpression") ?: 0],
                    )
                )
            }
            animations
        },
        getBool("PremiumSkin"),
        getBool("PersonaSkin"),
        getStringOrNull("ArmSize") ?: "",
        getStringOrNull("SkinColor") ?: "#0",
        run {
            val personaPieces = ArrayList<Appearance.PersonaPiece>()
            getJsonArrayOrNull("PersonaPieces")?.forEach {
                val jsonPersonaPiece = it.asJsonObject
                personaPieces.add(
                    Appearance.PersonaPiece(
                        jsonPersonaPiece.getString("PieceId"),
                        jsonPersonaPiece.getString("PieceType"),
                        jsonPersonaPiece.getString("PackId"),
                        jsonPersonaPiece.getBool("IsDefault"),
                        jsonPersonaPiece.getString("ProductId")
                    )
                )
            }
            personaPieces
        },
        run {
            val personaPieceTints = ArrayList<Appearance.PersonaPieceTint>()
            getJsonArrayOrNull("PieceTintColors")?.forEach {
                val jsonPersonaPieceTint = it.asJsonObject
                val jsonPersonaPieceTintColors = jsonPersonaPieceTint.getJsonArray("Colors")
                personaPieceTints.add(Appearance.PersonaPieceTint(jsonPersonaPieceTint.getString("PieceType"), Array(jsonPersonaPieceTintColors.size()) { jsonPersonaPieceTintColors[it].asString }))
            }
            personaPieceTints
        },
        false,
        ""
    )

fun PacketBuffer.readAppearancePre390(): Appearance {
    val skinId = readString()
    val skinResourcePatch = Streams.parse(JsonReader(ByteBufStringReader(this, readVarUInt()))).asJsonObject
    val skinImage = readAppearanceImage()
    val animationCount = readIntLE()
    val animations = ArrayList<AppearanceAnimation>(animationCount).apply { repeat(animationCount) { add(AppearanceAnimation(readAppearanceImage(), AppearanceAnimation.Type.values()[readIntLE()], buffer.readFloatLE())) } }
    val capeImage = readAppearanceImage()
    val skinGeometryData = readString()
    val skinAnimationData = readString()
    val premiumSkin = readBoolean()
    val personaSkin = readBoolean()
    val capeOnClassicSkin = readBoolean()
    val capeId = readString()
    val id = readString()
    return Appearance(id, skinId, mutableMapOf<String, Any>().apply { skinResourcePatch.entrySet().forEach { this[it.key] = gson.fromJson(it.value, Any::class.java) } }, skinImage, skinGeometryData, skinAnimationData, capeId, capeImage, capeOnClassicSkin, animations, premiumSkin, personaSkin, "", "", emptyList(), emptyList(), false, "")
}

fun PacketBuffer.readAppearancePre419(): Appearance {
    val skinId = readString()
    val skinResourcePatch = Streams.parse(JsonReader(ByteBufStringReader(this, readVarUInt()))).asJsonObject
    val skinImage = readAppearanceImage()
    val animationCount = readIntLE()
    val animations = ArrayList<AppearanceAnimation>(animationCount).apply { repeat(animationCount) { add(AppearanceAnimation(readAppearanceImage(), AppearanceAnimation.Type.values()[buffer.readIntLE()], buffer.readFloatLE())) } }
    val capeImage = readAppearanceImage()
    val skinGeometryData = readString()
    val skinAnimationData = readString()
    val premiumSkin = readBoolean()
    val personaSkin = readBoolean()
    val capeOnClassicSkin = readBoolean()
    val capeId = readString()
    val id = readString()
    val armSize = readString()
    val skinColor = readString()
    val personaPieces = ArrayList<Appearance.PersonaPiece>().apply { repeat(readIntLE()) { add(Appearance.PersonaPiece(readString(), readString(), readString(), readBoolean(), readString())) } }
    val personaPieceTints = ArrayList<Appearance.PersonaPieceTint>().apply { repeat(readIntLE()) { add(Appearance.PersonaPieceTint(readString(), Array(readIntLE()) { readString() })) } }
    return Appearance(id, skinId, mutableMapOf<String, Any>().apply { skinResourcePatch.entrySet().forEach { this[it.key] = gson.fromJson(it.value, Any::class.java) } }, skinImage, skinGeometryData, skinAnimationData, capeId, capeImage, capeOnClassicSkin, animations, premiumSkin, personaSkin, armSize, skinColor, personaPieces, personaPieceTints, false, "")
}

fun PacketBuffer.readAppearancePre428(): Appearance {
    val skinId = readString()
    val skinResourcePatch = Streams.parse(JsonReader(ByteBufStringReader(this, readVarUInt()))).asJsonObject
    val skinImage = readAppearanceImage()
    val animationCount = readIntLE()
    val animations = ArrayList<AppearanceAnimation>(animationCount).apply { repeat(animationCount) { add(AppearanceAnimation(readAppearanceImage(), AppearanceAnimation.Type.values()[buffer.readIntLE()], buffer.readFloatLE(), AppearanceAnimation.Expression.values()[buffer.readIntLE()])) } }
    val capeImage = readAppearanceImage()
    val skinGeometryData = readString()
    val skinAnimationData = readString()
    val premiumSkin = readBoolean()
    val personaSkin = readBoolean()
    val capeOnClassicSkin = readBoolean()
    val capeId = readString()
    val id = readString()
    val armSize = readString()
    val skinColor = readString()
    val personaPieces = ArrayList<Appearance.PersonaPiece>().apply { repeat(readIntLE()) { add(Appearance.PersonaPiece(readString(), readString(), readString(), readBoolean(), readString())) } }
    val personaPieceTints = ArrayList<Appearance.PersonaPieceTint>().apply { repeat(readIntLE()) { add(Appearance.PersonaPieceTint(readString(), Array(readIntLE()) { readString() })) } }
    return Appearance(id, skinId, mutableMapOf<String, Any>().apply { skinResourcePatch.entrySet().forEach { this[it.key] = gson.fromJson(it.value, Any::class.java) } }, skinImage, skinGeometryData, skinAnimationData, capeId, capeImage, capeOnClassicSkin, animations, premiumSkin, personaSkin, armSize, skinColor, personaPieces, personaPieceTints, false, "")
}

fun PacketBuffer.readAppearance(): Appearance {
    val skinId = readString()
    val playFabId = readString()
    val skinResourcePatch = Streams.parse(JsonReader(ByteBufStringReader(this, readVarUInt()))).asJsonObject
    val skinImage = readAppearanceImage()
    val animationCount = readIntLE()
    val animations = ArrayList<AppearanceAnimation>(animationCount).apply { repeat(animationCount) { add(AppearanceAnimation(readAppearanceImage(), AppearanceAnimation.Type.values()[buffer.readIntLE()], buffer.readFloatLE(), AppearanceAnimation.Expression.values()[buffer.readIntLE()])) } }
    val capeImage = readAppearanceImage()
    val skinGeometryData = readString()
    val skinAnimationData = readString()
    val premiumSkin = readBoolean()
    val personaSkin = readBoolean()
    val capeOnClassicSkin = readBoolean()
    val capeId = readString()
    val id = readString()
    val armSize = readString()
    val skinColor = readString()
    val personaPieces = ArrayList<Appearance.PersonaPiece>().apply { repeat(readIntLE()) { add(Appearance.PersonaPiece(readString(), readString(), readString(), readBoolean(), readString())) } }
    val personaPieceTints = ArrayList<Appearance.PersonaPieceTint>().apply { repeat(readIntLE()) { add(Appearance.PersonaPieceTint(readString(), Array(readIntLE()) { readString() })) } }
    return Appearance(id, skinId, mutableMapOf<String, Any>().apply { skinResourcePatch.entrySet().forEach { this[it.key] = gson.fromJson(it.value, Any::class.java) } }, skinImage, skinGeometryData, skinAnimationData, capeId, capeImage, capeOnClassicSkin, animations, premiumSkin, personaSkin, armSize, skinColor, personaPieces, personaPieceTints, false, playFabId)
}

fun PacketBuffer.writeAppearancePre390(value: Appearance) {
    writeString(value.skinId)
    writeString(JsonObject().apply { value.skinResourcePatch.forEach { add(it.key, gson.toJsonTree(it.value)) } }.toString())
    writeAppearanceImage(value.skinImage)
    writeIntLE(value.animations.size)
    value.animations.forEach { (image, type, frames) ->
        writeAppearanceImage(image)
        writeIntLE(type.ordinal)
        writeFloatLE(frames)
    }
    writeAppearanceImage(value.capeImage)
    writeString(value.skinGeometryData)
    writeString(value.skinAnimationData)
    writeBoolean(value.premiumSkin)
    writeBoolean(value.personaSkin)
    writeBoolean(value.capeOnClassicSkin)
    writeString(value.capeId)
    writeString(value.id!!)
}

fun PacketBuffer.writeAppearancePre419(value: Appearance) {
    writeAppearancePre390(value)
    writeString(value.armSize)
    writeString(value.skinColor)
    writeIntLE(value.personaPieces.size)
    value.personaPieces.forEach {
        writeString(it.id)
        writeString(it.type)
        writeString(it.packId)
        writeBoolean(it.defaultPersonaPiece)
        writeString(it.productId)
    }
    writeIntLE(value.personaPieceTints.size)
    value.personaPieceTints.forEach {
        writeString(it.pieceType)
        val personaPieceTintColors = it.colors
        writeIntLE(personaPieceTintColors.size)
        personaPieceTintColors.forEach { writeString(it) }
    }
}

fun PacketBuffer.writeAppearancePre428(value: Appearance) {
    writeString(value.skinId)
    writeString(JsonObject().apply { value.skinResourcePatch.forEach { add(it.key, gson.toJsonTree(it.value)) } }.toString())
    writeAppearanceImage(value.skinImage)
    writeIntLE(value.animations.size)
    value.animations.forEach { (image, type, frames, expression) ->
        writeAppearanceImage(image)
        writeIntLE(type.ordinal)
        writeFloatLE(frames)
        writeIntLE(expression.ordinal)
    }
    writeAppearanceImage(value.capeImage)
    writeString(value.skinGeometryData)
    writeString(value.skinAnimationData)
    writeBoolean(value.premiumSkin)
    writeBoolean(value.personaSkin)
    writeBoolean(value.capeOnClassicSkin)
    writeString(value.capeId)
    writeString(value.id!!)
    writeString(value.armSize)
    writeString(value.skinColor)
    writeIntLE(value.personaPieces.size)
    value.personaPieces.forEach {
        writeString(it.id)
        writeString(it.type)
        writeString(it.packId)
        writeBoolean(it.defaultPersonaPiece)
        writeString(it.productId)
    }
    writeIntLE(value.personaPieceTints.size)
    value.personaPieceTints.forEach {
        writeString(it.pieceType)
        val personaPieceTintColors = it.colors
        writeIntLE(personaPieceTintColors.size)
        personaPieceTintColors.forEach { writeString(it) }
    }
}

fun PacketBuffer.writeAppearance(value: Appearance) {
    writeString(value.skinId)
    writeString(value.playFabId)
    writeString(JsonObject().apply { value.skinResourcePatch.forEach { add(it.key, gson.toJsonTree(it.value)) } }.toString())
    writeAppearanceImage(value.skinImage)
    writeIntLE(value.animations.size)
    value.animations.forEach { (image, type, frames, expression) ->
        writeAppearanceImage(image)
        writeIntLE(type.ordinal)
        writeFloatLE(frames)
        writeIntLE(expression.ordinal)
    }
    writeAppearanceImage(value.capeImage)
    writeString(value.skinGeometryData)
    writeString(value.skinAnimationData)
    writeBoolean(value.premiumSkin)
    writeBoolean(value.personaSkin)
    writeBoolean(value.capeOnClassicSkin)
    writeString(value.capeId)
    writeString(value.id!!)
    writeString(value.armSize)
    writeString(value.skinColor)
    writeIntLE(value.personaPieces.size)
    value.personaPieces.forEach {
        writeString(it.id)
        writeString(it.type)
        writeString(it.packId)
        writeBoolean(it.defaultPersonaPiece)
        writeString(it.productId)
    }
    writeIntLE(value.personaPieceTints.size)
    value.personaPieceTints.forEach {
        writeString(it.pieceType)
        val personaPieceTintColors = it.colors
        writeIntLE(personaPieceTintColors.size)
        personaPieceTintColors.forEach { writeString(it) }
    }
}

private val gson = Gson()
private val base64Decoder = Base64.getDecoder()
private val base64Encoder = Base64.getEncoder()
