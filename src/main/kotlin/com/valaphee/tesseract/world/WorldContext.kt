/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world

import com.google.inject.Injector
import com.valaphee.foundry.ecs.Context
import com.valaphee.foundry.ecs.entity.Entity
import com.valaphee.foundry.ecs.entity.EntityType
import com.valaphee.tesseract.util.ecs.EntityFactory
import com.valaphee.tesseract.world.provider.Provider
import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSuperclassOf

/**
 * @author Kevin Ludwig
 */
class WorldContext(
    val injector: Injector,
    val coroutineScope: CoroutineScope,
    val engine: WorldEngine,
    val entityFactory: EntityFactory<WorldContext>,
    val provider: Provider
) : Context {
    val world = provider.loadWorld() ?: entityFactory(WorldType, setOf()).also(engine::addEntity)

    var cycleDelta = 0.0f
}

typealias EntityOfWorld<T> = Entity<T, WorldContext>

typealias AnyEntityOfWorld = EntityOfWorld<out EntityType>

@Suppress("UNCHECKED_CAST")
inline fun <reified T : EntityType> Array<AnyEntityOfWorld>.filterType() = filter { T::class.isSuperclassOf(it.type::class) }.toList() as List<Entity<T, WorldContext>>

@Suppress("UNCHECKED_CAST")
inline fun <reified T : EntityType> Iterable<AnyEntityOfWorld>.filterType() = filter { T::class.isSuperclassOf(it.type::class) }.toList() as List<Entity<T, WorldContext>>

inline fun <reified T : EntityType> AnyEntityOfWorld.whenTypeIs(`do`: (Entity<T, WorldContext>) -> Unit) {
    if (this.type::class.isSubclassOf(T::class)) {
        @Suppress("UNCHECKED_CAST")
        `do`(this as Entity<T, WorldContext>)
    }
}
