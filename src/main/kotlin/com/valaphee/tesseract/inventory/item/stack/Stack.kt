/*
 * MIT License
 *
 * Copyright (c) 2021, Valaphee.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.valaphee.tesseract.inventory.item.stack

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.util.LittleEndianByteBufInputStream
import com.valaphee.tesseract.util.LittleEndianByteBufOutputStream
import com.valaphee.tesseract.util.LittleEndianVarIntByteBufInputStream
import com.valaphee.tesseract.util.LittleEndianVarIntByteBufOutputStream
import com.valaphee.tesseract.util.getCompoundTagOrNull
import com.valaphee.tesseract.util.getIntOrNull
import com.valaphee.tesseract.util.getString
import com.valaphee.tesseract.util.nbt.CompoundTag
import com.valaphee.tesseract.util.nbt.NbtInputStream
import com.valaphee.tesseract.util.nbt.NbtOutputStream
import io.netty.buffer.Unpooled
import java.util.Base64

/**
 * @author Kevin Ludwig
 */
@JsonSerialize(using = StackSerializer::class)
@JsonDeserialize(using = StackDeserializer::class)
data class Stack(
    val itemKey: String,
    var subId: Int = 0,
    var count: Int = 1,
    var tag: CompoundTag? = null,
    var canPlaceOn: Array<String>? = null,
    var canDestroy: Array<String>? = null,
    var blockingTicks: Long = 0,
    var netId: Int = 0,
    var blockStateKey: String? = null
) {
    fun toTag(tag: CompoundTag): CompoundTag {
        tag.setString("Name", itemKey)
        tag.setShort("Damage", subId.toShort())
        tag.setByte("Count", count.toByte())
        this.tag?.let { tag["tag"] = it }
        return tag
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Stack

        if (itemKey != other.itemKey) return false
        if (subId != other.subId) return false
        if (count != other.count) return false
        if (tag != other.tag) return false
        if (blockStateKey != other.blockStateKey) return false

        return true
    }

    fun equalsIgnoreCount(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Stack

        if (itemKey != other.itemKey) return false
        if (subId != other.subId) return false
        if (tag != other.tag) return false
        if (blockStateKey != other.blockStateKey) return false

        return true
    }

    override fun hashCode(): Int {
        var result = itemKey.hashCode()
        result = 31 * result + subId
        result = 31 * result + count
        result = 31 * result + (tag?.hashCode() ?: 0)
        return result
    }
}

/**
 * @author Kevin Ludwig
 */
object StackSerializer : JsonSerializer<Stack?>() {
    private val base64Encoder = Base64.getEncoder()

    override fun serialize(value: Stack?, generator: JsonGenerator, provider: SerializerProvider) {
        value?.let {
            generator.writeStartObject()
            generator.writeStringField("item", it.itemKey)
            if (it.subId != 0) generator.writeNumberField("subId", it.subId)
            if (it.count != 1) generator.writeNumberField("count", it.count)
            it.tag?.let { tag ->
                var buffer: PacketBuffer? = null
                try {
                    buffer = PacketBuffer(Unpooled.buffer(), true).also { it.toNbtOutputStream().use { it.writeTag(tag) } }
                    val array = ByteArray(buffer.readableBytes())
                    buffer.readBytes(array)
                    generator.writeStringField("tag", base64Encoder.encodeToString(array))
                } finally {
                    buffer?.release()
                }
            }
            it.blockStateKey?.let { generator.writeStringField("blockState", it) }
            generator.writeEndObject()
        } ?: generator.writeNull()
    }
}

/**
 * @author Kevin Ludwig
 */
class StackDeserializer : JsonDeserializer<Stack?>() {
    private val base64Decoder = Base64.getDecoder()

    override fun deserialize(parser: JsonParser, context: DeserializationContext): Stack? {
        val node = parser.readValueAsTree<JsonNode>()
        return if (node.isNull) null else {
            var tag: CompoundTag? = null
            node["tag"]?.let {
                var buffer: PacketBuffer? = null
                try {
                    buffer = PacketBuffer(Unpooled.wrappedBuffer(base64Decoder.decode(it.asText())), true).also { tag = it.toNbtInputStream().use { it.readTag()?.asCompoundTag() } }
                } finally {
                    buffer?.release()
                }
            }
            Stack(checkNotNull(node["item"].asText()), node["subId"]?.asInt() ?: 0, node["count"]?.asInt() ?: 1, tag, blockStateKey = node["blockState"]?.asText())
        }
    }
}

