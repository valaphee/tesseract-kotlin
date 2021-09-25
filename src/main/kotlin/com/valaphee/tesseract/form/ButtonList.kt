/*
 * Copyright (c) 2021, GrieferGames, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.form

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.JsonElement

/**
 * @author Kevin Ludwig
 */
class ButtonList(
    title: String,
    @get:JsonProperty("content") val content: String,
    @get:JsonProperty("buttons") val buttons: List<Button>
) : Form<String>(title) {
    override fun getResponse(json: JsonElement) = if (json.asInt >= buttons.size) null else buttons[json.asInt].id

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ButtonList

        if (title != other.title) return false
        if (content != other.content) return false
        if (buttons != other.buttons) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + buttons.hashCode()
        return result
    }

    override fun toString() = "ButtonList(title=$title, content=$content, buttons=$buttons)"
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class Button(
    @JsonIgnore val id: String,
    @get:JsonProperty("text") val text: String
)
