/*
 * Copyright (c) 2021, GrieferGames, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.player

import com.google.gson.JsonObject
import com.valaphee.tesseract.util.address
import com.valaphee.tesseract.util.getBoolOrNull
import com.valaphee.tesseract.util.getIntOrNull
import com.valaphee.tesseract.util.getLong
import com.valaphee.tesseract.util.getString
import java.net.InetSocketAddress
import java.util.Locale
import java.util.UUID

/**
 * @author Kevin Ludwig
 */
data class User(
    val selfSignedId: UUID,
    val clientId: Long,
    val thirdPartyName: String,
    val thirdPartyNameOnly: Boolean,
    val appearance: Appearance,
    val platformOfflineId: String,
    val platformOnlineId: String,
    val deviceId: String,
    val deviceModel: String,
    val operatingSystem: OperatingSystem,
    val version: String,
    val locale: Locale,
    val defaultInputMode: InputMode,
    val currentInputMode: InputMode,
    val guiScale: Int,
    val uiProfile: UiProfile,
    val serverAddress: InetSocketAddress?
) {
    enum class OperatingSystem {
        Unknown,
        Android,
        Ios,
        Osx,
        FireOs,
        GearVr,
        Hololens,
        Windows10,
        Windows32,
        Dedicated,
        Tv,
        Orbis,
        Nx,
        XboxOne,
        WindowsPhone
    }

    enum class InputMode {
        Unknown, KeyboardAndMouse, Touchscreen, GamePad, MotionControlled
    }

    enum class UiProfile {
        Classic, Pocket, Unknown2, Unknown3
    }

    fun toJson(json: JsonObject) {
        json.addProperty("SelfSignedId", selfSignedId.toString())
        json.addProperty("ClientRandomId", clientId)
        json.addProperty("ThirdPartyName", thirdPartyName)
        json.addProperty("ThirdPartyNameOnly", thirdPartyNameOnly)
        appearance.toJson(json)
        json.addProperty("PlatformOfflineId", platformOfflineId)
        json.addProperty("PlatformOnlineId", platformOnlineId)
        json.addProperty("DeviceId", deviceId)
        json.addProperty("DeviceModel", deviceModel)
        json.addProperty("DeviceOS", operatingSystem.ordinal)
        json.addProperty("GameVersion", version)
        json.addProperty("LanguageCode", locale.toString())
        json.addProperty("DefaultInputMode", defaultInputMode.ordinal)
        json.addProperty("CurrentInputMode", currentInputMode.ordinal)
        json.addProperty("GuiScale", guiScale)
        json.addProperty("UIProfile", uiProfile.ordinal)
        json.addProperty("ServerAddress", serverAddress.toString())
    }
}

val JsonObject.asUser
    get() = User(
        UUID.fromString(getString("SelfSignedId")),
        getLong("ClientRandomId"),
        getString("ThirdPartyName"),
        getBoolOrNull("ThirdPartyNameOnly") ?: false,
        asAppearance,
        getString("PlatformOfflineId"),
        getString("PlatformOnlineId"),
        getString("DeviceId"),
        getString("DeviceModel"),
        User.OperatingSystem.values()[getIntOrNull("DeviceOS") ?: 0],
        getString("GameVersion"),
        Locale.forLanguageTag(getString("LanguageCode").replace('_', '-')),
        User.InputMode.values()[getIntOrNull("DefaultInputMode") ?: 0],
        User.InputMode.values()[getIntOrNull("CurrentInputMode") ?: 0],
        getIntOrNull("GuiScale") ?: 0,
        User.UiProfile.values()[getIntOrNull("UIProfile") ?: 0],
        address(getString("ServerAddress"), 19132)
    )
