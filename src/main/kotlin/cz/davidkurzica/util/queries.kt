package cz.davidkurzica.util


/*

fun selectStopsByRouteId(routeId: Int): List<Stop> {
    return Stops.innerJoin(RouteStops)
        .select {
            (RouteStops.routeId eq routeId)
        }.mapNotNull { toStop(it) }
}

fun selectPacketByPacketId(packetId: Int): Packet {
    return Packets.select {
        (Packets.id eq packetId )
    }.mapNotNull { toPacket(it) }
        .single()
}

fun selectRouteByRouteId(routeId: Int): Route {
    return Routes.select {
        (Routes.id eq routeId )
    }.mapNotNull { toRoute(it) }
        .single()
}


fun selectDeparturesByConnectionId(connectionId: Int): List<Departure> {
    return Departures
        .select {
            (Departures.connectionId eq connectionId)
        }.mapNotNull { toDeparture(it) }
}

fun selectRulesByConnectionId(connectionId: Int): List<Rule> {
    return (ConnectionRules innerJoin Rules)
        .slice(Rules.id, Rules.description)
        .select {
            (ConnectionRules.connectionId eq connectionId)
        }.mapNotNull { toRule(it) }
}


fun selectRoutesByLineId(id: Int): List<Route> {
    return Routes.select {
        (Routes.id eq id )
    }.mapNotNull { toRoute(it) }
}

fun selectLineShortCodeByLineId(id: Int): String {
    return Lines.select {
        (Lines.id eq id )
    }.mapNotNull { it[Lines.shortCode] }
        .single()
}

*/