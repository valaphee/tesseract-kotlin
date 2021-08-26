/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.foundry.ecs.entity

import com.valaphee.foundry.ecs.Context
import com.valaphee.foundry.ecs.mutator.AttributeMutator
import com.valaphee.foundry.ecs.mutator.BehaviorMutator
import com.valaphee.foundry.ecs.mutator.FacetMutator

/**
 * @author Kevin Ludwig
 */
interface MutableEntity<T : EntityType, C : Context> : Entity<T, C>, AttributeMutator, FacetMutator<C>, BehaviorMutator<C>
