package cz.davidkurzica.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object GraphNodes : Table("graph_nodes") {
    val id = integer("graph_node_id").autoIncrement()
    val stopId = integer("stop_id") references Stops.id
    val graphId = integer("graph_id") references Graphs.id

    override val primaryKey = PrimaryKey(id, name = "PK_GraphNodes")
}

@Serializable
data class GraphNode(
    val id: Int,
    val stopId: Int,
    val graphId: Int,
)

@Serializable
data class NewGraphNode(
    val stopId: Int,
    val graphId: Int,
)