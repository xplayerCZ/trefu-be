package cz.davidkurzica.util

import cz.davidkurzica.model.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select

fun selectStopsByRouteId(routeId: Int): List<Stop> {
    return Stops.innerJoin(RouteStops)
        .select {
            (RouteStops.routeId eq routeId)
        }.mapNotNull { toStop(it) }
}

private fun toStop(row: ResultRow): Stop {
    return Stop(
        id = row[Stops.id],
        name = row[Stops.name],
        latitude = row[Stops.latitude],
        longitude = row[Stops.longitude],
        code = row[Stops.code]
    )
}

fun selectPacketByPacketId(packetId: Int): Packet {
    return Packets.select {
        (Packets.id eq packetId )
    }.mapNotNull { toPacket(it) }
        .single()
}

fun toPacket(row: ResultRow): Packet =
    Packet(
        id = row[Packets.id],
        from = row[Packets.from],
        to = row[Packets.to],
        valid = row[Packets.valid]
    )

fun selectRouteByRouteId(routeId: Int): Route {
    return Routes.select {
        (Routes.id eq routeId )
    }.mapNotNull { toRoute(it) }
        .single()
}


fun toRoute(row: ResultRow): Route {
    val routeId = row[Routes.id]

    return Route(
        id = routeId,
        length = row[Routes.length],
        direction = row[Routes.direction],
        stops = selectStopsByRouteId(routeId)
    )
}

fun selectDeparturesByConnectionId(connectionId: Int): List<Departure> {
    return Departures
        .select {
            (Departures.connectionId eq connectionId)
        }.mapNotNull { toDeparture(it) }
}

fun toDeparture(row: ResultRow): Departure =
    Departure(
        id = row[Departures.id],
        time = row[Departures.time],
        index = row[Departures.index]
    )

fun selectRulesByConnectionId(connectionId: Int): List<Rule> {
    return ConnectionRules
        .select {
            (ConnectionRules.connectionId eq connectionId)
        }.mapNotNull { toRule(it) }
}

fun toRule(row: ResultRow): Rule =
    Rule(
        row[Rules.id],
        row[Rules.description]
    )

fun toConnection(row: ResultRow): Connection =
    Connection(
        id = row[Connections.id],
        number = row[Connections.number],
        route = selectRouteByRouteId(row[Connections.routeId]),
        departures = selectDeparturesByConnectionId(row[Connections.id]),
        rules = selectRulesByConnectionId(row[Connections.id])
    )