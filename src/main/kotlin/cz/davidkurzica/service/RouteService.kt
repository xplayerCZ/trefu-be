package cz.davidkurzica.service

import cz.davidkurzica.model.*
import cz.davidkurzica.service.DatabaseFactory.dbQuery
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*

class RouteService {

    suspend fun get(id: Int) = dbQuery {
        Routes.select { Routes.id eq id }.mapNotNull { toResult(it) }.singleOrNull()
    }

    suspend fun getAll() = dbQuery {
        Routes.selectAll().map { toResult(it) }
    }

    suspend fun insert(route: RouteDTO) {
        var timetableKey = 0
        var routeKey = 0
        dbQuery {
            timetableKey = Timetables.slice(Timetables.id)
                .select { Timetables.direction eq route.direction and (Timetables.lineId eq route.lineFullCode)}
                .mapNotNull { it[Timetables.id] }.singleOrNull()!!
        }
        dbQuery {
            routeKey = Routes.insert {
                it[timetableId] = timetableKey
            } get Routes.id
        }
        dbQuery {
            route.stopIds.forEachIndexed { i, item ->
                RouteStops.insert {
                    it[index] = i
                    it[routeId] = routeKey
                    it[stopId] = item
                }
            }
        }
    }

    suspend fun delete(id: Int) = dbQuery { Routes.deleteWhere { Routes.id eq id } > 0 }

    private suspend fun toResult(row: ResultRow) {

    }

}