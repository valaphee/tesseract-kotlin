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

package com.valaphee.tesseract.log

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.jul.LevelTranslator
import org.apache.logging.log4j.message.MessageFormatMessage
import java.util.MissingResourceException
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Handler
import java.util.logging.LogRecord

/**
 * @author Kevin Ludwig
 */
class Log4JLogHandler : Handler() {
    private val loggers = ConcurrentHashMap<String, Logger>()

    override fun publish(record: LogRecord) {
        if (!isLoggable(record)) return
        val logger = loggers.computeIfAbsent(record.loggerName ?: "", LogManager::getLogger)
        var message = record.message
        record.resourceBundle?.let {
            try {
                message = it.getString(message)
            } catch (_: MissingResourceException) {
            }
        }
        val level = LevelTranslator.toLevel(record.level)
        if (record.parameters != null && record.parameters.isNotEmpty()) logger.log(level, MessageFormatMessage(message, *record.parameters), record.thrown) else logger.log(level, message, record.thrown)
    }

    override fun flush() {}

    override fun close() {}
}
