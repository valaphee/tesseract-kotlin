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

package com.valaphee.tesseract.data.locale

import com.fasterxml.jackson.annotation.JsonIgnore
import com.valaphee.tesseract.data.Component
import com.valaphee.tesseract.data.Data
import com.valaphee.tesseract.data.Keyed
import java.text.MessageFormat
import java.util.regex.Pattern

/**
 * @author Kevin Ludwig
 */
@Component("tesseract:locale")
class LocaleData(
    override val key: String,
    val entries: Map<String, String>
) : Data, Keyed {
    @JsonIgnore
    private val formats = mutableMapOf<String, MessageFormat>()

    operator fun get(key: String) = entries[key]

    fun format(key: String, vararg arguments: Any?) = this[key]?.let {
        (formats[key] ?: (try {
            MessageFormat(it)
        } catch (_: IllegalArgumentException) {
            MessageFormat(pattern.matcher(it).replaceAll("\\[$1\\]"))
        }).also { formats[key] = it }).format(arguments)
    } ?: key

    companion object {
        private val pattern = Pattern.compile("\\{(\\D*?)}")
    }
}
