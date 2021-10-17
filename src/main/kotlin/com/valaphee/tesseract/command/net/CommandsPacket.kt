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

package com.valaphee.tesseract.command.net

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader
import com.valaphee.tesseract.net.Restrict
import com.valaphee.tesseract.net.Restriction
import com.valaphee.tesseract.util.Registry

/**
 * @author Kevin Ludwig
 */
@Restrict(Restriction.ToClient)
class CommandsPacket(
    val commands: Array<Command>,
    val constraints: Array<EnumerationConstraint>
) : Packet {
    override val id get() = 0x4C

    override fun write(buffer: PacketBuffer, version: Int) {
        val values = mutableListOf<String>()
        val enumerationsMap = mutableMapOf<String, Enumeration>()
        val softEnumerationsMap = mutableMapOf<String, Enumeration>()
        val postfixes = mutableListOf<String>()
        commands.forEach { command ->
            command.aliases?.let {
                values.addAll(it.values)
                enumerationsMap[it.name] = it
            }
            command.overloads.forEach { overload ->
                overload.forEach { parameter ->
                    parameter.enumeration?.let {
                        if (it.soft) softEnumerationsMap[it.name] = it else {
                            values.addAll(it.values)
                            enumerationsMap[it.name] = it
                        }
                    }
                    parameter.postfix?.let { postfixes.add(it) }
                }
            }
        }
        buffer.writeVarUInt(values.size)
        values.forEach { buffer.writeString(it) }
        buffer.writeVarUInt(postfixes.size)
        postfixes.forEach { buffer.writeString(it) }
        val indexWriter: (Int) -> Unit = when {
            values.size <= 0xFF -> buffer::writeByte
            values.size <= 0xFFFF -> buffer::writeShortLE
            else -> buffer::writeIntLE
        }
        buffer.writeVarUInt(enumerationsMap.values.size)
        enumerationsMap.values.forEach {
            buffer.writeString(it.name)
            buffer.writeVarUInt(it.values.size)
            it.values.forEach { indexWriter(values.indexOf(it)) }
        }
        buffer.writeVarUInt(commands.size)
        commands.forEach { command ->
            buffer.writeString(command.name)
            buffer.writeString(command.description)
            if (version >= 448) buffer.writeShortLEFlags(command.flags) else buffer.writeByteFlags(command.flags)
            buffer.writeByte(command.permission.ordinal)
            buffer.writeIntLE(enumerationsMap.values.indexOf(command.aliases))
            buffer.writeVarUInt(command.overloads.size)
            command.overloads.forEach { overload ->
                buffer.writeVarUInt(overload.size)
                overload.forEach { parameter ->
                    buffer.writeString(parameter.name)
                    buffer.writeIntLE(parameter.postfix?.let { postfixes.indexOf(it) or parameterFlagPostfix } ?: parameter.enumeration?.let { (if (it.soft) softEnumerationsMap.values.indexOf(parameter.enumeration) or parameterFlagSoftEnumeration else enumerationsMap.values.indexOf(parameter.enumeration) or parameterFlagEnumeration) or parameterFlagValid } ?: parameter.type?.let { /*(if (version >= 419) parameterTypes else parameterTypesPre419).getKey(*/it/*)*/ or parameterFlagValid } ?: error("Unknown type in ${parameter.name}"))
                    buffer.writeBoolean(parameter.optional)
                    buffer.writeByteFlags(parameter.options)
                }
            }
        }
        buffer.writeVarUInt(softEnumerationsMap.values.size)
        softEnumerationsMap.values.forEach { buffer.writeEnumeration(it) }
        buffer.writeVarUInt(constraints.size)
        constraints.forEach { buffer.writeEnumerationConstraint(it, values, enumerationsMap.values) }
    }

    override fun handle(handler: PacketHandler) = handler.commands(this)

    override fun toString() = "CommandsPacket(commands=${commands.contentToString()}, constraints=${constraints.contentToString()})"

    companion object {
        internal val parameterTypesPre419 = Registry<Parameter.Type>().apply {
            this[0x01] = Parameter.Type.Integer
            this[0x02] = Parameter.Type.Float
            this[0x03] = Parameter.Type.Value
            this[0x04] = Parameter.Type.WildcardInteger
            this[0x05] = Parameter.Type.Operator
            this[0x06] = Parameter.Type.Target
            this[0x07] = Parameter.Type.WildcardTarget
            this[0x0E] = Parameter.Type.FilePath
            this[0x1D] = Parameter.Type.String
            this[0x25] = Parameter.Type.Int3
            this[0x26] = Parameter.Type.Float3
            this[0x29] = Parameter.Type.Message
            this[0x2B] = Parameter.Type.Text
            this[0x2F] = Parameter.Type.Json
            this[0x36] = Parameter.Type.Command
        }
        internal val parameterTypes = Registry<Parameter.Type>().apply {
            this[0x01] = Parameter.Type.Integer
            this[0x02] = Parameter.Type.Float
            /*this[0x03] = Parameter.Type.Float*/
            this[0x04] = Parameter.Type.Value
            this[0x05] = Parameter.Type.WildcardInteger
            this[0x06] = Parameter.Type.Operator
            this[0x07] = Parameter.Type.Target
            this[0x09] = /*Parameter.Type.Target*/Parameter.Type.WildcardTarget
            this[0x10] = Parameter.Type.FilePath
            this[0x20] = Parameter.Type.String
            this[0x28] = Parameter.Type.Int3
            this[0x29] = Parameter.Type.Float3
            this[0x2C] = Parameter.Type.Message
            this[0x2E] = Parameter.Type.Text
            this[0x32] = Parameter.Type.Json
            this[0x3C] = Parameter.Type.BlockState
            this[0x3F] = Parameter.Type.Command
        }
        internal const val parameterFlagValid = 1 shl 20
        internal const val parameterFlagEnumeration = 1 shl 21
        internal const val parameterFlagPostfix = 1 shl 24
        internal const val parameterFlagSoftEnumeration = 1 shl 26
    }
}

