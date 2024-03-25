package cz.davidkurzica.domain.route

import cz.davidkurzica.db.dbQuery
import cz.davidkurzica.domain.line.Lines
import cz.davidkurzica.domain.packet.Packets
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

        query.mapNotNull { it.toRoute() }
    }

    suspend fun getRouteById(id: Int): Route? = dbQuery {
        Routes.select {
            (Routes.id eq id)
        }.mapNotNull { it.toRoute() }
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

    private fun ResultRow.toRoute(): Route =
        Route(
            id = this[Routes.id],
            lineId = this[Routes.lineId],
            length = this[Routes.length],
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