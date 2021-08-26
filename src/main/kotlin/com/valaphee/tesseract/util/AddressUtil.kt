/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.util

import java.net.InetSocketAddress
import java.util.regex.Pattern

fun address(string: CharSequence, defaultPort: Int): InetSocketAddress? {
    converters.forEach { it.to(string, defaultPort)?.let { return it } }
    return null
}

private interface Converter {
    fun to(addressWithPort: CharSequence, defaultPort: Int): InetSocketAddress?
}

private fun parse(string: CharSequence, defaultPort: Int, pattern: Pattern, patternHostIndex: Int, patternPortIndex: Int): InetSocketAddress? {
    val matcher = pattern.matcher(string)
    if (matcher.matches() && matcher.reset().find()) {
        val portString = matcher.group(patternPortIndex)
        var port = defaultPort
        try {
            if (portString != null && portString.isNotEmpty()) port = portString.toInt()
        } catch (_: NumberFormatException) {
        }
        return InetSocketAddress(matcher.group(patternHostIndex), port)
    }
    return null
}

private val hostnamePattern = Pattern.compile("([a-zA-Z][\\w-]*[\\w]*(\\.[a-zA-Z][\\w-]*[\\w]*)*)(:(6553[0-5]|6(55[012]|(5[0-4]|[0-4]\\d)\\d)\\d|[1-5]?\\d{1,4}))?")
private val v4AddressPattern = Pattern.compile("(((25[0-5]|(2[0-4]|1\\d|[1-9]?)\\d)(\\.|\\b)){4}(?<!\\.))(:(6553[0-5]|6(55[012]|(5[0-4]|[0-4]\\d)\\d)\\d|[1-5]?\\d{1,4}))?")
private val v6AddressPattern = Pattern.compile("((((?=(?>.*?::)(?!.*::)))(::)?([0-9a-f]{1,4}::?){0,5}|([0-9a-f]{1,4}:){6})(((25[0-5]|(2[0-4]|1[0-9]|[1-9])?[0-9])(\\.|\\b)){4}|\\3([0-9a-f]{1,4}(::?|\\b)){0,2}|[0-9a-f]{1,4}:[0-9a-f]{1,4})(?<![^:]:)(?<!\\.))(?:([#.])(6553[0-5]|6(55[012]|(5[0-4]|[0-4]\\d)\\d)\\d|[1-5]?\\d{1,4}))?$")
private val bracketV6AddressPattern = Pattern.compile("\\[((((?=(?>.*?::)(?!.*::)))(::)?([0-9a-f]{1,4}::?){0,5}|([0-9a-f]{1,4}:){6})(((25[0-5]|(2[0-4]|1[0-9]|[1-9])?[0-9])(\\.|\\b)){4}|\\3([0-9a-f]{1,4}(::?|\\b)){0,2}|[0-9a-f]{1,4}:[0-9a-f]{1,4})(?<![^:]:)(?<!\\.))](:(6553[0-5]|6(55[012]|(5[0-4]|[0-4]\\d)\\d)\\d|[1-5]?\\d{1,4}))?$")
private val converters: List<Converter> = listOf(
    object : Converter {
        override fun to(addressWithPort: CharSequence, defaultPort: Int) = parse(addressWithPort, defaultPort, hostnamePattern, 1, 5)
    },
    object : Converter {
        override fun to(addressWithPort: CharSequence, defaultPort: Int) = parse(addressWithPort, defaultPort, v4AddressPattern, 1, 7)
    },
    object : Converter {
        override fun to(addressWithPort: CharSequence, defaultPort: Int) = parse(addressWithPort, defaultPort, v6AddressPattern, 1, 15)
    },
    object : Converter {
        override fun to(addressWithPort: CharSequence, defaultPort: Int) = parse(addressWithPort, defaultPort, bracketV6AddressPattern, 1, 15)
    }
)
