package cz.davidkurzica.service

import cz.davidkurzica.db.dbQuery
import cz.davidkurzica.model.GraphNode
import cz.davidkurzica.model.GraphNodes
import cz.davidkurzica.model.Graphs
import cz.davidkurzica.model.NewGraphNode
import org.jetbrains.exposed.sql.*

class GraphNodeService {

    suspend fun getGraphNodes(
        offset: Int?,
        limit: Int?,
        stopId: Int?,
        graphId: Int?,
        packetId: Int?,
    ) = dbQuery {
        val query = GraphNodes.selectAll()

        query.apply {
            limit?.let { limit(it, (offset ?: 0).toLong()) }
            stopId?.let { andWhere { GraphNodes.stopId eq it } }
            graphId?.let { andWhere { GraphNodes.graphId eq it } }
            packetId?.let {
                adjustColumnSet { innerJoin(Graphs) }
                andWhere { Graphs.packetId eq it }
            }
        }

        query.mapNotNull { toGraphNode(it) }
    }

    suspend fun getGraphNodeById(id: Int): GraphNode? = dbQuery {
        GraphNodes.select {
            (GraphNodes.id eq id)
        }.mapNotNull { toGraphNode(it) }
            .singleOrNull()
    }

    suspend fun addGraphNode(graphNode: NewGraphNode): GraphNode {
        var key = 0
        dbQuery {
            key = (GraphNodes.insert {
                it[stopId] = graphNode.stopId
                it[graphId] = graphNode.graphId
            } get GraphNodes.id)
        }
        return getGraphNodeById(key)!!
    }

    suspend fun editGraphNode(graphNode: NewGraphNode, id: Int): GraphNode {
        dbQuery {
            GraphNodes.update({ GraphNodes.id eq id }) {
                it[stopId] = graphNode.stopId
                it[graphId] = graphNode.graphId
            }
        }
        return getGraphNodeById(id)!!
    }

    suspend fun deleteGraphNodeById(id: Int): Boolean {
        var numOfDeletedItems = 0
        dbQuery {
            numOfDeletedItems = GraphNodes.deleteWhere { GraphNodes.id eq id }
        }
        return numOfDeletedItems == 1
    }

    fun toGraphNode(row: ResultRow): GraphNode =
        GraphNode(
            id = row[GraphNodes.id],
            stopId = row[GraphNodes.stopId],
            graphId = row[GraphNodes.graphId],
        )
}