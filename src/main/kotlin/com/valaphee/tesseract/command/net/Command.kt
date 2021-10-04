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

package com.valaphee.tesseract.command.net

/**
 * @author Kevin Ludwig
 */
data class Command(
    val name: String,
    val description: String,
    val flags: Collection<Flag>,
    val permission: Permission,
    val aliases: Enumeration?,
    val overloads: Array<Array<Parameter>>
) {
    enum class Flag {
        Usage, Visible, Synchronized, Executable, Type, Cheat, Unknown6
    }

    class Structure(
        val name: String,
        val description: String,
        val flags: Collection<Flag>,
        val permission: Permission,
        val aliasesIndex: Int,
        val overloadStructures: Array<Array<Parameter.Structure>>
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Command

        if (name != other.name) return false
        if (description != other.description) return false
        if (flags != other.flags) return false
        if (permission != other.permission) return false
        if (aliases != other.aliases) return false
        if (!overloads.contentDeepEquals(other.overloads)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + flags.hashCode()
        result = 31 * result + permission.hashCode()
        result = 31 * result + (aliases?.hashCode() ?: 0)
        result = 31 * result + overloads.contentDeepHashCode()
        return result
    }
}
