/*
 * Copyright (c) 2021, GrieferGames, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.form

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.google.gson.JsonElement

/**
 * @author Kevin Ludwig
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(Modal::class, name = "modal"),
    JsonSubTypes.Type(ButtonList::class, name = "form"),
    JsonSubTypes.Type(CustomForm::class, name = "custom_form")
)
abstract class Form<T>(
    @get:JsonProperty("title") val title: String
) {
    abstract fun getResponse(json: JsonElement): T?
}
