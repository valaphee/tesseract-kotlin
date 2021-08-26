/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.world.terrain

const val MortonBits = 24
const val MortonXyz = 256
const val MortonMaximumDepth = MortonBits / 3
const val Morton = 2 shl (MortonBits - 1)
