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
import com.valaphee.tesseract.inventory.item.Item
import com.valaphee.tesseract.inventory.item.stack.meta.Meta
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.util.LittleEndianByteBufInputStream
import com.valaphee.tesseract.util.LittleEndianByteBufOutputStream
import com.valaphee.tesseract.util.LittleEndianVarIntByteBufInputStream
import com.valaphee.tesseract.util.LittleEndianVarIntByteBufOutputStream
import com.valaphee.tesseract.util.getCompoundTagOrNull
import com.valaphee.tesseract.util.getInt
import com.valaphee.tesseract.util.getIntOrNull
import com.valaphee.tesseract.util.getString
import com.valaphee.tesseract.util.nbt.CompoundTag
import com.valaphee.tesseract.util.nbt.NbtInputStream
import com.valaphee.tesseract.util.nbt.NbtOutputStream

/**
 * @author Kevin Ludwig
 */
@JsonSerialize(using = StackSerializer::class)
@JsonDeserialize(using = StackDeserializer::class)
class Stack<T : Meta> private constructor(
    val item: Item<T>,
    var subId: Int,
    var count: Int,
    var meta: T?,
    var canPlaceOn: Array<String>? = null,
    var canDestroy: Array<String>? = null,
    var blockingTicks: Long = 0,
    var netId: Int = 0,
    var blockRuntimeId: Int = 0
) {
    var tag: CompoundTag?
        get() = meta?.toTag()
        set(value) {
            value?.let { meta?.fromTag(value) } ?: run { meta = null }
        }

    constructor(item: Item<T>, subId: Int = 0, count: Int = 1, canPlaceOn: Array<String>? = null, canDestroy: Array<String>? = null, blockingTicks: Long = 0, netId: Int = 0, blockRuntimeId: Int = 0, meta: T.() -> Unit = {}) : this(item, subId, count, item.meta().apply(meta), canPlaceOn, canDestroy, blockingTicks, netId, blockRuntimeId)

    constructor(item: Item<T>, subId: Int = 0, count: Int, tag: CompoundTag?, canPlaceOn: Array<String>? = null, canDestroy: Array<String>? = null, blockingTicks: Long = 0, netId: Int = 0, blockRuntimeId: Int = 0) : this(item, subId, count, tag?.let { item.meta().apply { fromTag(tag) } }, canPlaceOn, canDestroy, blockingTicks, netId, blockRuntimeId)

    fun meta(meta: T.() -> Unit = {}): T = item.meta().apply(meta).also { this.meta = it }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Stack<*>

        if (item != other.item) return false
        if (count != other.count) return false
        if (meta != other.meta) return false

        return true
    }

    fun equalsIgnoreCount(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Stack<*>

        if (item != other.item) return false
        if (meta != other.meta) return false

        return true
    }

    override fun hashCode(): Int {
        var result = item.hashCode()
        result = 31 * result + count
        result = 31 * result + (meta?.hashCode() ?: 0)
        return result
    }
}

/**
 * @author Kevin Ludwig
 */
object StackSerializer : JsonSerializer<Stack<*>?>() {
    override fun serialize(value: Stack<*>?, generator: JsonGenerator, provider: SerializerProvider) {
        value?.let {
            generator.writeStartObject()
            generator.writeStringField("item", it.item.key)
            if (it.subId != 0) generator.writeNumberField("data", it.subId)
            if (it.count != 1) generator.writeNumberField("count", it.count)
            generator.writeEndObject()
        } ?: generator.writeNull()
    }
}

/**
 * @author Kevin Ludwig
 */
object StackDeserializer : JsonDeserializer<Stack<*>?>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Stack<*>? {
        val node = parser.readValueAsTree<JsonNode>()
        return if (node.isNull) null else {
            Stack(Item.byKeyOrNull(node["item"].asText()) as Item<*>, node["data"]?.asInt() ?: 0, node["count"]?.asInt() ?: 1)
        }
    }
}

