package cz.davidkurzica.service

import cz.davidkurzica.model.*
import cz.davidkurzica.util.DatabaseFactory.dbQuery
import cz.davidkurzica.util.selectStopsByRouteId
import cz.davidkurzica.util.toRoute
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class RouteService {

    private suspend fun getRoute(id: Int): Route? = dbQuery {
        Routes.select {
            (Routes.id eq id)
        }.mapNotNull { toRoute(it) }
            .singleOrNull()
    }

    suspend fun getDirections(lineId: Int) = dbQuery {
        (Routes innerJoin Lines)
            .slice(Routes.id, Routes.direction)
            .select {
            (Lines.id eq lineId)
        }.mapNotNull { toDirection(it) }
    }

    suspend fun addRoute(route: NewRoute): Route {
        var key = 0
        dbQuery {
            key = (Routes.insert {
                it[length] = route.stopIds.size
                it[direction] = route.direction
                it[lineId] =  route.lineId
            } get Routes.id)
        }
        dbQuery {
            route.stopIds.forEachIndexed { _index, _stopId ->
                RouteStops.insert {
                    it[stopId] = _stopId
                    it[routeId] = key
                    it[served] = route.servedStopsIds.contains(_stopId)
                    it[index] = _index
                }
            }
        }
        return getRoute(key)!!
    }

    private fun toDirection(row: ResultRow): Direction {
        val stops = selectStopsByRouteId(row[Routes.id])
        return Direction(
            id = row[Routes.direction],
            description = "${stops.first().name} - ${stops.last().name}"
        )
    }
}