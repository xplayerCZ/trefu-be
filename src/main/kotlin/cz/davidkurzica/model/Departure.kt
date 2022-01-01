package cz.davidkurzica.model

import cz.davidkurzica.model.Connections.autoIncrement
import cz.davidkurzica.model.Connections.references
import cz.davidkurzica.util.LocalTimeSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.time
import java.time.LocalTime


object Departures: Table() {
    val id = integer("id").autoIncrement()
    val connectionId = integer("connection_id") references Connections.id
    val routeStopId = integer("route_stop_id") references RouteStops.id
    val time = time("time").nullable()

    override val primaryKey = PrimaryKey(id, name = "PK_Departures_ID")
}

@Serializable
data class Departure(
    val time: @Serializable(with = LocalTimeSerializer::class) LocalTime?,
    val line: Line,
    val stop: Stop,
)

@Serializable
data class DepartureDTO(
    val time: @Serializable(with = LocalTimeSerializer::class) LocalTime?,
    val connectionNumber: Int,
    val direction: Int,
    val lineFullCode: Int,
    val packetId: Int,
    val index: Int,
)

@Serializable
data class DepartureSimple(
    val time: @Serializable(with = LocalTimeSerializer::class) LocalTime?,
    val lineShortCode: String,
    val lastStopName: String,
)

