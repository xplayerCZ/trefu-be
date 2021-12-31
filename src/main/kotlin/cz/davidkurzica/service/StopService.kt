package cz.davidkurzica.service

import cz.davidkurzica.model.*
import cz.davidkurzica.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class StopService {

    suspend fun get(id: Int) = dbQuery {
        Stops.select { Stops.id eq id }.mapNotNull { toStop(it) }.singleOrNull()
    }

    suspend fun getAll() = dbQuery {
        Stops.selectAll().map { toStop(it) }
    }

    suspend fun getAllByTimetableId(timetableId: Int) = dbQuery {
        (Stops innerJoin RouteStops innerJoin Timetables)
            .slice(Stops.id, Stops.name, Stops.code, Stops.latitude, Stops.longitude)
            .select { Timetables.id eq timetableId }
            .mapNotNull { toStop(it) }
    }

    suspend fun getByRouteStopId(routeStopId: Int) = dbQuery {
        (Stops innerJoin RouteStops)
            .slice(Stops.id, Stops.name, Stops.code, Stops.latitude, Stops.longitude)
            .select { RouteStops.id eq routeStopId }
            .mapNotNull { toStop(it) }
            .singleOrNull()
    }

    suspend fun insert(stop: Stop): Stop {
        var key = 0
        dbQuery {
            key = Stops.insert {
                it[id] = stop.id
                it[name] = stop.name
                it[latitude] = stop.latitude
                it[longitude] = stop.longitude
                it[code] = stop.code
            } get Stops.id
        }
        return get(key)!!
    }

    suspend fun update(stop: Stop): Stop? {
        val id = stop.id
        var exists = false
        dbQuery {
            exists = Stops.select { Stops.id eq id }.singleOrNull() != null
        }
        return if (!exists) {
            insert(stop)
        } else {
            return null
        }
    }

    suspend fun delete(id: Int) = dbQuery { Stops.deleteWhere { Stops.id eq id } > 0 }

    private fun toStop(row: ResultRow) =
        Stop(
            id = row[Stops.id],
            name = row[Stops.name],
            latitude = row[Stops.latitude],
            longitude = row[Stops.longitude],
            code = row[Stops.code]
        )
}