package cz.davidkurzica.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table


object Timetables: Table() {
    val id = integer("id").autoIncrement()
    val packetId = integer("packet_id") references Packets.id
    val lineId = integer("line_id") references Lines.fullCode
    val duringWeekDay = bool("duringWeekDay")
    val valid = bool("valid")

    override val primaryKey = PrimaryKey(id, name = "PK_Timetables_ID")
}

@Serializable
data class Timetable(
    val id: Int,
    val packet: Packet,
    val line: Line,
    val duringWeekDay: Boolean,
    val stops: List<Stop>,
    val connections: List<Connection>,
    val valid: Boolean
)

@Serializable
data class TimetableDTO(
    val packetId: Int,
    val lineId: Int,
    val duringWeekDay: Boolean,
    val stopIds: List<Int>,
    val connections: List<ConnectionDTO>,
    val valid: Boolean
)