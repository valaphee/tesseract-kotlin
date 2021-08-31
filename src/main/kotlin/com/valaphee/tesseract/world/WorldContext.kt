/*
 * MIT License
 *
 * Copyright (c) 2021, Valaphee.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
    val world = provider.loadWorld() ?: entityFactory(WorldType, emptySet()).also(engine::addEntity)

    var cycle = 0L
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
