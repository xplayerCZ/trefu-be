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
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>): Unit = EngineMain.main(args)

val koinModule = module {
    single { ConnectionService() }
    single { LineService() }
    single { PacketService() }
    single { StopService() }
    single { DepartureService() }
    single { RouteService() }
    single { RuleService() }
}

fun Application.module() {
    install(CallLogging)
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
    install(Koin) {
        slf4jLogger()
        modules(koinModule)
    }

    DatabaseFactory.initDatabase(this.environment.config)

    routing {
        connection()
        line()
        packet()
        stop()
        departure()
        route()
        rule()
    }
}