fun CompoundTag.asStack() = Stack(getString("Name"), getIntOrNull("Damage") ?: 0, getIntOrNull("Count") ?: 1, getCompoundTagOrNull("tag"))

fun PacketBuffer.readStackPre431(): Stack? {
    val id = readVarInt()
    if (id == 0) return null
    val itemKey = checkNotNull(items[id])
    val countAndSubId = readVarInt()
    return Stack(
        itemKey,
        (countAndSubId shr 8).let { if (it == Short.MAX_VALUE.toInt()) -1 else it },
        countAndSubId and 0xFF,
        readShortLE().toInt().let {
            when {
                it > 0 -> NbtInputStream(LittleEndianByteBufInputStream(readSlice(it))).use { it.readTag()?.asCompoundTag() }
                it == -1 -> NbtInputStream(LittleEndianVarIntByteBufInputStream(this)).use { if (readVarUInt() == 1) it.readTag()?.asCompoundTag() else null }
                else -> null
            }
        },
        readVarInt().let { if (it == 0) null else Array(it) { readString() } },
        readVarInt().let { if (it == 0) null else Array(it) { readString() } },
        if (itemKey == shieldKey) readVarLong() else 0
    )
}

fun PacketBuffer.readStackWithNetIdPre431(): Stack? {
    val netId = readVarInt()
    val stack = readStackPre431()
    stack?.let { it.netId = netId }
    return stack
}

fun PacketBuffer.readStack(): Stack? {
    val id = readVarInt()
    if (id == 0) return null
    val itemKey = checkNotNull(items[id])
    val count = readUnsignedShortLE()
    val subId = readVarUInt()
    val netId = if (readBoolean()) readVarInt() else 0
    val blockRuntimeId = readVarInt()
    readVarUInt()
    return Stack(
        itemKey,
        subId,
        count,
        readShortLE().toInt().let {
            when {
                it > 0 -> NbtInputStream(LittleEndianByteBufInputStream(readSlice(it))).use { it.readTag()?.asCompoundTag() }
                it == -1 -> NbtInputStream(LittleEndianByteBufInputStream(this)).use { if (readUnsignedByte().toInt() == 1) it.readTag()?.asCompoundTag() else null }
                else -> null
            }
        },
        readIntLE().let { if (it == 0) null else Array(it) { readString16() } },
        readIntLE().let { if (it == 0) null else Array(it) { readString16() } },
        if (itemKey == shieldKey) readLongLE() else 0,
        netId,
        if (blockRuntimeId != 0) blockStates[blockRuntimeId] else null
    )
}

fun PacketBuffer.readStackInstance(): Stack? {
    val id = readVarInt()
    if (id == 0) return null
    val itemKey = checkNotNull(items[id])
    val count = readUnsignedShortLE()
    val subId = readVarUInt()
    val blockRuntimeId = readVarInt()
    readVarUInt()
    return Stack(
        itemKey,
        subId,
        count,
        readShortLE().toInt().let {
            when {
                it > 0 -> NbtInputStream(LittleEndianByteBufInputStream(readSlice(it))).use { it.readTag()?.asCompoundTag() }
                it == -1 -> NbtInputStream(LittleEndianByteBufInputStream(this)).use { if (readUnsignedByte().toInt() == 1) it.readTag()?.asCompoundTag() else null }
                else -> null
            }
        },
        readIntLE().let { if (it == 0) null else Array(it) { readString16() } },
        readIntLE().let { if (it == 0) null else Array(it) { readString16() } },
        if (itemKey == shieldKey) readLongLE() else 0,
        0,
        if (blockRuntimeId != 0) blockStates[blockRuntimeId] else null
    )
}

fun PacketBuffer.readIngredient(): Stack? {
    val id = readVarInt()
    if (id == 0) return null
    return Stack(checkNotNull(items[id]), readVarInt().let { if (it == Short.MAX_VALUE.toInt()) -1 else it }, readVarInt())
}

