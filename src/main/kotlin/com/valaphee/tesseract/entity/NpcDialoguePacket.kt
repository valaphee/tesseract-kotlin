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

package com.valaphee.tesseract.entity

import com.valaphee.tesseract.net.Packet
import com.valaphee.tesseract.net.PacketBuffer
import com.valaphee.tesseract.net.PacketHandler
import com.valaphee.tesseract.net.PacketReader

/**
 * @author Kevin Ludwig
 */
class NpcDialoguePacket(
    val uniqueEntityId: Long,
    val action: Action,
    val dialogue: String,
    val sceneName: String,
    val npcName: String,
    val actionJson: String
) : Packet {
    enum class Action {
        Open,
        Close
    }

    override val id get() = 0xA9

    override fun write(buffer: PacketBuffer, version: Int) {
        buffer.writeLongLE(uniqueEntityId)
        buffer.writeVarInt(action.ordinal)
        buffer.writeString(dialogue)
        buffer.writeString(sceneName)
        buffer.writeString(npcName)
        buffer.writeString(actionJson)
    }

    override fun handle(handler: PacketHandler) = handler.npcDialogue(this)

    override fun toString() = "NpcDialoguePacket(uniqueEntityId=$uniqueEntityId, action=$action, dialogue='$dialogue', sceneName='$sceneName', npcName='$npcName', actionJson='$actionJson')"
}

/**
 * @author Kevin Ludwig
 */
object NpcDialoguePacketReader : PacketReader {
    override fun read(buffer: PacketBuffer, version: Int) = NpcDialoguePacket(buffer.readLongLE(), NpcDialoguePacket.Action.values()[buffer.readVarInt()], buffer.readString(), buffer.readString(), buffer.readString(), buffer.readString())
}
