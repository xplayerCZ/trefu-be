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
        var timetableKeys = emptyList<Int>()
        var routeKeys = mutableListOf<Int>()
        dbQuery {
            timetableKeys = Timetables.slice(Timetables.id)
                .select { Timetables.direction eq route.direction and (Timetables.lineId eq route.lineFullCode)}
                .mapNotNull { it[Timetables.id] }
        }
        dbQuery {
            val routeKey = Routes.insert {
                it[length] = route.stopIds.size
            } get Routes.id
            timetableKeys.forEach { key ->
                Timetables.update( { Timetables.id eq key} ) {
                    it[routeId] = routeKey
                }
            }
            routeKeys.add(routeKey)
        }
        dbQuery {
            route.stopIds.forEachIndexed { i, item ->
                routeKeys.forEach { routeKey ->
                    RouteStops.insert {
                        it[index] = i
                        it[routeId] = routeKey
                        it[stopId] = item
                    }
                }
            }
        }
    }

    suspend fun delete(id: Int) = dbQuery { Routes.deleteWhere { Routes.id eq id } > 0 }

    private suspend fun toResult(row: ResultRow) {

    }

}