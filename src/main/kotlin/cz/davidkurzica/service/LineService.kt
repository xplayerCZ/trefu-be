package cz.davidkurzica.service

import cz.davidkurzica.model.*
import cz.davidkurzica.service.DatabaseFactory.dbQuery
import cz.davidkurzica.util.selectPacketByPacketId
import cz.davidkurzica.util.selectRoutesByLineId
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class LineService {

    suspend fun getAll(): List<Line> = dbQuery {
        Lines.selectAll().mapNotNull { toLine(it) }
    }

    suspend fun getLine(id: Int): Line? = dbQuery {
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
        return getLine(key)!!
    }

    private fun toLine(row: ResultRow): Line =
        Line(
            id = row[Lines.id],
            shortCode = row[Lines.shortCode],
            fullCode = row[Lines.fullCode],
            packet = selectPacketByPacketId(row[Lines.packetId]),
            routes = selectRoutesByLineId(row[Lines.id])
        )
}