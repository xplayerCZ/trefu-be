package cz.davidkurzica.domain.departure

import cz.davidkurzica.db.dbQuery
import cz.davidkurzica.domain.connection.Connections
import cz.davidkurzica.domain.connectionrule.ConnectionRules
import cz.davidkurzica.domain.line.Lines
import cz.davidkurzica.domain.packet.Packets
import cz.davidkurzica.domain.route.Routes
import cz.davidkurzica.domain.routestop.RouteStops
import org.jetbrains.exposed.sql.*
import java.time.LocalTime

class DepartureService {

    suspend fun getDepartures(
        offset: Int?,
        limit: Int?,
        connectionId: Int?,
        index: Int?,
        after: LocalTime?,
        before: LocalTime?,
        packetId: Int?,
        stopId: Int?,
        lineId: Int?,
        routeId: Int?,
        ruleId: Int?,
    ) = dbQuery {
        val query = Departures.selectAll()
            .orderBy(Departures.time)

        query.apply {
            adjustJoinToPackets()

            limit?.let { limit(it, (offset ?: 0).toLong()) }
            connectionId?.let { andWhere { Departures.connectionId eq it } }
            index?.let { andWhere { Departures.index eq it } }
            after?.let {
                andWhere { Departures.time greaterEq it }
            }
            before?.let {
                andWhere { Departures.time lessEq it }
            }
            packetId?.let {
                andWhere { Packets.id eq it }
            }
            lineId?.let {
                andWhere { Lines.id eq it }
            }
            routeId?.let {
                andWhere { Routes.id eq it }
            }
            stopId?.let {
                adjustJoinToStops()
                andWhere { RouteStops.stopId eq it }
                andWhere { RouteStops.index eq Departures.index }
            }
            ruleId?.let {
                adjustJoinToRules()
                andWhere { ConnectionRules.ruleId eq it }
            }
        }

        query.mapNotNull { it.toDeparture() }
    }

    suspend fun getDepartureById(id: Int): Departure? = dbQuery {
        Departures.select {
            (Departures.id eq id)
        }.mapNotNull { it.toDeparture() }
            .singleOrNull()
    }

    suspend fun addDeparture(departure: NewDeparture): Departure {
        var key = 0
        dbQuery {
            key = (Departures.insert {
                it[connectionId] = departure.connectionId
                it[time] = departure.time
                it[index] = departure.index
            } get Departures.id)
        }
        return getDepartureById(key)!!
    }

    suspend fun editDeparture(departure: NewDeparture, id: Int): Departure {
        dbQuery {
            Departures.update({ Departures.id eq id }) {
                it[connectionId] = departure.connectionId
                it[time] = departure.time
                it[index] = departure.index
            }
        }
        return getDepartureById(id)!!
    }

    suspend fun deleteDepartureById(id: Int): Boolean {
        var numOfDeletedItems = 0
        dbQuery {
            numOfDeletedItems = Departures.deleteWhere { Departures.id eq id }
        }
        return numOfDeletedItems == 1
    }

    private fun Query.adjustJoinToPackets() = run {
        adjustColumnSet { innerJoin(Connections) }
        adjustColumnSet { innerJoin(Routes) }
        adjustColumnSet { innerJoin(Lines) }
        adjustColumnSet { innerJoin(Packets) }
    }

    private fun Query.adjustJoinToStops() = run {
        adjustColumnSet { innerJoin(RouteStops) }
    }

    private fun Query.adjustJoinToRules() = run {
        adjustColumnSet { innerJoin(ConnectionRules) }
    }


    private fun ResultRow.toDeparture() =
        Departure(
            id = this[Departures.id],
            connectionId = this[Departures.connectionId],
            time = this[Departures.time],
            index = this[Departures.index]
        )

    /*

    suspend fun get(time: LocalTime, stopId: Int, date: LocalDate) = dbQuery {
        (Departures innerJoin Connections innerJoin Routes innerJoin RouteStops innerJoin Lines innerJoin Packets innerJoin ConnectionRules)
            .slice(Lines.shortCode, Departures.time, Routes.length, Routes.id)
            .select {
                (Packets.valid eq true)
                    .and (Packets.from lessEq date)
                    .and (Packets.to greaterEq date)
                    .and (Departures.time greater time)
                    .and (RouteStops.stopId eq stopId)
                    .and (RouteStops.index eq Departures.index)
                    .and (ConnectionRules.ruleId eq getRule(date))
            }
            .orderBy(Departures.time)
            .limit(10)
            .map { toDepartureItem(it) }
    }

    suspend fun getTimetable(stopId: Int, lineId: Int, directionId: Int, date: LocalDate) = dbQuery {
            val departures = (Departures innerJoin Connections innerJoin Routes innerJoin RouteStops innerJoin Lines innerJoin Packets innerJoin ConnectionRules)
                .slice(Departures.time, Routes.length, Routes.id)
                .select {
                    (Packets.valid eq true)
                        .and (Packets.from lessEq date)
                        .and (Packets.to greaterEq date)
                        .and (RouteStops.stopId eq stopId)
                        .and (Routes.direction eq directionId)
                        .and (Lines.id eq lineId)
                        .and (RouteStops.index eq Departures.index)
                        .and (ConnectionRules.ruleId eq getRule(date))
                }
                .orderBy(Departures.time)
                .mapNotNull { toDepartureSimple(it) }

        DepartureTimetable(
            date = date,
            lineShortCode = selectLineShortCodeByLineId(lineId),
            departures = departures
        )
    }

    private fun toDepartureItem(row: ResultRow) =
        DepartureWithLine(
            time = row[Departures.time],
            lineShortCode = row[Lines.shortCode],
            stopName = getLastStopName(row[Routes.id], row[Routes.length] - 1)
        )

    private fun getLastStopName(routeId: Int, index: Int) =
        (Stops innerJoin RouteStops)
            .slice(Stops.name)
            .select {
                (RouteStops.routeId eq routeId)
                    .and(RouteStops.index eq index)
            }
            .mapNotNull { it[Stops.name] }
            .single()

    private fun toDepartureSimple(row: ResultRow): DepartureSimple? {
        if(row[Departures.time] == null) return null
        return DepartureSimple(
            time = row[Departures.time]!!,
            stopName = getLastStopName(row[Routes.id], row[Routes.length] - 1)
        )
    }
     */
}


