/*
 * Copyright (c) 2021, GrieferGames, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net.form

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.JsonElement

/**
 * @author Kevin Ludwig
 */
class Modal(
    title: String,
    @get:JsonProperty("content") val content: String,
    @get:JsonProperty("button1") val yesButtonText: String,
    @get:JsonProperty("button2") val noButtonText: String
) : Form<Boolean>(title) {
    override fun getResponse(json: JsonElement) = json.asBoolean

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Modal

        if (title != other.title) return false
        if (content != other.content) return false
        if (yesButtonText != other.yesButtonText) return false
        if (noButtonText != other.noButtonText) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + yesButtonText.hashCode()
        result = 31 * result + noButtonText.hashCode()
        return result
    }

    override fun toString() = "Modal(title=$title, content=$content, yesButtonText=$yesButtonText, noButtonText=$noButtonText)"
}
