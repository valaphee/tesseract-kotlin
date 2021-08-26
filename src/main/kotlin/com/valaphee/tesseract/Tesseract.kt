/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract

import com.valaphee.tesseract.log.Log4JLogHandler
import com.valaphee.tesseract.log.QueueAppender
import jline.UnsupportedTerminal
import jline.console.ConsoleReader
import jline.console.CursorBuffer
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.core.appender.ConsoleAppender
import org.apache.logging.log4j.io.IoBuilder
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole
import java.io.IOException
import java.io.InputStream
import java.io.PrintStream
import java.io.PrintWriter

val defaultSystemIn: InputStream = System.`in`
val defaultSystemOut: PrintStream = System.out
val defaultSystemErr: PrintStream = System.err
var ansi = false
lateinit var reader: ConsoleReader
lateinit var writer: PrintWriter

fun initializeConsole() {
    ansi = System.getProperty("jline.terminal") != UnsupportedTerminal::class.java.name && System.console() != null

    if (ansi) AnsiConsole.systemInstall()

    try {
        reader = ConsoleReader()
    } catch (ex: IOException) {
        try {
            System.setProperty("jline.terminal", UnsupportedTerminal::class.java.name)
            System.setProperty("user.language", "en")
            reader = ConsoleReader()
            ansi = false
        } catch (_: IOException) {
        }
    } finally {
        reader.expandEvents = false
    }
}

fun initializeLogging() {
    writer = if (::reader.isInitialized) PrintWriter(reader.output) else PrintWriter(defaultSystemOut)

    val julLogger = java.util.logging.Logger.getLogger("")
    julLogger.useParentHandlers = false
    julLogger.handlers.forEach { julLogger.removeHandler(it) }
    julLogger.level = java.util.logging.Level.ALL
    julLogger.addHandler(Log4JLogHandler())

    val logger = LogManager.getRootLogger() as Logger
    logger.appenders.values.forEach { if (it is ConsoleAppender) logger.removeAppender(it) }

    Thread({
        while (true) {
            val message = QueueAppender.getMessage()
            if (ansi) {
                val stashed: CursorBuffer = reader.cursorBuffer.copy()
                writer.write(ansiErase)
                writer.flush()
                writer.write(message)
                writer.write(ansiReset)
                try {
                    reader.resetPromptLine(reader.prompt, stashed.toString(), stashed.cursor)
                } catch (_: IOException) {
                    reader.cursorBuffer.clear()
                } catch (_: IndexOutOfBoundsException) {
                    reader.cursorBuffer.clear()
                }
            } else {
                writer.write(message)
                writer.flush()
            }
        }
    }, "console-writer").apply {
        isDaemon = true
    }.start()

    System.setIn(null)
    System.setOut(IoBuilder.forLogger(logger).setLevel(Level.INFO).buildPrintStream())
    System.setErr(IoBuilder.forLogger(logger).setLevel(Level.ERROR).buildPrintStream())
}

private val ansiErase = Ansi.ansi().cursorToColumn(0).eraseLine().toString().toCharArray()
private val ansiReset = Ansi.ansi().a(Ansi.Attribute.RESET).toString().toCharArray()
