package cz.davidkurzica.service

import cz.davidkurzica.model.Lines
import cz.davidkurzica.service.DatabaseFactory.dbQuery
import cz.davidkurzica.model.Timetable
import cz.davidkurzica.model.TimetableDTO
import cz.davidkurzica.model.Timetables
import org.jetbrains.exposed.sql.*

class TimetableService {
    private val stopService = StopService()
    private val lineService = LineService()
    private val packetService = PacketService()
    private val timetableStopService = TimetableStopService()

    suspend fun get(id: Int) = dbQuery {
        Timetables.select { Timetables.id eq id }.mapNotNull { toTimetable(it) }.singleOrNull()
    }

    suspend fun getAll() = dbQuery {
        Timetables.selectAll().map { toTimetable(it) }
    }

    //TODO: Fix get callback
    suspend fun insert(timetable: TimetableDTO) {
        dbQuery {
            Timetables.insert {
                it[packetId] = timetable.packetId
                it[lineId] = timetable.lineId
                it[duringWeekDay] = timetable.duringWeekDay
            }
        }
    }

    suspend fun delete(id: Int) = dbQuery { Timetables.deleteWhere { Timetables.id eq id } > 0 }

    private suspend fun toTimetable(row: ResultRow) =
        Timetable(
            id = row[Timetables.id],
            packet = packetService.get(row[Timetables.packetId])!!,
            line = lineService.get(row[Timetables.lineId])!!,
            stops = timetableStopService.getByTimetables(row[Timetables.id]).map { stopService.get(it.stopId)!! },
            duringWeekDay = row[Timetables.duringWeekDay],
        )
}