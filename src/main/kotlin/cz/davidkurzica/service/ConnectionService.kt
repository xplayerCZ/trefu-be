package cz.davidkurzica.service

import cz.davidkurzica.model.*
import cz.davidkurzica.util.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

class ConnectionService {

    suspend fun getConnections(
        offset: Int?,
        limit: Int?,
        routeId: Int?,
        packetId: Int?,
    ) = dbQuery {
        val query = Connections.selectAll()

        limit?.let { query.limit(it, (offset ?: 0).toLong()) }
        routeId?.let { query.andWhere { Connections.routeId eq it } }
        packetId?.let {
            query.adjustColumnSet {
                innerJoin(Routes, { Routes.id }, { Connections.routeId })
                innerJoin(Lines, { Lines.id }, { Routes.lineId })
                innerJoin(Packets, { Packets.id }, { Lines.packetId })
            }.andWhere { Packets.id eq it }
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

    suspend fun deleteConnectionById(id: Int): Boolean {
        var numOfDeletedItems = 0
        dbQuery {
            numOfDeletedItems = Connections.deleteWhere { Connections.id eq id }
        }
        return numOfDeletedItems == 1
    }

    fun toConnection(row: ResultRow) =
        Connection(
            id = row[Connections.id],
            routeId = row[Connections.routeId],
            number = row[Connections.number]
        )
}
