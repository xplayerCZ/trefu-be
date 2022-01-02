package cz.davidkurzica.service

import cz.davidkurzica.model.*
import cz.davidkurzica.service.DatabaseFactory.dbQuery
import cz.davidkurzica.util.toConnection
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class ConnectionService {

    suspend fun getConnection(id: Int): Connection? = dbQuery {
        Connections.select {
            (Connections.id eq id)
        }.mapNotNull { toConnection(it) }
            .singleOrNull()
    }

    suspend fun addConnection(connection: NewConnection): Connection {
        var key = 0
        dbQuery {
            key = (Connections.insert {
                it[id] = connection.id
                it[routeId] = connection.routeId
                it[number] = connection.number
            } get Connections.id)
        }
        dbQuery {
            connection.departureTimes.forEachIndexed { _index, departureTime ->
                Departures.insert {
                    it[connectionId] = key
                    it[time] = departureTime
                    it[index] = _index
                }
            }
        }
        dbQuery {
            connection.ruleIds.forEach { _ruleId ->
                ConnectionRules.insert {
                    it[connectionId] = key
                    it[ruleId] = _ruleId
                }
            }
        }
        return getConnection(key)!!
    }
}