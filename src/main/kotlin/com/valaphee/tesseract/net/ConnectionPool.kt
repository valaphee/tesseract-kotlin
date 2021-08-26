/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.net

import com.google.inject.Singleton

/**
 * @author Kevin Ludwig
 */
@Singleton
class ConnectionPool {
    private val connections = mutableListOf<Connection>()

    fun add(connection: Connection) {
        connections.add(connection)
    }

    fun remove(connection: Connection) {
        connections.remove(connection)
    }
}