fun PacketBuffer.writeStackPre431(value: Stack?) {
    value?.let {
        writeVarInt(items.getId(it.itemKey))
        writeVarInt(((if (it.subId == -1) Short.MAX_VALUE.toInt() else it.subId) shl 8) or (it.count and 0xFF))
        it.tag?.let {
            writeShortLE(-1)
            writeVarUInt(1)
            NbtOutputStream(LittleEndianVarIntByteBufOutputStream(this)).use { stream -> stream.writeTag(it) }
        } ?: writeShortLE(0)
        it.canPlaceOn?.let {
            writeVarInt(it.size)
            it.forEach { writeString(it) }
        } ?: writeVarInt(0)
        it.canDestroy?.let {
            writeVarInt(it.size)
            it.forEach { writeString(it) }
        } ?: writeVarInt(0)
        if (it.itemKey == shieldKey) writeVarLong(it.blockingTicks)
    } ?: writeVarInt(0)
}

fun PacketBuffer.writeStackWithNetIdPre431(value: Stack?) {
    writeVarInt(value?.netId ?: 0)
    writeStackPre431(value)
}

fun PacketBuffer.writeStack(value: Stack?) {
    value?.let {
        writeVarInt(items.getId(it.itemKey))
        writeShortLE(it.count)
        writeVarUInt(it.subId)
        if (it.netId != 0) {
            writeBoolean(true)
            writeVarInt(it.netId)
        } else writeBoolean(false)
        writeVarInt(it.blockStateKey?.let { blockStates.getId(it) } ?: 0)
        val dataIndex = buffer.writerIndex()
        writeZero(PacketBuffer.MaximumVarUIntLength)
        it.tag?.let {
            writeShortLE(-1)
            writeByte(1)
            NbtOutputStream(LittleEndianByteBufOutputStream(this)).use { stream -> stream.writeTag(it) }
        } ?: writeShortLE(0)
        it.canPlaceOn?.let {
            writeIntLE(it.size)
            it.forEach { writeString16(it) }
        } ?: writeIntLE(0)
        it.canDestroy?.let {
            writeIntLE(it.size)
            it.forEach { writeString16(it) }
        } ?: writeIntLE(0)
        if (it.itemKey == shieldKey) writeLongLE(it.blockingTicks)
        setMaximumLengthVarUInt(dataIndex, writerIndex() - (dataIndex + PacketBuffer.MaximumVarUIntLength))
    } ?: writeVarInt(0)
}

fun PacketBuffer.writeStackInstance(value: Stack?) {
    value?.let {
        writeVarInt(items.getId(it.itemKey))
        writeShortLE(it.count)
        writeVarUInt(it.subId)
        writeVarInt(it.blockStateKey?.let { blockStates.getId(it) } ?: 0)
        val dataIndex = buffer.writerIndex()
        writeZero(PacketBuffer.MaximumVarUIntLength)
        it.tag?.let {
            writeShortLE(-1)
            writeVarUInt(1)
            NbtOutputStream(LittleEndianByteBufOutputStream(this)).use { stream -> stream.writeTag(it) }
        } ?: writeShortLE(0)
        it.canPlaceOn?.let {
            writeIntLE(it.size)
            it.forEach { writeString16(it) }
        } ?: writeIntLE(0)
        it.canDestroy?.let {
            writeIntLE(it.size)
            it.forEach { writeString16(it) }
        } ?: writeIntLE(0)
        if (it.itemKey == shieldKey) writeVarLong(it.blockingTicks)
        setMaximumLengthVarUInt(dataIndex, writerIndex() - (dataIndex + PacketBuffer.MaximumVarUIntLength))
    } ?: writeIntLE(0)
}

fun PacketBuffer.writeIngredient(value: Stack?) {
    value?.let {
        writeVarInt(checkNotNull(items.getId(it.itemKey)))
        writeVarInt(if (value.subId == -1) Short.MAX_VALUE.toInt() else value.subId)
        writeVarInt(value.count)
    } ?: writeVarInt(0)
}

private const val shieldKey = "minecraft:shield"