fun CompoundTag.asStack() = Stack(
    when {
        has("Name") -> Item.byKeyOrNull(getString("Name")) ?: Item.default
        has("id") -> Item.byIdOrNull(getInt("id"))
        else -> Item.default
    } as Item<*>, getIntOrNull("Damage") ?: 0, getIntOrNull("Count") ?: 1, getCompoundTagOrNull("tag")
)

fun PacketBuffer.readStackPre431(): Stack<*>? {
    val id = readVarInt()
    if (id == 0) return null
    val item = Item.byIdOrNull(id) as Item<*>
    val countAndSubId = readVarInt()
    return Stack(
        item,
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
        if (item == shield) readVarLong() else 0
    )
}

fun PacketBuffer.readStackWithNetIdPre431(): Stack<*>? {
    val netId = readVarInt()
    val stack = readStackPre431()
    stack?.let { it.netId = netId }
    return stack
}

fun PacketBuffer.readStack(): Stack<*>? {
    val id = readVarInt()
    if (id == 0) return null
    val item = Item.byIdOrNull(id) as Item<*>
    val count = readUnsignedShortLE()
    val subId = readVarUInt()
    val netId = if (readBoolean()) readVarInt() else 0
    val blockRuntimeId = readVarInt()
    readVarUInt()
    return Stack(
        item,
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
        if (item == shield) readLongLE() else 0,
        netId,
        blockRuntimeId
    )
}

fun PacketBuffer.readStackInstance(): Stack<*>? {
    val id = readVarInt()
    if (id == 0) return null
    val item = Item.byIdOrNull(id) as Item<*>
    val count = readUnsignedShortLE()
    val subId = readVarUInt()
    val blockRuntimeId = readVarInt()
    readVarUInt()
    return Stack(
        item,
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
        if (item == shield) readLongLE() else 0,
        0,
        blockRuntimeId
    )
}

fun PacketBuffer.readIngredient(): Stack<*>? {
    val id = readVarInt()
    if (id == 0) return null
    return Stack(Item.byIdOrNull(id) as Item<*>, readVarInt().let { if (it == Short.MAX_VALUE.toInt()) -1 else it }, readVarInt())
}

fun PacketBuffer.writeStackPre431(value: Stack<*>?) {
    value?.let {
        writeVarInt(it.item.id)
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
        if (it.item == shield) writeVarLong(it.blockingTicks)
    } ?: writeVarInt(0)
}

fun PacketBuffer.writeStackWithNetIdPre431(value: Stack<*>?) {
    writeVarInt(value?.netId ?: 0)
    writeStackPre431(value)
}

fun PacketBuffer.writeStack(value: Stack<*>?) {
    value?.let {
        writeVarInt(it.item.id)
        writeShortLE(it.count)
        writeVarUInt(it.subId)
        if (it.netId != 0) {
            writeBoolean(true)
            writeVarInt(it.netId)
        } else writeBoolean(false)
        writeVarInt(it.blockRuntimeId)
        val dataLengthIndex = buffer.writerIndex()
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
        if (it.item == shield) writeLongLE(it.blockingTicks)
        setMaximumLengthVarUInt(dataLengthIndex, writerIndex() - (dataLengthIndex + PacketBuffer.MaximumVarUIntLength))
    } ?: writeVarInt(0)
}

fun PacketBuffer.writeStackInstance(value: Stack<*>?) {
    value?.let {
        writeVarInt(it.item.id)
        writeShortLE(it.count)
        writeVarUInt(it.subId)
        writeVarInt(it.blockRuntimeId)
        val dataLengthIndex = buffer.writerIndex()
        writeZero(PacketBuffer.MaximumVarUIntLength)
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
        if (it.item == shield) writeVarLong(it.blockingTicks)
        setMaximumLengthVarUInt(dataLengthIndex, writerIndex() - (dataLengthIndex + PacketBuffer.MaximumVarUIntLength))
    } ?: writeVarInt(0)
}

fun PacketBuffer.writeIngredient(value: Stack<*>?) {
    value?.let {
        writeVarInt(value.item.id)
        writeVarInt(if (value.subId == -1) Short.MAX_VALUE.toInt() else value.subId)
        writeVarInt(value.count)
    } ?: writeVarInt(0)
}

private val shield = Item.byKeyOrNull("minecraft:shield")
