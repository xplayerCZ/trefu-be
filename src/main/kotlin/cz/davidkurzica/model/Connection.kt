package cz.davidkurzica.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Connections : Table() {
    val id = integer("connection_id").autoIncrement()
    val routeId = integer("route_id") references Routes.id
    val number = integer("number")

    override val primaryKey = PrimaryKey(id, name = "PK_Connections")
}

@Serializable
class Connection(
    val id: Int,
    val routeId: Int,
    val number: Int,
)

@Serializable
class NewConnection(
    val routeId: Int,
    val number: Int,
)

@Serializable
data class ConnectionItem(
    val connectionsParts: List<ConnectionItemPart>,
)

@Serializable
data class ConnectionItemPart(
    val lineShortCode: String,
    val from: DepartureSimple,
    val to: DepartureSimple,
)