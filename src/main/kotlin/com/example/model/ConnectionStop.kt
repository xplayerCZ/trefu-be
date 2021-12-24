package com.example.model

import org.jetbrains.exposed.sql.Table

object ConnectionStop : Table() {
    val stopId = integer("stop_id") references Stops.id
    val connectionId = integer("connection_id") references Connections.id

    override val primaryKey = PrimaryKey(stopId, connectionId, name = "PK_ConnectionStop")
}