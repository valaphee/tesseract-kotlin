/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.util

fun lazyToString(initialize: () -> String) = object {
    private val value: String by lazy(initialize)

    override fun toString() = value
}
