package cz.davidkurzica

import cz.davidkurzica.service.*
import cz.davidkurzica.web.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    install(CallLogging)
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    DatabaseFactory.initDatabase()

    val connectionService = ConnectionService()
    val lineService = LineService()
    val packetService = PacketService()
    val stopService = StopService()
    val departureService = DepartureService()
    val routeService = RouteService()

    install(Routing) {
        connection(connectionService)
        line(lineService)
        packet(packetService)
        stop(stopService)
        departure(departureService)
        route(routeService)
    }
}

