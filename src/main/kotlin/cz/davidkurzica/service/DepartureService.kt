package cz.davidkurzica.service

import cz.davidkurzica.db.dbQuery
import cz.davidkurzica.model.*
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

        query.mapNotNull { toDeparture(it) }
    }

    suspend fun getDepartureById(id: Int): Departure? = dbQuery {
        Departures.select {
            (Departures.id eq id)
        }.mapNotNull { toDeparture(it) }
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

    fun toDeparture(row: ResultRow) =
        Departure(
            id = row[Departures.id],
            connectionId = row[Departures.connectionId],
            time = row[Departures.time],
            index = row[Departures.index]
        )

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


