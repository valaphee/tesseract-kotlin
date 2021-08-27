/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.util

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException

fun JsonObject.getBoolOrNull(key: String): Boolean? {
    val jsonElement = this[key] ?: return null
    if (jsonElement.isJsonPrimitive && jsonElement.asJsonPrimitive.isBoolean) return jsonElement.asBoolean
    throw JsonSyntaxException("Expected $key to be a bool")
}

fun JsonObject.getBool(key: String): Boolean {
    val jsonElement = this[key] ?: throw JsonSyntaxException("Missing $key, expected to find a bool")
    if (jsonElement.isJsonPrimitive && jsonElement.asJsonPrimitive.isBoolean) return jsonElement.asBoolean
    throw JsonSyntaxException("Expected $key to be a bool")
}

fun JsonObject.getIntOrNull(key: String): Int? {
    val jsonElement = this[key] ?: return null
    if (jsonElement.isJsonPrimitive && jsonElement.asJsonPrimitive.isNumber) return jsonElement.asInt
    throw JsonSyntaxException("Expected $key to be an int")
}

fun JsonObject.getInt(key: String): Int {
    val jsonElement = this[key] ?: throw JsonSyntaxException("Missing $key, expected to find an int")
    if (jsonElement.isJsonPrimitive && jsonElement.asJsonPrimitive.isNumber) return jsonElement.asInt
    throw JsonSyntaxException("Expected $key to be an int")
}

fun JsonObject.getLongOrNull(key: String): Long? {
    val jsonElement = this[key] ?: return null
    if (jsonElement.isJsonPrimitive && jsonElement.asJsonPrimitive.isNumber) return jsonElement.asLong
    throw JsonSyntaxException("Expected $key to be a long")
}

fun JsonObject.getLong(key: String): Long {
    val jsonElement = this[key] ?: throw JsonSyntaxException("Missing $key, expected to find a long")
    if (jsonElement.isJsonPrimitive && jsonElement.asJsonPrimitive.isNumber) return jsonElement.asLong
    throw JsonSyntaxException("Expected $key to be a long")
}

fun JsonObject.getFloatOrNull(key: String): Float? {
    val jsonElement = this[key] ?: return null
    if (jsonElement.isJsonPrimitive && jsonElement.asJsonPrimitive.isNumber) return jsonElement.asFloat
    throw JsonSyntaxException("Expected $key to be a float")
}

fun JsonObject.getFloat(key: String): Float {
    val jsonElement = this[key] ?: throw JsonSyntaxException("Missing $key, expected to find a float")
    if (jsonElement.isJsonPrimitive && jsonElement.asJsonPrimitive.isNumber) return jsonElement.asFloat
    throw JsonSyntaxException("Expected $key to be a float")
}

fun JsonObject.getDoubleOrNull(key: String): Double? {
    val jsonElement = this[key] ?: return null
    if (jsonElement.isJsonPrimitive && jsonElement.asJsonPrimitive.isNumber) return jsonElement.asDouble
    throw JsonSyntaxException("Expected $key to be a double")
}

fun JsonObject.getDouble(key: String): Double {
    val jsonElement = this[key]
        ?: throw JsonSyntaxException("Missing $key, expected to find a double")
    if (jsonElement.isJsonPrimitive && jsonElement.asJsonPrimitive.isNumber) return jsonElement.asDouble
    throw JsonSyntaxException("Expected $key to be a double")
}

fun JsonObject.getStringOrNull(key: String): String? {
    val jsonElement = this[key] ?: return null
    if (jsonElement.isJsonPrimitive && jsonElement.asJsonPrimitive.isString) return jsonElement.asString
    throw JsonSyntaxException("Expected $key to be a string")
}

fun JsonObject.getString(key: String): String {
    val jsonElement = this[key] ?: throw JsonSyntaxException("Missing $key, expected to find a string")
    if (jsonElement.isJsonPrimitive && jsonElement.asJsonPrimitive.isString) return jsonElement.asString
    throw JsonSyntaxException("Expected $key to be a string")
}

fun JsonObject.getJsonArrayOrNull(key: String): JsonArray? {
    val jsonElement = this[key] ?: return null
    if (jsonElement.isJsonArray) return jsonElement.asJsonArray
    throw JsonSyntaxException("Expected $key to be a json array")
}

fun JsonObject.getJsonArray(key: String): JsonArray {
    val jsonElement = this[key] ?: throw JsonSyntaxException("Missing $key, expected to find a json array")
    if (jsonElement.isJsonArray) return jsonElement.asJsonArray
    throw JsonSyntaxException("Expected $key to be a json array")
}

fun JsonObject.getJsonObjectOrNull(key: String): JsonObject? {
    val jsonElement = this[key] ?: return null
    if (jsonElement.isJsonObject) return jsonElement.asJsonObject
    throw JsonSyntaxException("Expected $key to be a json object")
}

fun JsonObject.getJsonObject(key: String): JsonObject {
    val jsonElement = this[key] ?: throw JsonSyntaxException("Missing $key, expected to find a json object")
    if (jsonElement.isJsonObject) return jsonElement.asJsonObject
    throw JsonSyntaxException("Expected $key to be a json object")
}
