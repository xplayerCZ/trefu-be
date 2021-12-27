package cz.davidkurzica.model

import cz.davidkurzica.model.Timetables.references
import cz.davidkurzica.util.LocalTimeSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.joda.time.LocalTime

object Connections: Table() {
    val id = integer("id").autoIncrement()
    val timetableId = integer("timetable_id") references Timetables.id
    val number = integer("number")
    val notes = text("notes")
    val times = text("times") //String Array in CSV format separated by ';'

    override val primaryKey = PrimaryKey(id, name = "PK_Connection_ID")
}

@Serializable
data class Connection(
    val id: Int,
    val timetable: Timetable,
    val number: Int,
    val notes: String,
    val times: List<@Serializable(with = LocalTimeSerializer::class) LocalTime>
)

@Serializable
data class ConnectionDTO(
    val timetableId: Int,
    val number: Int,
    val notes: String,
    val times: List<@Serializable(with = LocalTimeSerializer::class) LocalTime>
)
