package cz.davidkurzica.domain.line

import cz.davidkurzica.db.dbQuery
import cz.davidkurzica.domain.packet.Packets
import org.jetbrains.exposed.sql.*

class LineService {

    suspend fun getLines(
        offset: Int?,
        limit: Int?,
        packetId: Int?,
    ) = dbQuery {
        val query = Lines.selectAll()

        query.apply {
            limit?.let { limit(it, (offset ?: 0).toLong()) }
            packetId?.let {
                adjustColumnSet { innerJoin(Packets) }
                andWhere { Packets.id eq it }
            }
        }

        query.mapNotNull { it.toLine() }
    }

    suspend fun getLineById(id: Int): Line? = dbQuery {
        Lines.select {
            (Lines.id eq id)
        }.mapNotNull { it.toLine() }
            .singleOrNull()
    }


    suspend fun addLine(line: NewLine): Line {
        var key = 0
        dbQuery {
            key = (Lines.insert {
                it[shortCode] = line.shortCode
                it[fullCode] = line.fullCode
                it[packetId] = line.packetId
            } get Lines.id)
        }
        return getLineById(key)!!
    }

    suspend fun editLine(line: NewLine, id: Int): Line {
        dbQuery {
            Lines.update({ Lines.id eq id }) {
                it[shortCode] = line.shortCode
                it[fullCode] = line.fullCode
                it[packetId] = line.packetId
            }
        }
        return getLineById(id)!!
    }

    suspend fun deleteLineById(id: Int): Boolean {
        var numOfDeletedItems = 0
        dbQuery {
            numOfDeletedItems = Lines.deleteWhere { Lines.id eq id }
        }
        return numOfDeletedItems == 1
    }

    private fun ResultRow.toLine(): Line =
        Line(
            id = this[Lines.id],
            shortCode = this[Lines.shortCode],
            fullCode = this[Lines.fullCode],
            packetId = this[Lines.packetId]
        )
}