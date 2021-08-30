/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract

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
            addOption(Option.builder("c")
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
