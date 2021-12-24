package cz.davidkurzica.service

import cz.davidkurzica.service.DatabaseFactory.dbQuery
import cz.davidkurzica.model.Stop
import cz.davidkurzica.model.Stops
import org.jetbrains.exposed.sql.*

class StopService {

    suspend fun get(id: Int) = dbQuery {
        Stops.select { Stops.id eq id }.mapNotNull { toStop(it) }.singleOrNull()
    }

    suspend fun getAll() = dbQuery {
        Stops.selectAll().map { toStop(it) }
    }

    suspend fun insert(stop: Stop): Stop {
        var key = 0
        dbQuery {
            key = Stops.insert {
                it[name] = stop.name
                it[latitude] = stop.latitude
                it[longitude] = stop.longitude
                it[code] = stop.code
            } get Stops.id
        }
        return get(key)!!
    }

    suspend fun update(stop: Stop): Stop? {
        val id = stop.id!!
        dbQuery {
            Stops.update({ Stops.id eq id }) {
                it[name] = stop.name
                it[latitude] = stop.latitude
                it[longitude] = stop.longitude
                it[code] = stop.code
            }
        }
        return get(id)
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