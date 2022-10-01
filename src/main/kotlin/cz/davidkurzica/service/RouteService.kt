package cz.davidkurzica.service

import cz.davidkurzica.db.dbQuery
import cz.davidkurzica.model.*
import org.jetbrains.exposed.sql.*

class RouteService {

    suspend fun getRoutes(
        offset: Int?,
        limit: Int?,
        lineId: Int?,
        packetId: Int?,
    ) = dbQuery {
        val query = Routes.selectAll()

        query.apply {
            limit?.let { limit(it, (offset ?: 0).toLong()) }
            lineId?.let { andWhere { Routes.lineId eq it } }
            packetId?.let {
                adjustColumnSet { innerJoin(Lines) }
                adjustColumnSet { innerJoin(Packets) }
                andWhere { Packets.id eq it }
            }
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
            } get Routes.id)
        }
        return getRouteById(key)!!
    }

    suspend fun editRoute(route: NewRoute, id: Int): Route {
        dbQuery {
            Routes.update({ Routes.id eq id }) {
                it[lineId] = route.lineId
                it[length] = route.length
            }
        }
        return getRouteById(id)!!
    }

    suspend fun deleteRouteById(id: Int): Boolean {
        var numOfDeletedItems = 0
        dbQuery {
            numOfDeletedItems = Routes.deleteWhere { Routes.id eq id }
        }
        return numOfDeletedItems == 1
    }

    fun toRoute(row: ResultRow): Route =
        Route(
            id = row[Routes.id],
            lineId = row[Routes.lineId],
            length = row[Routes.length],
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