package cz.davidkurzica.service

import cz.davidkurzica.db.dbQuery
import cz.davidkurzica.model.GraphEdge
import cz.davidkurzica.model.GraphEdges
import cz.davidkurzica.model.Graphs
import cz.davidkurzica.model.NewGraphEdge
import org.jetbrains.exposed.sql.*

class GraphEdgeService {
    suspend fun getGraphEdges(
        offset: Int?,
        limit: Int?,
        graphId: Int?,
        a: Int?,
        b: Int?,
        packetId: Int?,
    ) = dbQuery {
        val query = GraphEdges.selectAll()

        query.apply {
            limit?.let { limit(it, (offset ?: 0).toLong()) }
            graphId?.let { andWhere { GraphEdges.graphId eq it } }
            a?.let { andWhere { GraphEdges.a eq it } }
            b?.let { andWhere { GraphEdges.b eq it } }
            packetId?.let {
                adjustColumnSet { innerJoin(Graphs) }
                andWhere { Graphs.packetId eq it }
            }
        }

        query.mapNotNull { toGraphEdge(it) }
    }

    suspend fun getGraphEdgeById(id: Int): GraphEdge? = dbQuery {
        GraphEdges.select {
            (GraphEdges.id eq id)
        }.mapNotNull { toGraphEdge(it) }
            .singleOrNull()
    }

    suspend fun addGraphEdge(graphEdge: NewGraphEdge): GraphEdge {
        var key = 0
        dbQuery {
            key = (GraphEdges.insert {
                it[graphId] = graphEdge.graphId
                it[a] = graphEdge.a
                it[b] = graphEdge.b
            } get GraphEdges.id)
        }
        return getGraphEdgeById(key)!!
    }

    suspend fun editGraphEdge(graphEdge: NewGraphEdge, id: Int): GraphEdge {
        dbQuery {
            GraphEdges.update({ GraphEdges.id eq id }) {
                it[graphId] = graphEdge.graphId
                it[a] = graphEdge.a
                it[b] = graphEdge.b
            }
        }
        return getGraphEdgeById(id)!!
    }

    suspend fun deleteGraphEdgeById(id: Int): Boolean {
        var numOfDeletedItems = 0
        dbQuery {
            numOfDeletedItems = GraphEdges.deleteWhere { GraphEdges.id eq id }
        }
        return numOfDeletedItems == 1
    }

    fun toGraphEdge(row: ResultRow): GraphEdge =
        GraphEdge(
            id = row[GraphEdges.id],
            graphId = row[GraphEdges.graphId],
            a = row[GraphEdges.a],
            b = row[GraphEdges.b],
        )
}