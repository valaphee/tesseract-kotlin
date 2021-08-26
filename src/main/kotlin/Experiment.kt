/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

import com.valaphee.tesseract.net.PacketBuffer

/**
 * @author Kevin Ludwig
 */
data class Experiment(
    var name: String,
    var enabled: Boolean
)

fun PacketBuffer.readExperiment(): Experiment = Experiment(readString(), readBoolean())

fun PacketBuffer.writeExperiment(value: Experiment) {
    writeString(value.name)
    writeBoolean(value.enabled)
}
