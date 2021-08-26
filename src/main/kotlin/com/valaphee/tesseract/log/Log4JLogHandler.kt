/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
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
