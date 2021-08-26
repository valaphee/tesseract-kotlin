/*
 * Copyright (c) 2021, GrieferGames, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.player

import com.google.gson.JsonObject
import com.valaphee.tesseract.util.getString
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
data class AuthExtra(
    var id: UUID,
    var xboxUserId: String,
    var name: String
) {
    fun toJson(json: JsonObject) {
        json.addProperty("identity", id.toString())
        json.addProperty("XUID", xboxUserId)
        json.addProperty("displayName", name)
    }
}

val JsonObject.asAuthExtra get() = AuthExtra(UUID.fromString(getString("identity")), getString("XUID"), getString("displayName"))
