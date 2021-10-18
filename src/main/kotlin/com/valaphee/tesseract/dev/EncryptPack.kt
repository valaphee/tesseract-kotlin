package com.valaphee.tesseract.dev

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import com.valaphee.tesseract.pack.Content
import java.io.File

/**
 * @author Kevin Ludwig
 */
class EncryptPack @Inject constructor(
    private val objectMapper: ObjectMapper
) : Tool {
    override fun run() {
        val path = File("pack")
        objectMapper.writeValue(File(path, "contents.json"), Content(path.walkTopDown().filter { it.isFile }.map { Content.Entry(it.path.replace('\\', '/'), null) }.toList()))
    }

    override fun destroy() = Unit
}
