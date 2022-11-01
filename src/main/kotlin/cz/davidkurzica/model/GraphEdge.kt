package cz.davidkurzica.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object GraphEdges : Table("graph_edges") {
    val id = integer("graph_edge_id").autoIncrement()
    val graphId = (integer("graph_id") references Graphs.id).index()
    val a = (integer("a") references GraphNodes.id).index()
    val b = (integer("b") references GraphNodes.id).index()

    override val primaryKey = PrimaryKey(id, name = "PK_GraphEdges")
}

@Serializable
data class GraphEdge(
    val id: Int,
    val graphId: Int,
    val a: Int,
    val b: Int,
)

@Serializable
data class NewGraphEdge(
    val graphId: Int,
    val a: Int,
    val b: Int,
)