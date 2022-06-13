package cz.davidkurzica.service

import cz.davidkurzica.model.NewStop
import cz.davidkurzica.model.Stop
import cz.davidkurzica.model.Stops
import cz.davidkurzica.util.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

class StopService {

    suspend fun getStops(
        offset: Int?,
        limit: Int?
    ) = dbQuery {
        val query = Stops.selectAll()

        limit?.let {
            query.limit(limit, (offset ?: 0).toLong())
        }

        query.mapNotNull { toStop(it) }
    }

    suspend fun getStopById(id: Int): Stop? = dbQuery {
        Stops.select {
            (Stops.id eq id)
        }.mapNotNull { toStop(it) }
            .singleOrNull()
    }

    suspend fun addStop(stop: NewStop): Stop {
        var key = 0
        dbQuery {
            key = (Stops.insert {
                it[name] = stop.name
                it[latitude] = stop.latitude
                it[longitude] = stop.longitude
                it[code] = stop.code
            } get Stops.id)
        }
        return getStopById(key)!!
    }

    suspend fun editStop(stop: NewStop, id: Int): Stop {
        dbQuery {
            Stops.update({ Stops.id eq id }) {
                it[name] = stop.name
                it[latitude] = stop.latitude
                it[longitude] = stop.longitude
                it[code] = stop.code
            }
        }
        return getStopById(id)!!
    }

    private fun toStop(row: ResultRow): Stop =
        Stop(
            id = row[Stops.id],
            name = row[Stops.name],
            latitude = row[Stops.latitude],
            longitude = row[Stops.longitude],
            code = row[Stops.code]
        )

    /*
    
    suspend fun getAllDetail(): List<StopItem> = dbQuery {
        Stops
            .slice(Stops.id, Stops.name)
            .selectAll()
            .mapNotNull { toStopItem(it) }
    }
    
    private fun toStopItem(row: ResultRow): StopItem =
        StopItem(
            id = row[Stops.id],
            name = row[Stops.name],
            enabled = true
        )
        
     */
}