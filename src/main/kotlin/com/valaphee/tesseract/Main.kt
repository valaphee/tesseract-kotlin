/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract

import com.google.inject.Guice

fun main() {
    initializeConsole()
    initializeLogging()

    val instance = ServerInstance(Guice.createInjector())
    instance.bind()

    Thread({ Thread.sleep(0x7FFFFFFFFFFFFFFFL) }, "infinisleeper").apply {
        isDaemon = false
        start()
    }
}
