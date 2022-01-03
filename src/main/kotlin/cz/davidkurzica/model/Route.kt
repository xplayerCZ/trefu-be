package cz.davidkurzica.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Routes: Table() {
    val id = integer("route_id").autoIncrement()
    val lineFullCode = integer("line_id") references Lines.fullCode
    val length = integer("length")
    val direction = integer("direction")

    override val primaryKey = PrimaryKey(id, name = "PK_Routes")
}

object RouteStops : Table("route_stops") {
    val stopId = integer("stop_id") references Stops.id
    val routeId = integer("route_id") references Routes.id
    val index = integer("index")
    val served = bool("served")

    override val primaryKey = PrimaryKey(stopId, routeId, name = "PK_RouteStops")
}

//Route interface is responsible for RouteStop entries
@Serializable
class NewRoute(
    val id: Int,
    val length: Int,
    val direction: Int,
    val stopIds: List<Int>,
    val servedStopsIds:  List<Int>,
    val lineFullCode: Int
)

@Serializable
class Route(
    val id: Int,
    val length: Int,
    val direction: Int,
    val stops: List<Stop>,
)
