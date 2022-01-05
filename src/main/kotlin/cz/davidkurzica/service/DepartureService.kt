package cz.davidkurzica.service

import cz.davidkurzica.model.*
import cz.davidkurzica.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import java.time.LocalDate
import java.time.LocalTime

class DepartureService {
    suspend fun get(time: LocalTime, stopId: Int, date: LocalDate): List<DepartureItem> = dbQuery {
        (Departures innerJoin Connections innerJoin Routes innerJoin RouteStops innerJoin Lines innerJoin Packets)
            .slice(Lines.shortCode, Departures.time, Routes.length, Routes.id)
            .select {
                (Packets.valid eq true)
                    .and (Packets.from lessEq date)
                    .and (Packets.to greaterEq date)
                    .and (Departures.time greater time)
                    .and (RouteStops.stopId eq stopId)
            }
            .orderBy(Departures.time)
            .limit(10)
            .map {
                toDepartureItem(it)
            }
    }

    private fun toDepartureItem(row: ResultRow) =
        DepartureItem(
            time = row[Departures.time],
            lineShortCode = row[Lines.shortCode],
            lastStopName = getLastStopName(row[Routes.id], row[Routes.length] - 1)
        )

    private fun getLastStopName(routeId: Int, index: Int) =
        (Stops innerJoin RouteStops)
            .slice(Stops.name)
            .select {
                (RouteStops.routeId eq routeId)
                    .and(RouteStops.index eq index)
            }
            .mapNotNull { it[Stops.name] }
            .single()
}
