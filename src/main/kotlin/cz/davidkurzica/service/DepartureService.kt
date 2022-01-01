package cz.davidkurzica.service

import cz.davidkurzica.model.*
import cz.davidkurzica.service.DatabaseFactory.dbQuery
import io.ktor.network.sockets.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import java.time.LocalTime

class DepartureService {
    private val lineService = LineService()
    private val stopService = StopService()

    suspend fun getAll() = dbQuery {
        Departures.selectAll().map { toDeparture(it) }
    }

    suspend fun getByConnectionId(connectionId: Int) = dbQuery {
        Departures.select { Departures.connectionId eq connectionId }.mapNotNull { toDeparture(it) }
    }


    suspend fun getByTimeAndStopId(time: LocalTime, stopId: Int) = dbQuery {
        val placeholder = object {
            val departures = mutableListOf<DepartureSimple>()
            val lengths = mutableListOf<Int>()
            val routeIds = mutableListOf<Int>()
        }

        val results = mutableListOf<DepartureSimple>()

        (Departures innerJoin RouteStops innerJoin Stops innerJoin Routes innerJoin Timetables innerJoin Lines)
            .slice(Departures.time, Lines.shortCode, Routes.length, RouteStops.routeId)
            .select { RouteStops.stopId eq stopId and (Departures.time greater time and((Routes.length - 1) neq RouteStops.index))}
            .orderBy(Departures.time)
            .limit(10).forEach {
                placeholder.departures.add(DepartureSimple(it[Departures.time], it[Lines.shortCode], ""))
                placeholder.lengths.add(it[Routes.length])
                placeholder.routeIds.add(it[RouteStops.routeId])
            }

        placeholder.lengths.forEachIndexed { i, length ->
            results.add((RouteStops innerJoin Stops)
                .slice(Stops.name)
                .select { RouteStops.index eq (length - 1) and (RouteStops.routeId eq placeholder.routeIds[i]) }
                .map { placeholder.departures[i].copy(lastStopName = it[Stops.name]) }.single() )
        }

        results.toList()
    }

    suspend fun insert(departure: DepartureDTO) {
        var routeStopKey = 0
        var connectionKey = 0
        dbQuery {
            routeStopKey = (RouteStops innerJoin Routes innerJoin Timetables)
                .slice(RouteStops.id)
                .select(Timetables.lineId eq departure.lineFullCode and (Timetables.packetId eq departure.packetId and (Timetables.direction eq departure.direction and(RouteStops.index eq departure.index))))
                .mapNotNull { it[RouteStops.id] }.singleOrNull()!!
        }
        dbQuery {
            connectionKey = (RouteStops innerJoin Routes innerJoin Timetables innerJoin Connections)
                .slice(Connections.id)
                .select(Timetables.lineId eq departure.lineFullCode and (Timetables.packetId eq departure.packetId and (Timetables.direction eq departure.direction and(RouteStops.index eq departure.index and(Timetables.id eq Connections.timetableId and(Connections.number eq departure.connectionNumber))))))
                .mapNotNull { it[Connections.id] }.singleOrNull()!!
        }

        dbQuery {
            Departures.insert {
                it[time] = departure.time
                it[connectionId] = connectionKey
                it[routeStopId] = routeStopKey
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
