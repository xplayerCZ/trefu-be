package cz.davidkurzica.service

import cz.davidkurzica.model.NewRoute
import cz.davidkurzica.model.Route
import cz.davidkurzica.model.Routes
import cz.davidkurzica.util.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

class RouteService {

    suspend fun getRoutes(
        offset: Int?,
        limit: Int?
    ) = dbQuery {
        val query = Routes.selectAll()

        limit?.let {
            query.limit(limit, (offset ?: 0).toLong())
        }

        query.mapNotNull { toRoute(it) }
    }

    suspend fun getRouteById(id: Int): Route? = dbQuery {
        Routes.select {
            (Routes.id eq id)
        }.mapNotNull { toRoute(it) }
            .singleOrNull()
    }

    suspend fun addRoute(route: NewRoute): Route {
        var key = 0
        dbQuery {
            key = (Routes.insert {
                it[lineId] = route.lineId
                it[length] = route.length
                it[direction] = route.direction
            } get Routes.id)
        }
        return getRouteById(key)!!
    }

    suspend fun editRoute(route: NewRoute, id: Int): Route {
        dbQuery {
            Routes.update({ Routes.id eq id }) {
                it[lineId] = route.lineId
                it[length] = route.length
                it[direction] = route.direction
            }
        }
        return getRouteById(id)!!
    }

    fun toRoute(row: ResultRow): Route =
        Route(
            id = row[Routes.id],
            lineId = row[Routes.lineId],
            length = row[Routes.length],
            direction = row[Routes.direction]
        )


    /*
private fun toDirection(row: ResultRow): Direction {
    val stops = selectStopsByRouteId(row[Routes.id])
    return Direction(
        id = row[Routes.direction],
        description = "${stops.first().name} - ${stops.last().name}"
    )
}

suspend fun getDirections(lineId: Int) = dbQuery {
    (Routes innerJoin Lines)
        .slice(Routes.id, Routes.direction)
        .select {
        (Lines.id eq lineId)
    }.mapNotNull { toDirection(it) }
}
 */
}