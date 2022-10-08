package cz.davidkurzica.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Graphs : Table() {
    val id = integer("graph_id").autoIncrement()
    val packetId = integer("packet_id") references Packets.id

    override val primaryKey = PrimaryKey(id, name = "PK_Graphs")
}

@Serializable
data class Graph(
    val id: Int,
    val packetId: Int,
)

@Serializable
data class NewGraph(
    val packetId: Int,
)