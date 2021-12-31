package cz.davidkurzica.service

import cz.davidkurzica.model.RouteStopDTO
import cz.davidkurzica.service.DatabaseFactory.dbQuery
import cz.davidkurzica.model.RouteStops
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class RouteStopsService
{
    suspend fun get(id: Int) = dbQuery {
        RouteStops.select { RouteStops.id eq id }.mapNotNull { toRouteStop(it) }.singleOrNull()
    }

    suspend fun getByStops(stopId: Int) = dbQuery {
        RouteStops.select { RouteStops.stopId eq stopId }.mapNotNull { toRouteStop(it) }
    }


    suspend fun insert(routeStop: RouteStopDTO) {
        var exists = false
        dbQuery {
            //exists = RouteStops.select { RouteStops.stopId eq timetableStop.stopId and (RouteStops.timetableId eq timetableStop.timetableId) }.mapNotNull { toTimetableStop(it) }.singleOrNull() != null
        }
        if(!exists) {
            dbQuery {
                RouteStops.insert {
                    it[stopId] = routeStop.stopId
                    it[routeId] = routeStop.routeId
                    it[index] = routeStop.routeId
                }
            }
        }
    }

    suspend fun delete(id: Int) = dbQuery { RouteStops.deleteWhere { RouteStops.id eq id } > 0 }

    private fun toRouteStop(row: ResultRow) =
        RouteStopDTO(
            stopId = row[RouteStops.stopId],
            routeId = row[RouteStops.routeId],
            index = row[RouteStops.index]
        )
}