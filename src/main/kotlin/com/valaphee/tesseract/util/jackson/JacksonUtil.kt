/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.util.jackson

import com.fasterxml.jackson.core.JsonGenerator
import java.io.IOException

@Throws(IOException::class)
fun JsonGenerator.writeArray(array: FloatArray, offset: Int, length: Int) {
    this.writeStartArray(array, length)
    var i = offset
    val end = offset + length
    while (i < end) {
        this.writeNumber(array[i])
        ++i
    }
    this.writeEndArray()
}
