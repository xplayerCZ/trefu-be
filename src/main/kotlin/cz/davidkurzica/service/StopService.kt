package cz.davidkurzica.service

import cz.davidkurzica.model.Lines
import cz.davidkurzica.service.DatabaseFactory.dbQuery
import cz.davidkurzica.model.Stop
import cz.davidkurzica.model.StopDTO
import cz.davidkurzica.model.Stops
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class StopService {

    suspend fun get(code: Int) = dbQuery {
        Stops.select { Stops.code eq code }.mapNotNull { toStop(it) }.singleOrNull()
    }

    suspend fun getAll() = dbQuery {
        Stops.selectAll().map { toStop(it) }
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
            } get Stops.code
        }
        return get(key)!!
    }

    suspend fun update(stop: Stop): Stop? {
        val code = stop.code
        var exists = false
        dbQuery {
            exists = Stops.select { Stops.code eq code }.singleOrNull() != null
        }
        return if (!exists) {
            insert(stop)
        } else {
            return null
        }
    }

    suspend fun delete(code: Int) = dbQuery { Stops.deleteWhere { Stops.code eq code } > 0 }

    private fun toStop(row: ResultRow) =
        Stop(
            id = row[Stops.id],
            name = row[Stops.name],
            latitude = row[Stops.latitude],
            longitude = row[Stops.longitude],
            code = row[Stops.code]
        )
}