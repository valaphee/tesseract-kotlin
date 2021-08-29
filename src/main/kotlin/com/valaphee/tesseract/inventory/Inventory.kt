/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.inventory

import com.valaphee.tesseract.item.stack.Stack

/**
 * @author Kevin Ludwig
 */
class Inventory(
    val type: WindowType,
    private val content: Array<Stack<*>?>
) {
    constructor(type: WindowType, size: Int = type.size) : this(type, arrayOfNulls(size))
}
