/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net

/**
 * @author Kevin Ludwig
 */
interface ProtocolHandler {
    fun initialize() {}

    fun exceptionCaught(cause: Throwable) {}

    fun writabilityChanged() {}

    fun destroy() {}
}
