package cz.davidkurzica.domain.route

import cz.davidkurzica.domain.line.Lines
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Routes : Table() {
    val id = integer("route_id").autoIncrement()
    val lineId = integer("line_id") references Lines.id
    val length = integer("length")

    override val primaryKey = PrimaryKey(id, name = "PK_Routes")
}

@Serializable
class Route(
    val id: Int,
    val lineId: Int,
    val length: Int,
)

@Serializable
class NewRoute(
    val lineId: Int,
    val length: Int,
)