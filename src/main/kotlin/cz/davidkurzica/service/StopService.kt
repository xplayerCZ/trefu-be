package cz.davidkurzica.service

import cz.davidkurzica.db.dbQuery
import cz.davidkurzica.model.*
import org.jetbrains.exposed.sql.*

class StopService {

    suspend fun getStops(
        offset: Int?,
        limit: Int?,
        packetId: Int?,
    ) = dbQuery {
        val query = Stops.selectAll()

        query.apply {
            limit?.let { limit(it, (offset ?: 0).toLong()) }
            packetId?.let {
                adjustColumnSet { innerJoin(RouteStops) }
                adjustColumnSet { innerJoin(Routes) }
                adjustColumnSet { innerJoin(Lines) }
                adjustColumnSet { innerJoin(Packets) }
                andWhere { Packets.id eq it }
            }
            withDistinct(true)
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

    suspend fun deleteStopById(id: Int): Boolean {
        var numOfDeletedItems = 0
        dbQuery {
            numOfDeletedItems = Stops.deleteWhere { Stops.id eq id }
        }
        return numOfDeletedItems == 1
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