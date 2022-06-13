package cz.davidkurzica.service

import cz.davidkurzica.model.Line
import cz.davidkurzica.model.Lines
import cz.davidkurzica.model.NewLine
import cz.davidkurzica.util.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

class LineService {

    suspend fun getLines(
        offset: Int?,
        limit: Int?
    ) = dbQuery {
        val query = Lines.selectAll()

        limit?.let {
            query.limit(limit, (offset ?: 0).toLong())
        }

        query.mapNotNull { toLine(it) }
    }

    suspend fun getLineById(id: Int): Line? = dbQuery {
        Lines.select {
            (Lines.id eq id)
        }.mapNotNull { toLine(it) }
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

    private fun toLine(row: ResultRow): Line =
        Line(
            id = row[Lines.id],
            shortCode = row[Lines.shortCode],
            fullCode = row[Lines.fullCode],
            packetId = row[Lines.packetId]
        )

    /*


private fun toLineItem(row: ResultRow): LineItem =
    LineItem(
        id = row[Lines.id],
        shortCode = row[Lines.shortCode],
    )


suspend fun getDetails(stopId: Int, date: LocalDate) = dbQuery {
    (Lines innerJoin Routes innerJoin RouteStops innerJoin Packets)
        .slice(Lines.id, Lines.shortCode)
        .select {
            (RouteStops.stopId eq stopId)
                .and (Packets.valid eq true)
                .and (Packets.from lessEq date)
                .and (Packets.to greaterEq date)
        }
        .map { toLineItem(it) }
        .distinctBy { it.shortCode }
}

 */
}