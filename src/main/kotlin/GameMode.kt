/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

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
    WorldDefault;

    companion object {
        @JvmStatic
        fun byKey(key: String) = byKey[key]

        private val byKey = HashMap<String, GameMode>(values().size).apply { values().forEach { this[it.key] = it } }
    }
}