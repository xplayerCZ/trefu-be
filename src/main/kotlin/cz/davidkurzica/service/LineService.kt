package cz.davidkurzica.service

import cz.davidkurzica.model.*
import cz.davidkurzica.service.DatabaseFactory.dbQuery
import cz.davidkurzica.util.selectPacketByPacketId
import cz.davidkurzica.util.selectRoutesByLineFullCode
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class LineService {

    suspend fun getLine(fullCode: Int): Line? = dbQuery {
        Lines.select {
            (Lines.fullCode eq fullCode)
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
            } get Lines.fullCode)
        }
        return getLine(key)!!
    }

    private fun toLine(row: ResultRow): Line =
        Line(
            shortCode = row[Lines.shortCode],
            fullCode = row[Lines.fullCode],
            packet = selectPacketByPacketId(row[Lines.packetId]),
            routes = selectRoutesByLineFullCode(row[Lines.fullCode])
        )
}