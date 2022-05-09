package cz.davidkurzica

import cz.davidkurzica.service.*
import cz.davidkurzica.web.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
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

    DatabaseFactory.initDatabase(this.environment.config)

    val connectionService = ConnectionService()
    val lineService = LineService()
    val packetService = PacketService()
    val stopService = StopService()
    val departureService = DepartureService()
    val routeService = RouteService()
    val ruleService = RuleService()

    install(Routing) {
        connection(connectionService)
        line(lineService)
        packet(packetService)
        stop(stopService)
        departure(departureService)
        route(routeService)
        rule(ruleService)
    }
}

