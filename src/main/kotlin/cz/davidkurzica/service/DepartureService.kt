package cz.davidkurzica.service

import cz.davidkurzica.model.*
import cz.davidkurzica.service.DatabaseFactory.dbQuery
import io.ktor.network.sockets.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DepartureService {
    private val lineService = LineService()
    private val stopService = StopService()

    suspend fun getAll() = dbQuery {
        Departures.selectAll().map { toDeparture(it) }
    }

    suspend fun getByConnectionId(connectionId: Int) =
        Departures.select { Departures.connectionId eq connectionId }.mapNotNull { toDeparture(it) }

    suspend fun insert(departure: DepartureDTO) {
        var routeStopKey = 0
        var connectionKeys = emptyList<Int>()
        dbQuery {
            routeStopKey = (RouteStops innerJoin Routes innerJoin Timetables)
                .slice(RouteStops.id)
                .select(Timetables.lineId eq departure.lineFullCode and (Timetables.packetId eq departure.packetId and (Timetables.direction eq departure.direction and(RouteStops.index eq departure.index))))
                .mapNotNull { it[RouteStops.id] }.singleOrNull()!!
        }
        dbQuery {
            connectionKeys = (RouteStops innerJoin Routes innerJoin Timetables innerJoin Connections)
                .slice(Connections.id)
                .select(Timetables.lineId eq departure.lineFullCode and (Timetables.packetId eq departure.packetId and (Timetables.direction eq departure.direction and(RouteStops.index eq departure.index and(Timetables.id eq Connections.timetableId)))))
                .mapNotNull { it[Connections.id] }
        }

        dbQuery {
            connectionKeys.forEach { key ->
                Departures.insert {
                    it[time] = departure.time
                    it[connectionId] = key
                    it[routeStopId] = routeStopKey
                }
            }
        }
    }

    private suspend fun toDeparture(row: ResultRow) =
        Departure(
            time = row[Departures.time],
            line = lineService.getByRouteStopId(row[Departures.routeStopId])!!,
            stop = stopService.getByRouteStopId(row[Departures.routeStopId])!!
        )
}
