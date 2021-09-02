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

package com.valaphee.tesseract.net

import com.valaphee.tesseract.actor.location.MotionPacketReader
import com.valaphee.tesseract.actor.player.EmotePacketReader
import com.valaphee.tesseract.actor.player.EmotesPacketReader
import com.valaphee.tesseract.actor.player.InputPacketReader
import com.valaphee.tesseract.actor.player.InteractPacketReader
import com.valaphee.tesseract.actor.player.PlayerActionPacketReader
import com.valaphee.tesseract.actor.player.PlayerLocationPacketReader
import com.valaphee.tesseract.actor.player.view.ChunkPacketReader
import com.valaphee.tesseract.actor.player.view.ViewDistancePacketReader
import com.valaphee.tesseract.actor.player.view.ViewDistanceRequestPacketReader
import com.valaphee.tesseract.command.net.CommandPacketReader
import com.valaphee.tesseract.command.net.CommandResponsePacketReader
import com.valaphee.tesseract.command.net.CommandSoftEnumerationPacketReader
import com.valaphee.tesseract.command.net.CommandsPacketReader
import com.valaphee.tesseract.inventory.InventoryRequestPacketReader
import com.valaphee.tesseract.inventory.WindowClosePacketReader
import com.valaphee.tesseract.net.base.CacheBlobStatusPacketReader
import com.valaphee.tesseract.net.base.CacheStatusPacketReader
import com.valaphee.tesseract.net.base.DisconnectPacketReader
import com.valaphee.tesseract.net.base.LatencyPacketReader
import com.valaphee.tesseract.net.base.TextPacketReader
import com.valaphee.tesseract.net.base.TickSyncPacketReader
import com.valaphee.tesseract.net.base.ViolationPacketReader
import com.valaphee.tesseract.net.init.ClientToServerHandshakePacketReader
import com.valaphee.tesseract.net.init.LoginPacketReader
import com.valaphee.tesseract.net.init.PackDataChunkPacketReader
import com.valaphee.tesseract.net.init.PackDataChunkRequestPacketReader
import com.valaphee.tesseract.net.init.PackDataPacketReader
import com.valaphee.tesseract.net.init.PacksPacketReader
import com.valaphee.tesseract.net.init.PacksResponsePacketReader
import com.valaphee.tesseract.net.init.PacksStackPacketReader
import com.valaphee.tesseract.net.init.ServerToClientHandshakePacketReader
import com.valaphee.tesseract.net.init.StatusPacketReader
import com.valaphee.tesseract.world.DifficultyPacketReader
import com.valaphee.tesseract.world.DimensionPacketReader
import com.valaphee.tesseract.world.GameModePacketReader
import com.valaphee.tesseract.world.GameRulesPacketReader
import com.valaphee.tesseract.world.TimePacketReader
import com.valaphee.tesseract.world.WorldPacketReader
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * @author Kevin Ludwig
 */
class PacketDecoder(
    var version: Int = -1
) : MessageToMessageDecoder<ByteBuf>() {
    override fun decode(context: ChannelHandlerContext, `in`: ByteBuf, out: MutableList<Any>) {
        val buffer = PacketBuffer(`in`)
        val header = buffer.readVarUInt()
        val id = header and Packet.idMask
        readers[id]?.let { out.add(it.read(buffer, version)) } ?: log.debug("Unknown packet: 0x{}", id.toString(16).uppercase())
    }

    companion object {
        private val log: Logger = LogManager.getLogger(PacketDecoder::class.java)
        private val readers = Int2ObjectOpenHashMap<PacketReader>().apply {
            this[0x01] = LoginPacketReader
            this[0x02] = StatusPacketReader
            this[0x03] = ServerToClientHandshakePacketReader
            this[0x04] = ClientToServerHandshakePacketReader
            this[0x05] = DisconnectPacketReader
            this[0x06] = PacksPacketReader
            this[0x07] = PacksStackPacketReader
            this[0x08] = PacksResponsePacketReader
            this[0x09] = TextPacketReader
            this[0x0A] = TimePacketReader
            this[0x0B] = WorldPacketReader
            //this[0x0C] = PlayerAddPacketReader
            //this[0x0D] = ActorAddPacketReader
            //this[0x0E] = ActorRemovePacketReader
            //this[0x0F] = StackAddPacketReader
            //this[0x11] = StackTakePacketReader
            //this[0x12] = TeleportPacketReader
            this[0x13] = PlayerLocationPacketReader
            //this[0x14] =
            //this[0x15] = BlockUpdatePacketReader
            //this[0x16] = PaintingAddPacketReader*
            this[0x17] = TickSyncPacketReader
            //this[0x18] = SoundEventPacketV1Reader
            //this[0x19] = WorldEventPacketReader
            //this[0x1A] = BlockEventPacketReader*
            //this[0x1B] = ActorEventPacketReader*
            //this[0x1C] =
            //this[0x1D] =
            //this[0x1E] =
            //this[0x1F] =
            this[0x21] = InteractPacketReader

            this[0x24] = PlayerActionPacketReader

            this[0x28] = MotionPacketReader

            this[0x2F] = WindowClosePacketReader

            this[0x3A] = ChunkPacketReader

            this[0x3C] = DifficultyPacketReader
            this[0x3D] = DimensionPacketReader
            this[0x3E] = GameModePacketReader

            this[0x45] = ViewDistanceRequestPacketReader
            this[0x46] = ViewDistancePacketReader

            this[0x48] = GameRulesPacketReader

            this[0x4C] = CommandsPacketReader
            this[0x4D] = CommandPacketReader

            this[0x4F] = CommandResponsePacketReader

            this[0x52] = PackDataPacketReader
            this[0x53] = PackDataChunkPacketReader
            this[0x54] = PackDataChunkRequestPacketReader

            this[0x72] = CommandSoftEnumerationPacketReader
            this[0x73] = LatencyPacketReader

            this[0x81] = CacheStatusPacketReader

            this[0x87] = CacheBlobStatusPacketReader

            this[0x8A] = EmotePacketReader

            this[0x90] = InputPacketReader

            this[0x93] = InventoryRequestPacketReader

            this[0x98] = EmotesPacketReader

            this[0x9C] = ViolationPacketReader
        }

        const val NAME = "ta-packet-decoder"
    }
}
