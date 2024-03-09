package cz.davidkurzica.service

import cz.davidkurzica.db.dbQuery
import cz.davidkurzica.model.*
import org.jetbrains.exposed.sql.*

class ConnectionService {

    suspend fun getConnections(
        offset: Int?,
        limit: Int?,
        routeId: Int?,
        packetId: Int?,
    ) = dbQuery {
        val query = Connections.selectAll()

        query.apply {
            limit?.let { limit(it, (offset ?: 0).toLong()) }
            routeId?.let { andWhere { Connections.routeId eq it } }
            packetId?.let {
                adjustColumnSet { innerJoin(Routes) }
                adjustColumnSet { innerJoin(Lines) }
                adjustColumnSet { innerJoin(Packets) }
                andWhere { Packets.id eq it }
            }
        }

        query.mapNotNull { it.toConnection() }
    }

    suspend fun getConnectionById(id: Int): Connection? = dbQuery {
        Connections.select {
            (Connections.id eq id)
        }.mapNotNull { it.toConnection() }
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

    private fun ResultRow.toConnection() =
        Connection(
            id = this[Connections.id],
            routeId = this[Connections.routeId],
            number = this[Connections.number]
        )
}
