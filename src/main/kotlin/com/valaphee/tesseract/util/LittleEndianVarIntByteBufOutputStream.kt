/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.util

import io.netty.buffer.ByteBuf
import java.nio.charset.StandardCharsets

/**
 * @author Kevin Ludwig
 */
class LittleEndianVarIntByteBufOutputStream(
    buffer: ByteBuf
) : LittleEndianByteBufOutputStream(buffer) {
    override fun writeInt(value: Int) {
        writeVarUInt((value shl 1) xor (value shr 31))
    }

    override fun writeLong(value: Long) {
        @Suppress("NAME_SHADOWING") var value = (value shl 1) xor (value shr 63)
        while (true) {
            if (value and 0x7FL.inv() == 0L) {
                writeByte(value.toInt())
                return
            } else {
                writeByte((value.toInt() and 0x7F) or 0x80)
                value = value ushr 7
            }
        }
    }

    override fun writeUTF(value: String) {
        val bytes = value.toByteArray(StandardCharsets.UTF_8)
        writeVarUInt(bytes.size)
        write(bytes)
    }

    private fun writeVarUInt(value: Int) {
        @Suppress("NAME_SHADOWING") var value = value
        while (true) {
            if (value and 0x7F.inv() == 0) {
                writeByte(value)
                return
            } else {
                writeByte((value and 0x7F) or 0x80)
                value = value ushr 7
            }
        }
    }
}
