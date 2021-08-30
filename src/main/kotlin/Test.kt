
import com.valaphee.foundry.ecs.entity.Entity
import com.valaphee.foundry.ecs.entity.EntityType
import com.valaphee.foundry.ecs.system.BaseBehavior
import com.valaphee.foundry.math.Int2
import com.valaphee.tesseract.world.WorldContext
import com.valaphee.tesseract.world.chunk.ChunkType
import com.valaphee.tesseract.world.chunk.position
import com.valaphee.tesseract.world.chunk.terrain.block.BlockState
import com.valaphee.tesseract.world.chunk.terrain.terrain
import com.valaphee.tesseract.world.whenTypeIs

/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

class Test : BaseBehavior<WorldContext>() {
    private var fmx = 0

    override suspend fun update(entity: Entity<out EntityType, WorldContext>, context: WorldContext): Boolean {
        if (fmx++ > 20) {
            fmx = 0
        } else {
            return true
        }

        entity.whenTypeIs<ChunkType> {
            if (it.position != Int2.Zero) return true
            //it.terrain.cartesianDelta[128, 255, 128] = Material.Water.id
            it.terrain.blockUpdates[7, 100, 7] = flowingWaterId
            it.terrain.blockUpdates[15, 100, 15] = flowingWaterId2

        }

        return true
    }
}

private val flowingWaterId = BlockState.byKeyWithStates("minecraft:flowing_water")?.id ?: error("Missing minecraft:flowing_water")
private val flowingWaterId2 = BlockState.byKeyWithStates("minecraft:flowing_lava")?.id ?: error("Missing minecraft:flowing_water")
