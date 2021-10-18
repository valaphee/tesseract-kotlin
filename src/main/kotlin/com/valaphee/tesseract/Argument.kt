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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import com.valaphee.tesseract.data.Config
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import java.io.File

/**
 * @author Kevin Ludwig
 */
class Argument {
    lateinit var config: File
        private set

    fun parse(arguments: Array<String>): Boolean {
        val options = Options().apply {
            addOption(
                Option.builder("c")
                    .desc("use a different configuration")
                    .longOpt("config")
                    .type(File::class.java)
                    .hasArg()
                    .build()
            )
        }
        return try {
            val commandLine = DefaultParser().parse(options, arguments)
            config = if (commandLine.hasOption("config")) commandLine.getParsedOptionValue("config") as File else File("config.json")
            true
        } catch (ex: ParseException) {
            HelpFormatter().printHelp("tesseract", options, true)
            false
        }
    }
}

/**
 * @author Kevin Ludwig
 */
class ArgumentModule(
    private val argument: Argument
) : AbstractModule() {
    @Singleton
    @Provides
    fun argument() = argument

    @Singleton
    @Provides
    fun config(objectMapper: ObjectMapper) = (if (argument.config.exists()) objectMapper.readValue(argument.config) else Config()).also { objectMapper.writeValue(argument.config, it) }
}
