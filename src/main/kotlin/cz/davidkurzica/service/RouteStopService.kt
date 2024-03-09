package cz.davidkurzica.service

import cz.davidkurzica.db.dbQuery
import cz.davidkurzica.model.Connections
import cz.davidkurzica.model.RouteStop
import cz.davidkurzica.model.RouteStops
import org.jetbrains.exposed.sql.*

class RouteStopService {

    suspend fun getRouteStops(
        offset: Int? = null,
        limit: Int? = null,
        routeId: Int? = null,
        stopId: Int? = null,
        index: Int? = null,
        served: Boolean? = null,
    ) = dbQuery {
        val query = RouteStops.selectAll()

        query.apply {
            limit?.let { limit(it, (offset ?: 0).toLong()) }
            routeId?.let { andWhere { RouteStops.routeId eq it } }
            stopId?.let { andWhere { RouteStops.stopId eq it } }
            index?.let { andWhere { RouteStops.index eq it } }
            served?.let { andWhere { RouteStops.served eq it } }
        }

        query.mapNotNull { it.toRouteStop() }
    }

    suspend fun addRouteStop(routeStop: RouteStop): RouteStop {
        dbQuery {
            RouteStops.insert {
                it[routeId] = routeStop.routeId
                it[stopId] = routeStop.stopId
                it[index] = routeStop.index
                it[served] = routeStop.served
            }
        }
        return getRouteStops(
            routeId = routeStop.routeId,
            stopId = routeStop.stopId,
            index = routeStop.index
        ).single()
    }

    suspend fun deleteRouteStop(routeId: Int, stopId: Int, index: Int): Boolean {
        var numOfDeletedItems = 0
        dbQuery {
            numOfDeletedItems = Connections.deleteWhere {
                (RouteStops.routeId eq routeId)
                    .and(RouteStops.stopId eq stopId)
                    .and(RouteStops.index eq index)
            }
        }
        return numOfDeletedItems == 1
    }

    private fun ResultRow.toRouteStop() =
        RouteStop(
            routeId = this[RouteStops.routeId],
            stopId = this[RouteStops.stopId],
            index = this[RouteStops.index],
            served = this[RouteStops.served]
        )
}