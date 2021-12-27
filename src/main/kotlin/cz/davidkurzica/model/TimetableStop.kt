package cz.davidkurzica.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object TimetableStops : Table("timetable_stops") {
    val stopId = integer("stop_id") references Stops.id
    val timetableId = integer("connection_id") references Timetables.id

    override val primaryKey = PrimaryKey(stopId, timetableId, name = "PK_TimetableStop")
}

@Serializable
data class TimetableStopDTO(
    val stopId: Int,
    val timetableId: Int,
)