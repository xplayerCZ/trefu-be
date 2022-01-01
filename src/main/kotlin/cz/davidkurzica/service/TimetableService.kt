package cz.davidkurzica.service

import cz.davidkurzica.model.*
import cz.davidkurzica.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

class TimetableService {
    private val stopService = StopService()
    private val lineService = LineService()
    private val packetService = PacketService()
    private val routeStopService = RouteStopsService()
    private val connectionService = ConnectionService()
    private val routeService = RouteService()

    suspend fun get(id: Int) = dbQuery {
        Timetables.select { Timetables.id eq id }.mapNotNull { toTimetable(it) }.singleOrNull()
    }

    suspend fun getAll() = dbQuery {
        Timetables.selectAll().map { toTimetable(it) }
    }

    suspend fun exists(timetable: TimetableDTO) = dbQuery {
        Timetables.select {
            Timetables.packetId eq timetable.packetId and (Timetables.lineId eq timetable.lineId and (Timetables.duringWeekDay eq Timetables.duringWeekDay and (Timetables.valid eq timetable.valid and (Timetables.direction eq timetable.direction))))
        }.singleOrNull() != null
    }

    suspend fun insert(timetable: TimetableDTO) {
        var key = 0
        if(!exists(timetable)) {
            dbQuery {
                key = Timetables.insert {
                    it[packetId] = timetable.packetId
                    it[lineId] = timetable.lineId
                    it[duringWeekDay] = timetable.duringWeekDay
                    it[valid] = timetable.valid
                    it[direction] = timetable.direction
                } get Timetables.id
            }
            dbQuery {
                timetable.connections.forEach {
                    connectionService.update(it, key)
                }
            }
        }
    }

    suspend fun delete(id: Int) = dbQuery { Timetables.deleteWhere { Timetables.id eq id } > 0 }

    private suspend fun toTimetable(row: ResultRow) =
        Timetable(
            id = row[Timetables.id],
            packet = packetService.get(row[Timetables.packetId])!!,
            line = lineService.get(row[Timetables.lineId])!!,
            duringWeekDay = row[Timetables.duringWeekDay],
            connections = connectionService.getByTimetable(row[Timetables.id]),
            valid = row[Timetables.valid],
            direction = row[Timetables.direction]
        )
}