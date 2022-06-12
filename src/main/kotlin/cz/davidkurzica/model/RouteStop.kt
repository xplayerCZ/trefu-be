package cz.davidkurzica.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object RouteStops : Table("route_stops") {
    val stopId = integer("stop_id") references Stops.id
    val routeId = integer("route_id") references Routes.id
    val index = integer("index")
    val served = bool("served")

    override val primaryKey = PrimaryKey(stopId, routeId, index, name = "PK_RouteStops")
}

@Serializable
class RouteStop(
    val stopId: Int,
    val routeId: Int,
    val index: Int,
    val served: Boolean
)