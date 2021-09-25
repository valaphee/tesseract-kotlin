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

package com.valaphee.tesseract

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.inject.Guice
import com.valaphee.tesseract.command.CommandManager
import com.valaphee.tesseract.data.DataModule
import com.valaphee.tesseract.data.block.BlockState
import com.valaphee.tesseract.inventory.item.Item
import com.valaphee.tesseract.inventory.item.stack.Stack
import com.valaphee.tesseract.log.Log4JLogHandler
import com.valaphee.tesseract.log.QueueAppender
import com.valaphee.tesseract.util.LittleEndianByteBufInputStream
import com.valaphee.tesseract.util.getCompoundTag
import com.valaphee.tesseract.util.getInt
import com.valaphee.tesseract.util.getIntOrNull
import com.valaphee.tesseract.util.getJsonArray
import com.valaphee.tesseract.util.getString
import com.valaphee.tesseract.util.getStringOrNull
import com.valaphee.tesseract.util.nbt.NbtInputStream
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.PooledByteBufAllocator
import io.netty.buffer.Unpooled
import jline.Terminal
import jline.TerminalFactory
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
import java.io.InputStreamReader
import java.io.PrintStream
import java.io.PrintWriter
import java.lang.invoke.MethodHandles
import java.util.Base64
import kotlin.concurrent.thread

val defaultSystemIn: InputStream = System.`in`
val defaultSystemOut: PrintStream = System.out
val defaultSystemErr: PrintStream = System.err
val terminal: Terminal = TerminalFactory.get()
var ansi = false
lateinit var reader: ConsoleReader
lateinit var writer: PrintWriter

fun initializeConsole() {
    ansi = System.getProperty("jline.terminal") != UnsupportedTerminal::class.java.name && System.console() != null

    if (ansi) AnsiConsole.systemInstall()

    try {
        reader = ConsoleReader("tesseract", defaultSystemIn, defaultSystemOut, terminal)
    } catch (ex: IOException) {
        try {
            System.setProperty("jline.terminal", UnsupportedTerminal::class.java.name)
            System.setProperty("user.language", "en")
            reader = ConsoleReader("tesseract", defaultSystemIn, defaultSystemOut, terminal)
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

    thread(isDaemon = true, name = "console-writer") {
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
    }

    System.setIn(null)
    System.setOut(IoBuilder.forLogger(logger).setLevel(Level.INFO).buildPrintStream())
    System.setErr(IoBuilder.forLogger(logger).setLevel(Level.ERROR).buildPrintStream())
}

fun main(arguments: Array<String>) {
    val argument = Argument().apply { if (!parse(arguments)) return }
    println(
        """
            ________                                           _____ 
            ___  __/____________________________________ ________  /_
            __  /  _  _ \_  ___/_  ___/  _ \_  ___/  __ `/  ___/  __/
            _  /   /  __/(__  )_(__  )/  __/  /   / /_/ // /__ / /_  
            /_/    \___//____/ /____/ \___//_/    \__,_/ \___/ \__/  
        """.trimIndent()
    )

    initializeConsole()
    initializeLogging()

    val `class` = MethodHandles.lookup().lookupClass()
    val gson = GsonBuilder().create()
    val base64Decoder = Base64.getDecoder()

    run {
        val buffer = PooledByteBufAllocator.DEFAULT.directBuffer()
        try {
            buffer.writeBytes(`class`.getResourceAsStream("/runtime_block_states.dat")!!.readBytes())
            NbtInputStream(ByteBufInputStream(buffer)).use { it.readTag() }?.asCompoundTag()?.get("blocks")?.asListTag()!!.toList().map { it.asCompoundTag()!! }.forEach { BlockState.register(BlockState(it.getString("name"), it.getCompoundTag("states"), it.getInt("version"))) }
        } finally {
            buffer.release()
        }
        BlockState.finish()
    }

    run {
        gson.newJsonReader(InputStreamReader(`class`.getResourceAsStream("/runtime_item_states.json")!!)).use { (gson.fromJson(it, JsonArray::class.java) as JsonArray).map { it.asJsonObject }.forEach { Item.register(it.getString("name"), it.getInt("id")) } }
    }

    run {
        `class`.getResourceAsStream("/biome_definitions.dat")!!.readBytes()
    }

    /*run {
        val buffer = PooledByteBufAllocator.DEFAULT.directBuffer()
        try {
            val data = clazz.getResourceAsStream("/entity_identifiers.dat")!!.readBytes()
            buffer.writeBytes(data)
            NbtInputStream(LittleEndianVarIntByteBufInputStream(buffer)).use { it.readTag() }!!.asCompoundTag()!!.getListTag("idlist").toList().map { it.asCompoundTag()!! }.forEach { ActorTypeRegistry.register(it.getString("id"), it.getInt("rid")) }
            entityIdentifiersPacket = EntityIdentifiersPacket(data)
        } finally {
            buffer.release()
        }
    }*/

    run {
        gson.newJsonReader(InputStreamReader(`class`.getResourceAsStream("/creative_items.json")!!)).use {
            val content = mutableListOf<Stack<*>>()
            (gson.fromJson(it, JsonObject::class.java) as JsonObject).getJsonArray("items").map { it.asJsonObject }.forEach {
                Item.byKeyOrNull(it.getString("id"))?.let { item ->
                    content += Stack(Item.byIdOrNull(item.id)!!, it.getIntOrNull("damage") ?: 0, 1, it.getStringOrNull("nbt_b64")?.let {
                        val buffer = Unpooled.wrappedBuffer(base64Decoder.decode(it))
                        val tag = NbtInputStream(LittleEndianByteBufInputStream(buffer)).use { it.readTag() }?.asCompoundTag()
                        buffer.release()
                        tag
                    }, blockRuntimeId = it.getIntOrNull("blockRuntimeId") ?: 0)
                }
            }
        }
    }

    val guice = Guice.createInjector(DataModule(argument))

    val commandManager = guice.getInstance(CommandManager::class.java)
    if (ansi) reader.prompt = "tesseract> "
    thread(isDaemon = true, name = "console-reader") {
        while (true) {
            try {
                val message = reader.readLine(null, null, null)
                if (message.trim { it <= ' ' }.isNotEmpty()) commandManager.dispatch(message)
            } catch (_: IOException) {
            } catch (_: IndexOutOfBoundsException) {
            }
        }
    }
}

private val ansiErase = Ansi.ansi().cursorToColumn(0).eraseLine().toString().toCharArray()
private val ansiReset = Ansi.ansi().a(Ansi.Attribute.RESET).toString().toCharArray()
