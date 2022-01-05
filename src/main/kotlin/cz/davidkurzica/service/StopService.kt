package cz.davidkurzica.service

import cz.davidkurzica.model.NewStop
import cz.davidkurzica.model.Stop
import cz.davidkurzica.model.StopItem
import cz.davidkurzica.model.Stops
import cz.davidkurzica.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class StopService {

    suspend fun getAll(): List<StopItem> = dbQuery {
        Stops
            .slice(Stops.id, Stops.name)
            .selectAll()
            .mapNotNull { toStopItem(it) }
    }

    suspend fun getStop(id: Int): Stop? = dbQuery {
        Stops.select {
            (Stops.id eq id)
        }.mapNotNull { toStop(it) }
            .singleOrNull()
    }

    suspend fun addStop(stop: NewStop): Stop {
        var key = 0
        dbQuery {
            key = (Stops.insert {
                it[id] = stop.id
                it[name] = stop.name
                it[latitude] = stop.latitude
                it[longitude] = stop.longitude
                it[code] = stop.code
            } get Stops.id)
        }
        return getStop(key)!!
    }

    private fun toStop(row: ResultRow): Stop =
        Stop(
            id = row[Stops.id],
            name = row[Stops.name],
            latitude = row[Stops.latitude],
            longitude = row[Stops.longitude],
            code = row[Stops.code]
        )

    private fun toStopItem(row: ResultRow): StopItem =
        StopItem(
            id = row[Stops.id],
            name = row[Stops.name],
            enabled = true
        )
}