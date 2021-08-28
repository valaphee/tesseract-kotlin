/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.player

import com.google.gson.JsonObject
import com.valaphee.foundry.ecs.BaseAttribute
import com.valaphee.tesseract.util.getString
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
data class AuthExtra(
    var userId: UUID,
    var xboxUserId: String,
    var userName: String
) : BaseAttribute() {
    fun toJson(json: JsonObject) {
        json.addProperty("identity", userId.toString())
        json.addProperty("XUID", xboxUserId)
        json.addProperty("displayName", userName)
    }
}

val JsonObject.asAuthExtra get() = AuthExtra(UUID.fromString(getString("identity")), getString("XUID"), getString("displayName"))

val Player.authExtra get() = findAttribute(AuthExtra::class)
