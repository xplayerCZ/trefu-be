package cz.davidkurzica.model

import org.jetbrains.exposed.sql.Table

object Graphs : Table() {
    val id = integer("graph_id").autoIncrement()
    val packetId = integer("packet_id") references Packets.id

    override val primaryKey = PrimaryKey(id, name = "PK_Graphs")
}