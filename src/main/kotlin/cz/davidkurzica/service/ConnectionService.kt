package cz.davidkurzica.service

import cz.davidkurzica.model.Connection
import cz.davidkurzica.model.Connections
import cz.davidkurzica.model.NewConnection
import cz.davidkurzica.util.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

class ConnectionService {

    suspend fun getConnections(
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
        return getConnectionById(key)!!
    }

    suspend fun editConnection(connection: NewConnection, id: Int): Connection {
        dbQuery {
            Connections.update({ Connections.id eq id }) {
                it[routeId] = connection.routeId
                it[number] = connection.number
            }
        }
        return getConnectionById(id)!!
    }

    fun toConnection(row: ResultRow) =
        Connection(
            id = row[Connections.id],
            routeId = row[Connections.routeId],
            number = row[Connections.number]
        )
}
