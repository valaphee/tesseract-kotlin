/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.actor.player

/**
 * @author Kevin Ludwig
 */
enum class GameMode(
    val key: String = ""
) {
    Survival("Survival"),
    Creative("Creative"),
    Adventure("Adventure"),
    SurvivalViewer,
    CreativeViewer,
    Default("Default"),
    LevelDefault;

    companion object {
        @JvmStatic
        fun byKey(key: String) = byKey[key]

        private val byKey = HashMap<String, GameMode>(values().size).apply { values().forEach { this[it.key] = it } }
    }
}
