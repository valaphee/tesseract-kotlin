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

import com.valaphee.foundry.ecs.system.BaseBehavior
import com.valaphee.foundry.math.Float3
import kotlin.random.Random

/**
 * @author Kevin Ludwig
 */
class EnvironmentUpdater : BaseBehavior<WorldContext>(Environment::class) {
    override suspend fun update(entity: AnyEntityOfWorld, context: WorldContext): Boolean {
        entity.whenTypeIs<WorldType> {
            val environment = it.environment

            environment.time++

            fun isRaining() = environment.rainLevel > 0.0f

            fun setRaining(raining: Boolean) {
                if (raining) {
                    environment.rainLevel = 1.0f
                    environment.rainTime = Random.nextInt(12000) + 12000
                    it.broadcast(WorldEventPacket(WorldEventPacket.Event.StartRaining, Float3.Zero, environment.rainTime))
                } else {
                    environment.rainLevel = 0.0f
                    environment.rainTime = Random.nextInt(168000) + 12000
                    it.broadcast(WorldEventPacket(WorldEventPacket.Event.StopRaining, Float3.Zero, environment.rainTime))
                }
            }

            fun isThundering() = environment.thunderLevel > 0.0f

            fun setThundering(thundering: Boolean) {
                if (thundering) {
                    if (!isRaining()) setRaining(true)

                    environment.thunderLevel = 1.0f
                    environment.thunderTime = Random.nextInt(12000) + 12000
                    it.broadcast(WorldEventPacket(WorldEventPacket.Event.StartThunderstorm, Float3.Zero, environment.rainTime))
                } else {
                    environment.thunderLevel = 0.0f
                    environment.thunderTime = Random.nextInt(168000) + 3600
                    it.broadcast(WorldEventPacket(WorldEventPacket.Event.StopThunderstorm, Float3.Zero, environment.rainTime))
                }
            }

            environment.rainTime--
            if (environment.rainTime <= 0) setRaining(!isRaining())

            environment.thunderTime--
            if (environment.thunderTime <= 0) setThundering(!isThundering())
        }

        return true
    }
}
