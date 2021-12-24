package cz.davidkurzica.model

import org.jetbrains.exposed.sql.Table
import org.joda.time.LocalTime

object Connections: Table() {
    val id = integer("id").autoIncrement()
    val number = integer("number")
    val notes = text("notes")
    val lineId = integer("line_id") references Lines.id
    val duringWeekDay = bool("duringWeekDay")
    val times = text("times") //String Array in CSV format separated by ';'

    override val primaryKey = PrimaryKey(id, name = "PK_Connection_ID")
}

class Connection(
    val id: Int?,
    val number: Int,
    val notes: String,
    val line: Line,
    val duringWeekDay: Boolean,
    val times: List<LocalTime>
)