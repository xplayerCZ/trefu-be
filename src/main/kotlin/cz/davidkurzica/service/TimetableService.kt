package cz.davidkurzica.service

import cz.davidkurzica.model.*
import cz.davidkurzica.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

class TimetableService {
    private val stopService = StopService()
    private val lineService = LineService()
    private val packetService = PacketService()
    private val timetableStopService = TimetableStopService()
    private val connectionService = ConnectionService()

    suspend fun get(id: Int) = dbQuery {
        Timetables.select { Timetables.id eq id }.mapNotNull { toTimetable(it) }.singleOrNull()
    }

    suspend fun getAll() = dbQuery {
        Timetables.selectAll().map { toTimetable(it) }
    }

    suspend fun insert(timetable: TimetableDTO) {
        var key = 0
        dbQuery {
            key = Timetables.insert {
                it[packetId] = timetable.packetId
                it[lineId] = timetable.lineId
                it[duringWeekDay] = timetable.duringWeekDay
                it[valid] = timetable.valid
            } get Timetables.id
        }
        dbQuery {
            timetable.connections.forEach {
                connectionService.update(it, key)
            }
        }
        dbQuery {
            timetable.stopIds.forEach {
                timetableStopService.insert(TimetableStopDTO(it, key))
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
            connections = connectionService.getByTimetable(row[Timetables.id]),
            valid = row[Timetables.valid]
        )
}