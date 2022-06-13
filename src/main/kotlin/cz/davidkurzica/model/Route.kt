package cz.davidkurzica.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Routes : Table() {
    val id = integer("route_id").autoIncrement()
    val lineId = integer("line_id") references Lines.id
    val length = integer("length")
    val direction = integer("direction")

    override val primaryKey = PrimaryKey(id, name = "PK_Routes")
}

@Serializable
class Route(
    val id: Int,
    val lineId: Int,
    val length: Int,
    val direction: Int
)

@Serializable
class NewRoute(
    val lineId: Int,
    val length: Int,
    val direction: Int
)


@Serializable
class Direction(
    val id: Int,
    val description: String
)