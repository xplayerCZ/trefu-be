package cz.davidkurzica.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object RouteStops : Table("route_stops") {
    val id = integer("id").autoIncrement()
    val stopId = integer("stop_id") references Stops.id
    val routeId = integer("route_id") references Routes.id
    val index = integer("index")

    override val primaryKey = PrimaryKey(id, name = "PK_RouteStop_ID")
}

@Serializable
data class RouteStopDTO(
    val stopId: Int,
    val routeId: Int,
    val index: Int
)
