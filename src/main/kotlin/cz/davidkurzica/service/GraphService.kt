package cz.davidkurzica.service

import cz.davidkurzica.db.dbQuery
import cz.davidkurzica.model.Graph
import cz.davidkurzica.model.Graphs
import cz.davidkurzica.model.NewGraph
import cz.davidkurzica.model.Packets
import org.jetbrains.exposed.sql.*

class GraphService {

    suspend fun getGraphs(
        offset: Int?,
        limit: Int?,
        packetId: Int?,
    ) = dbQuery {
        val query = Graphs.selectAll()

        query.apply {
            limit?.let { limit(it, (offset ?: 0).toLong()) }
            packetId?.let { andWhere { Packets.id eq it } }
        }

        query.mapNotNull { toGraph(it) }
    }

    suspend fun getGraphById(id: Int): Graph? = dbQuery {
        Graphs.select {
            (Graphs.id eq id)
        }.mapNotNull { toGraph(it) }
            .singleOrNull()
    }

    suspend fun addGraph(graph: NewGraph): Graph {
        var key = 0
        dbQuery {
            key = (Graphs.insert {
                it[packetId] = graph.packetId
            } get Graphs.id)
        }
        return getGraphById(key)!!
    }

    suspend fun editGraph(graph: NewGraph, id: Int): Graph {
        dbQuery {
            Graphs.update({ Graphs.id eq id }) {
                it[packetId] = graph.packetId
            }
        }
        return getGraphById(id)!!
    }

    suspend fun deleteGraphById(id: Int): Boolean {
        var numOfDeletedItems = 0
        dbQuery {
            numOfDeletedItems = Graphs.deleteWhere { Graphs.id eq id }
        }
        return numOfDeletedItems == 1
    }

    fun toGraph(row: ResultRow): Graph =
        Graph(
            id = row[Graphs.id],
            packetId = row[Graphs.packetId],
        )
}