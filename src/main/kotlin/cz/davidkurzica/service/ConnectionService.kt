package cz.davidkurzica.service

import cz.davidkurzica.model.*
import cz.davidkurzica.util.DatabaseFactory.dbQuery
import cz.davidkurzica.util.toConnection
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class ConnectionService {

    suspend fun filterConnections(
        offset: Int?,
        limit: Int?
    ) = dbQuery {
        val query = Connections.selectAll()

        limit?.let {
            query.limit(limit, (offset ?: 0).toLong())
        }

        query.mapNotNull { toConnection(it) }
    }

    suspend fun getConnectionById(id: Int): Connection? = dbQuery {
        Connections.select {
            (Connections.id eq id)
        }.mapNotNull { toConnection(it) }
            .singleOrNull()
    }

    suspend fun addConnection(connection: NewConnection): Connection {
        var key = 0
        dbQuery {
            key = (Connections.insert {
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
        return getConnectionById(key)!!
    }

    suspend fun editConnection(connection: NewConnection, id: Int): Connection {
        dbQuery {
            Connections.update({Connections.id eq id}) {
                it[routeId] = connection.routeId
                it[number] = connection.number
            }
        }
        return getConnectionById(id)!!
    }
}