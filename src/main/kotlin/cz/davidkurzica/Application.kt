package cz.davidkurzica

import cz.davidkurzica.db.DatabaseFactory
import cz.davidkurzica.di.serviceModule
import cz.davidkurzica.web.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    install(CallLogging) {
        level = Level.INFO
        format { call ->
            val request = call.request
            val response = call.response
            "[${response.status()}] ${request.httpMethod.value} ${request.uri}"

        }
    }
    install(Resources)
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    install(Koin) {
        slf4jLogger()
        modules(serviceModule)
    }

    DatabaseFactory.initDatabase(this.environment.config)

    routing {
        connection()
        connectionRule()
        line()
        packet()
        stop()
        departure()
        route()
        routeStop()
        rule()
    }
}

