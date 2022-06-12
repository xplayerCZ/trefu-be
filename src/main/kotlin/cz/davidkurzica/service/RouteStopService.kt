package cz.davidkurzica.service

import cz.davidkurzica.model.*
import cz.davidkurzica.util.DatabaseFactory
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class RouteStopService {

    suspend fun getRouteStops(
        offset: Int? = null,
        limit: Int? = null,
        routeId: Int? = null,
        stopId: Int? = null,
    ) = DatabaseFactory.dbQuery {
        val query = RouteStops.selectAll()

        limit?.let {
            query.limit(limit, (offset ?: 0).toLong())
        }
        routeId?.let {
            query.andWhere { RouteStops.routeId eq routeId }
        }
        stopId?.let {
            query.andWhere { RouteStops.stopId eq stopId }
        }

        query.mapNotNull { toRouteStop(it) }
    }

    suspend fun addRouteStop(routeStop: RouteStop): RouteStop {
        DatabaseFactory.dbQuery {
            RouteStops.insert {
                it[routeId] = routeStop.routeId
                it[stopId] = routeStop.stopId
                it[index] = routeStop.index
                it[served] = routeStop.served
            }
        }
        return getRouteStops(
            routeId = routeStop.routeId,
            stopId = routeStop.stopId
        ).single()
    }

    fun toRouteStop(row: ResultRow) =
        RouteStop(
            routeId = row[RouteStops.routeId],
            stopId = row[RouteStops.stopId],
            index = row[RouteStops.index],
            served = row[RouteStops.served]
        )
}