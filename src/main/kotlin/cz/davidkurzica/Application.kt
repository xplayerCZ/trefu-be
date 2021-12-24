package cz.davidkurzica

import cz.davidkurzica.service.DatabaseFactory.initDatabase
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    initDatabase()

    routing {
        get("/") {
            call.respondText("Hi!")
        }
    }
}

