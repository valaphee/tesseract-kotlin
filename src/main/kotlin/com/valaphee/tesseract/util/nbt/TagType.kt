/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.util.nbt

/**
 * @author Kevin Ludwig
 */
enum class TagType(
    val tag: ((name: kotlin.String) -> Tag)? = null
) {
    End,
    Byte(::ByteTagImpl),
    Short(::ShortTagImpl),
    Int(::IntTagImpl),
    Long(::LongTagImpl),
    Float(::FloatTagImpl),
    Double(::DoubleTagImpl),
    ByteArray(::ByteArrayTagImpl),
    String(::StringTagImpl),
    List(::ListTagImpl),
    Compound(::CompoundTagImpl),
    IntArray(::IntArrayTagImpl),
    LongArray(::LongArrayTagImpl)
}