/**
 * @author Kevin Ludwig
 */
object CommandsPacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int): CommandsPacket {
        val values = Array(buffer.readVarUInt()) { buffer.readString() }
        val postfixes = Array(buffer.readVarUInt()) { buffer.readString() }
        val indexReader: () -> Int = when {
            values.size <= 0xFF -> {
                { buffer.readUnsignedByte().toInt() }
            }
            values.size <= 0xFFFF -> {
                { buffer.readUnsignedShortLE() }
            }
            else -> {
                { buffer.readIntLE() }
            }
        }
        val enumerations = Array(buffer.readVarUInt()) { Enumeration(buffer.readString(), Array(buffer.readVarUInt()) { values[indexReader()] }, false) }
        val commandStructures = Array(buffer.readVarUInt()) {
            val name = buffer.readString()
            val description = buffer.readString()
            val flags = if (version >= 448) buffer.readShortLEFlags<Command.Flag>() else buffer.readByteFlags()
            val permission = Permission.values()[buffer.readByte().toInt()]
            val aliasesIndex = buffer.readIntLE()
            val overloadStructures = Array(buffer.readVarUInt()) {
                Array(buffer.readVarUInt()) {
                    val parameterName = buffer.readString()
                    val type = buffer.readIntLE()
                    val optional = buffer.readBoolean()
                    val options = buffer.readByteFlags<Parameter.Option>()
                    Parameter.Structure(
                        parameterName,
                        optional,
                        options,
                        type and CommandsPacket.parameterFlagEnumeration != 0,
                        type and CommandsPacket.parameterFlagSoftEnumeration != 0,
                        type and CommandsPacket.parameterFlagPostfix != 0,
                        type and 0xFFFF
                    )
                }
            }
            Command.Structure(name, description, flags, permission, aliasesIndex, overloadStructures)
        }
        val softEnumerations = Array(buffer.readVarUInt()) { buffer.readEnumeration(true) }
        val constraints = Array(buffer.readVarUInt()) { buffer.readEnumerationConstraint(values, enumerations) }
        return CommandsPacket(Array(commandStructures.size) {
            val commandStructure = commandStructures[it]
            val aliasesIndex = commandStructure.aliasesIndex
            val aliases = if (aliasesIndex == -1) null else enumerations[aliasesIndex]
            val overloadStructures = commandStructure.overloadStructures
            val overloads: Array<Array<Parameter>> = Array(overloadStructures.size) { i ->
                Array(overloadStructures[i].size) { j ->
                    val overloadStructure = overloadStructures[i][j]
                    var postfix: String? = null
                    var enumeration: Enumeration? = null
                    var type: Int? = null
                    when {
                        overloadStructure.postfix -> postfix = postfixes[overloadStructure.index]
                        overloadStructure.enumeration -> enumeration = enumerations[overloadStructure.index]
                        overloadStructure.softEnumeration -> enumeration = softEnumerations[overloadStructure.index]
                        else -> type = overloadStructure.index/*if (version >= 419) CommandsPacket.parameterTypes[overloadStructure.index] else CommandsPacket.parameterTypesPre419[overloadStructure.index]*/
                    }
                    Parameter(overloadStructure.name, overloadStructure.optional, overloadStructure.options, enumeration, postfix, type)
                }
            }
            Command(commandStructure.name, commandStructure.description, commandStructure.flags, commandStructure.permission, aliases, overloads)
        }, constraints)
    }
}
