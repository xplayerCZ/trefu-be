package cz.davidkurzica.service

import cz.davidkurzica.model.*
import cz.davidkurzica.util.DatabaseFactory.dbQuery
import cz.davidkurzica.util.selectPacketByPacketId
import cz.davidkurzica.util.selectRoutesByLineId
import org.jetbrains.exposed.sql.*
import java.time.LocalDate

class LineService {

    suspend fun getAll(): List<Line> = dbQuery {
        Lines.selectAll().mapNotNull { toLine(it) }
    }

    private suspend fun getLine(id: Int): Line? = dbQuery {
        Lines.select {
            (Lines.id eq id)
        }.mapNotNull { toLine(it) }
            .singleOrNull()
    }

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

    private fun toLineItem(row: ResultRow): LineItem =
        LineItem(
            id = row[Lines.id],
            shortCode = row[Lines.shortCode],
        )
}