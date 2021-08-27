/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

/**
 * @author Kevin Ludwig
 */
data class PotionMixRecipe(
    var inputId: Int,
    var inputSubId: Int,
    var reagentId: Int,
    var reagentSubId: Int,
    var outputId: Int,
    var outputSubId: Int
) {
    constructor(inputId: Int, reagentId: Int, outputId: Int) : this(inputId, 0, reagentId, 0, outputId, 0)
}
