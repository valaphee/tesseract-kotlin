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
 *
 */

package com.valaphee.tesseract.world.scoreboard

/**
 * @author Kevin Ludwig
 */
class Score private constructor(
    val scoreboardId: Long,
    val objectiveName: String,
    val value: Int,
    val type: ScorerType,
    val name: String?,
    val entityId: Long
) {
    enum class ScorerType {
        None, Player, Entity, Fake
    }

    constructor(scoreboardId: Long, objectiveName: String) : this(scoreboardId, objectiveName, 0, ScorerType.None, null, 0)

    constructor(scoreboardId: Long, objectiveName: String, value: Int) : this(scoreboardId, objectiveName, value, ScorerType.None, null, 0)

    constructor(scoreboardId: Long, objectiveName: String, value: Int, name: String) : this(scoreboardId, objectiveName, value, ScorerType.Fake, name, 0)

    constructor(scoreboardId: Long, objectiveName: String, value: Int, type: ScorerType, entityId: Long) : this(scoreboardId, objectiveName, value, type, null, entityId)
}
