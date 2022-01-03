package cz.davidkurzica.service

import cz.davidkurzica.model.*
import cz.davidkurzica.service.DatabaseFactory.dbQuery
import cz.davidkurzica.util.toRoute

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class RouteService {

    suspend fun getRoute(id: Int): Route? = dbQuery {
        Routes.select {
            (Routes.id eq id)
        }.mapNotNull { toRoute(it) }
            .singleOrNull()
    }

    suspend fun addRoute(route: NewRoute): Route {
        var key = 0
        dbQuery {
            key = (Routes.insert {
                it[id] = route.id
                it[length] = route.length
                it[direction] = route.direction
                it[lineFullCode] = route.lineFullCode
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
}